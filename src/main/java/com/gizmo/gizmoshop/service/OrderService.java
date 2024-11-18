package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class OrderService {
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherToOrderRepository voucherToOrderRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private WithdrawalHistoryRepository withdrawalHistoryRepository;
    @Autowired
    private GenericExporter<VoucherResponse> genericExporter;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public Page<OrderResponse> findOrdersByUserIdAndStatusAndDateRange(
            Long userId, Long idStatus, Date startDate, Date endDate, Pageable pageable) {
        return orderRepository.findOrdersByUserIdAndStatusAndDateRange(userId, idStatus, startDate, endDate, pageable)
                .map(this::convertToOrderResponse);
    }

    public Page<OrderResponse> findOrdersByALlWithStatusRoleAndDateRange(
            Long idStatus, Boolean roleStatus, Date startDate, Date endDate, Pageable pageable) {
        System.err.println("trạng thái của status:" + roleStatus);
        return orderRepository.findOrdersByALlWithStatusRoleAndDateRange(idStatus, roleStatus, startDate, endDate, pageable)
                .map(this::convertToOrderResponse);
    }


    public OrderSummaryResponse totalCountOrderAndPrice(
            Long userId, Long idStatus, Date startDate, Date endDate) {
        List<Order> orders = orderRepository.totalOrder(userId, idStatus, startDate, endDate);
        long count = 0;
        long sumPrice = 0;
        for (Order order: orders) {
           count++;
           sumPrice+= order.getTotalPrice();
        }
        return OrderSummaryResponse.builder()
                .totalQuantityOrder(count)
                .totalAmountOrder(sumPrice)
                .build();
    }


    public OrderResponse getOrderByPhoneAndOrderCode(String phoneNumber, String orderCode) {
        // Tìm đơn hàng theo orderCode và sdt từ AddressAccount
        Optional<Order> orderOpt = orderRepository.findByOrderCodeAndAddressAccount_Sdt(orderCode, phoneNumber);

        if (!orderOpt.isPresent()) {
            throw new InvalidInputException("Không tìm thấy đơn hàng với orderCode và số điện thoại này.");
        }

        Order order = orderOpt.get();
        return convertToOrderResponse(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderDetail> orderDetailsList = orderDetailRepository.findByIdOrder(order);

        Optional<VoucherToOrder> optionalVoucherOrder = voucherToOrderRepository.findByOrderId(order.getId());

        return OrderResponse.builder()
                .id(order.getId())
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
                .orderDetails(orderDetailsList.stream().map(orderDetail -> OrderDetailsResponse.builder()
                        .id(orderDetail.getId())
                        .price(orderDetail.getPrice())
                        .quantity(orderDetail.getQuantity())
                        .accept(orderDetail.getAccept())
                        .total(orderDetail.getPrice() * orderDetail.getQuantity())
                        .product(ProductResponse.builder()
                                .id(orderDetail.getIdProduct().getId())
                                .productName(orderDetail.getIdProduct().getName())
                                .productImageMappingResponse(orderDetail.getIdProduct().getProductImageMappings().stream()
                                        .map(imageMapping -> new ProductImageMappingResponse(imageMapping)) // Chuyển từ ProductImageMapping sang ProductImageMappingResponse
                                        .collect(Collectors.toList()))// Thu thập thành List
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

   public String cancelOrderForUsers(long idOrder, String note){
        Optional <Order> order = orderRepository.findById(idOrder);
       if (!order.isPresent()) {
           throw new InvalidInputException("Không tìm thấy đơn hàng ");
       }
       //kiem tra donhang hien tai co phai Đơn hàng đang chờ xét duyệt khong
       if(order.get().getOrderStatus().getId()!=1L){
           throw new InvalidInputException("Đơn hàng không thể hủy vì đã được xác nhận bởi nhân viên");
       }
       //25 ,là Đơn hàng của người dùng đã hủy thành công
       Optional <OrderStatus> statusCancel=orderStatusRepository.findById(25L);
       if (!statusCancel.isPresent()) {
           throw new InvalidInputException("Không tìm trạng thái Đơn hàng đã hủy và đang đợi xét duyệt (24L) ");
       }
       order.get().setOrderStatus(statusCancel.get());
       order.get().setNote(note);
//       kiểm tra để lưu và bảng lịch sử giao dịch

       if(!order.get().getPaymentMethods()){
           //thanh toan online
           WithdrawalHistory gd = new WithdrawalHistory();

           gd.setWalletAccount(order.get().getIdWallet());
           gd.setAccount(order.get().getIdAccount());
           gd.setAmount(order.get().getTotalPrice());
           gd.setWithdrawalDate(new Date());
           gd.setNote("CUSTOMER|"+note+"|"+"PENDING");

           withdrawalHistoryRepository.save(gd);
       }
       orderRepository.save(order.get());
       return statusCancel.get().getStatus();
   }


}
