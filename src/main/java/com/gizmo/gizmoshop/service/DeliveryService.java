package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final OrderRepository orderRepository;
    @Autowired
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private SuppilerInfoRepository suppilerInfoRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherToOrderRepository voucherToOrderRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private WithdrawalHistoryRepository withdrawalHistoryRepository;
    @Autowired
    private GenericExporter<VoucherResponse> genericExporter;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AddressAccountRepository addressAccountRepository;
    @Autowired
    private WalletAccountRepository walletAccountRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private  CartService cartService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ShipperInforRepository shipperInforRepository;
    @Autowired
    private ShipperOrderRepository shipperOrderRepository;


    public Page<OrderResponse> getAllOrderForDelivery(String keyword, Date startDate, Date endDate, String type, Pageable pageable) {
        boolean roleStatus = type != null && type.contains("ORDER_CUSTOMER") ? false : true;
        // ORDER_CUSTOMER : ORDER_SUPPLIER
        return orderRepository.findAllOrderByTypeAndDateAndKeyword(startDate, endDate, roleStatus, keyword, pageable)
                .map(this::convertToOrderResponse);
    }
    @Transactional
    public void assignOrderToShipper(Long orderId, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new InvalidInputException("Tài khoản không tồn tại"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidInputException("Đơn hàng không tồn tại"));
        ShipperInfor shipper = shipperInforRepository.findByAccountId(account)
                .orElseThrow(() -> new InvalidInputException("Nhân viên giao hàng không tồn tại"));

        Optional<ShipperOrder> shipperOrderOrCurrent= shipperOrderRepository.findByOrderIdAndShipperInforId(order,shipper);
        if(shipperOrderOrCurrent.isPresent()){
            throw new InvalidInputException("Đơn hàng đã được nhận bởi bạn nhân viên khác");
        }
        // Set order + shipper
        ShipperOrder shipperOrder = new ShipperOrder();
        shipperOrder.setOrderId(order);
        shipperOrder.setShipperInforId(shipper);
        shipperOrderRepository.save(shipperOrder);

        OrderStatus assignedStatus = null ;
        if(!order.getOrderStatus().getRoleStatus()){
            assignedStatus = orderStatusRepository.findById(15L)  //Nhận đơn cho người dùng
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của người dùng không tồn tại"));
        }else {
            assignedStatus = orderStatusRepository.findById(29L)  //Nhận đơn nhà cung cấp
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của nhà cung cấp không tồn tại"));
        }
        order.setOrderStatus(assignedStatus);
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, String note, Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new InvalidInputException("Tài khoản không tồn tại"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidInputException("Đơn hàng không tồn tại"));
        ShipperInfor shipper = shipperInforRepository.findByAccountId(account)
                .orElseThrow(() -> new InvalidInputException("Nhân viên giao hàng không tồn tại"));

        ShipperOrder shipperOrder= shipperOrderRepository.findByOrderIdAndShipperInforId(order,shipper).orElseThrow(() -> new InvalidInputException("Không tìm thấy record cho nhân viên giao hàng và đơn hàng đó"));
        shipperOrderRepository.delete(shipperOrder);
        order.setNote("_Đơn hàng đã bị từ chối từ nhân viên giao hàng với lý do : "+ note + "_ Thông tin cũ :  "+order.getNote());
        OrderStatus assignedStatus = null ;
        if(!order.getOrderStatus().getRoleStatus()){
            assignedStatus = orderStatusRepository.findById(4L)  // đơn cho người dùng
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của người dùng không tồn tại"));
        }else {
            assignedStatus = orderStatusRepository.findById(28L)  // đơn nhà cung cấp
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của nhà cung cấp không tồn tại"));
        }
        order.setOrderStatus(assignedStatus);
        orderRepository.save(order);
    }
    @Transactional
    public void confirmDelivery(Long orderId,String deliveryNote,Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(() ->
                new InvalidInputException("Tài khoản không tồn tại"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidInputException("Đơn hàng không tồn tại"));
        ShipperInfor shipper = shipperInforRepository.findByAccountId(account)
                .orElseThrow(() -> new InvalidInputException("Nhân viên giao hàng không tồn tại"));
        if(order.getOrderStatus().getId()==13 || order.getOrderStatus().getId()==20L){
            throw new InvalidInputException("Đơn hàng đã này đã giao không thể xác minh 2 lần nữa");
        }
        shipper.setTotalOrder(shipper.getTotalOrder() + 1);
        shipper.setCommission(shipper.getCommission() + 15000L);
        shipperInforRepository.save(shipper);

        Optional<ShipperOrder> shipperOrderOrCurrent= shipperOrderRepository.findByOrderIdAndShipperInforId(order,shipper);
        if(!shipperOrderOrCurrent.isPresent()){
            throw new InvalidInputException("Đơn hàng đã này không phải của bạn nên không thể xác nhận đã giao");
        }
        order.setNote("_Đơn hàng đã được giao thành công (ghi chú  : "+  deliveryNote+ ") Thông tin cũ :  "+order.getNote());
        OrderStatus assignedStatus = null ;
        if(!order.getOrderStatus().getRoleStatus()){
            assignedStatus = orderStatusRepository.findById(13L)  // đơn cho người dùng
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của người dùng không tồn tại"));
        }else {
            assignedStatus = orderStatusRepository.findById(20L)  // đơn nhà cung cấp
                    .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng của nhà cung cấp không tồn tại"));
        }
        order.setOrderStatus(assignedStatus);
        orderRepository.save(order);
    }

    //lấy toàn bộ đơn han của mình đã nhận và đơn hàng đã giao : DA_NHAN_VA_GIAO_THANH_CONG đã giao thành công , DA_NHAN đã nhận default là DA_NHAN
   public Page<OrderResponse> getAllReceivedOrders(String keyword,Date startDate,Long accountId,Date endDate,String type,Pageable pageable){
        boolean type1 = type.equals("DA_NHAN") ? true : false;
       Account account = accountRepository.findById(accountId).orElseThrow(() ->
               new InvalidInputException("Tài khoản không tồn tại"));
       ShipperInfor shipper = shipperInforRepository.findByAccountId(account)
               .orElseThrow(() -> new InvalidInputException("Nhân viên giao hàng không tồn tại"));
       return orderRepository.findAllOrderByShipperAndDateAndKeyword(startDate,endDate,shipper.getId(),keyword,type1,pageable)
               .map(this::convertToOrderResponse);
   }


    private OrderResponse convertToOrderResponse(Order order) {
        ContractResponse contractResponse = null;
        if (order.getContract() != null) {
            contractResponse = ContractResponse.builder()
                    .contractId(order.getContract().getId())
                    .notes(order.getContract().getNotes())
                    .contractMaintenanceFee(order.getContract().getContractMaintenanceFee())
                    .start_date(order.getContract().getStartDate())
                    .expirationDate(order.getContract().getExpireDate())
                    .build();
        }
        Optional<SupplierInfo> supplierInfoOptional = suppilerInfoRepository.findByAccount_Id(order.getIdAccount().getId());
        SupplierInfo supplierInfo = new SupplierInfo();
        if(supplierInfoOptional.isPresent()){
            supplierInfo = supplierInfoOptional.get();
        }
        List<OrderDetail> orderDetailsList = orderDetailRepository.findByIdOrder(order);
        Optional<VoucherToOrder> optionalVoucherOrder = voucherToOrderRepository.findByOrderId(order.getId());
        return OrderResponse.builder()

                .id(order.getId())
                .paymentMethods(order.getPaymentMethods())
                .account(AccountResponse.builder()
                        .id(order.getIdAccount().getId())
                        .fullname(order.getIdAccount().getFullname())
                        .build())
                .addressAccount(AddressAccountResponse.builder()
                        .fullname(order.getAddressAccount().getFullname())
                        .city(order.getAddressAccount().getCity())
                        .commune(order.getAddressAccount().getCommune())
                        .district(order.getAddressAccount().getDistrict())
                        .specificAddress(order.getAddressAccount().getSpecific_address())
                        .sdt(order.getAddressAccount().getSdt())
                        .build())
                .orderStatus(OrderStatusResponse.builder()
                        .id(order.getOrderStatus().getId())
                        .status(order.getOrderStatus().getStatus())
                        .roleStatus(order.getOrderStatus().getRoleStatus())
                        .build())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .totalWeight(order.getTotalWeight())
                .orderCode(order.getOrderCode())
                .createOderTime(order.getCreateOderTime())
                .supplierDto(SupplierDto.builder()
                        .tax_code(supplierInfo.getTaxCode())
                        .nameSupplier(supplierInfo.getBusiness_name())
                        .Id(supplierInfo.getId())
                        .description(supplierInfo.getDescription())
                        .build())
                .orderDetails(orderDetailsList.stream().map(orderDetail -> OrderDetailsResponse.builder()
                        .id(orderDetail.getId())
                        .price(orderDetail.getPrice())
                        .quantity(orderDetail.getQuantity())
                        .accept(orderDetail.getAccept())
                        .total(orderDetail.getPrice() * orderDetail.getQuantity())
                        .product(ProductResponse.builder()
                                .id(orderDetail.getIdProduct().getId())
                                .discountProduct(orderDetail.getIdProduct().getDiscountProduct())
                                .productInventoryResponse(buildProductInventoryResponse(orderDetail.getIdProduct().getProductInventory()))
                                .productName(orderDetail.getIdProduct().getName())
                                .productImageMappingResponse(orderDetail.getIdProduct().getProductImageMappings().stream()
                                        .map(imageMapping -> new ProductImageMappingResponse(imageMapping)) // Chuyển từ ProductImageMapping sang ProductImageMappingResponse
                                        .collect(Collectors.toList()))
                                .productPrice(orderDetail.getIdProduct().getPrice())
                                .thumbnail(orderDetail.getIdProduct().getThumbnail())
                                .productLongDescription(orderDetail.getIdProduct().getLongDescription())
                                .productShortDescription(orderDetail.getIdProduct().getShortDescription())
                                .productWeight(orderDetail.getIdProduct().getWeight())
                                .productArea(orderDetail.getIdProduct().getArea())
                                .productVolume(orderDetail.getIdProduct().getVolume())
                                .productHeight(orderDetail.getIdProduct().getHeight())
                                .productLength(orderDetail.getIdProduct().getLength())
                                .build())
                        .build()).collect(Collectors.toList()))
                .contractresponse(contractResponse)
                .vouchers(optionalVoucherOrder.stream().map(voucherOrder -> VoucherToOrderResponse.builder()
                        .id(voucherOrder.getId())
                        .voucherId(voucherOrder.getVoucher().getId())
                        .orderId(order.getId())
                        .usedAt(voucherOrder.getUsedAt())
                        .voucher(VoucherResponse.builder()
                                .id(voucherOrder.getVoucher().getId())
                                .code(voucherOrder.getVoucher().getCode())
                                .description(voucherOrder.getVoucher().getDescription())
                                .discountAmount(voucherOrder.getVoucher().getDiscountAmount())
                                .discountPercent(voucherOrder.getVoucher().getDiscountPercent())
                                .maxDiscountAmount(voucherOrder.getVoucher().getMaxDiscountAmount())
                                .minimumOrderValue(voucherOrder.getVoucher().getMinimumOrderValue())
                                .validFrom(voucherOrder.getVoucher().getValidFrom())
                                .validTo(voucherOrder.getVoucher().getValidTo())
                                .usageLimit(voucherOrder.getVoucher().getUsageLimit())
                                .usedCount(voucherOrder.getVoucher().getUsedCount())
                                .status(voucherOrder.getVoucher().getStatus())
                                .build())
                        .build()).collect(Collectors.toList()))
                .build();
    }
    private ProductInventoryResponse buildProductInventoryResponse(ProductInventory inventory) {
        if (inventory == null) return null;

        return ProductInventoryResponse.builder()
                .id(inventory.getId())
                .quantity(inventory.getQuantity())
                .build();
    }

}
