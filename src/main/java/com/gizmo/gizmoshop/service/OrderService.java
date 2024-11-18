package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service

public class OrderService {
    @Autowired
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private VoucherToOrderRepository voucherToOrderRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
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
    private OrderStatusRepository orderStatusRepository;
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
        for (Order order : orders) {
            count++;
            sumPrice += order.getTotalPrice();
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
    @Transactional
    public void placeOrder(Long accountId, OrderRequest orderRequest) {
        // Kiểm tra xem giỏ hàng có tồn tại hay không
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng không có sản phẩm");
        }

        long totalAmount = 0;
        float totalWeight = 0.0f;

        for (CartItems cartItem : cart.getItems()) {
            if (cartItem.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ trong giỏ hàng");
            }

            Product product = productRepository.findById(cartItem.getProductId().getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            ProductInventory productInventory = product.getProductInventory();

            // Kiểm tra tồn kho
            if (productInventory == null || productInventory.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Sản phẩm không đủ trong kho");
            }

            totalAmount += cartItem.getQuantity() * product.getPrice();

            totalWeight += cartItem.getQuantity() * product.getWeight();
        }

        // Lấy các giá trị từ OrderRequest thay vì từ tham số riêng lẻ
        Long addressId = orderRequest.getAddressId();
        Boolean paymentMethod = orderRequest.getPaymentMethod();
        Long walletId = orderRequest.getWalletId();
        String note = orderRequest.getNote();

        // Kiểm tra địa chỉ giao hàng
        AddressAccount address = addressAccountRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ giao hàng không tồn tại"));

        // Kiểm tra ví
        WalletAccount wallet = walletAccountRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Ví không tồn tại"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        OrderStatus orderStatus = orderStatusRepository.findByStatus("Đơn hàng đang chờ xét duyệt")
                .orElseThrow(() -> new RuntimeException("Trạng thái đơn hàng không tồn tại"));

        // Tạo mã đơn hàng ngẫu nhiên
        String orderCode = generateOrderCode(accountId);

        // Tạo đơn hàng
        Order order = new Order();
        order.setIdAccount(account); // Đặt lại ID tài khoản đúng
        order.setAddressAccount(address);
        order.setPaymentMethods(paymentMethod); // false cho trả tiền khi nhận, true cho trả online
        order.setIdWallet(wallet);
        order.setNote(note);
        order.setTotalWeight(totalWeight);
        order.setOrderCode(orderCode);
        order.setCreateOderTime(new Date());
        order.setTotalPrice(totalAmount);
        order.setOrderStatus(orderStatus); // Thêm tổng tiền của đơn hàng
        orderRepository.save(order);

        // Tạo OrderDetails từ các sản phẩm trong giỏ hàng
        for (CartItems cartItem : cart.getItems()) {
            OrderDetail orderDetails = new OrderDetail();
            orderDetails.setIdOrder(order);
            orderDetails.setIdProduct(cartItem.getProductId());
            orderDetails.setPrice(cartItem.getProductId().getPrice());
            orderDetails.setQuantity(cartItem.getQuantity());
            orderDetails.setTotal(cartItem.getQuantity() * cartItem.getProductId().getPrice());
            orderDetails.setAccept(false);  // Đơn hàng chưa được xét duyệt
            orderDetailRepository.save(orderDetails);

            // Giảm số lượng sản phẩm trong kho
            ProductInventory productInventory = cartItem.getProductId().getProductInventory();
            if (productInventory != null) {
                Integer currentQuantity = Integer.valueOf(productInventory.getQuantity().intValue());
                Integer cartItemQuantity = Integer.valueOf(cartItem.getQuantity().intValue());

                // Kiểm tra và trừ đi số lượng
                Integer updatedQuantity = currentQuantity - cartItemQuantity;

                productInventory.setQuantity(updatedQuantity);
                productInventoryRepository.save(productInventory);
            }
        }

        cartItemsRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(0L); // Set lại giá trị TotalPrice
        cartRepository.save(cart);
    }

    private String generateOrderCode(Long accountId) {
        // Sinh mã đơn hàng ngẫu nhiên theo định dạng: ORD_ddMMyyyy_accountId
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("ddMMyyyy")); // ddMMyyyy

        // Sinh 4 số ngẫu nhiên
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); // Tạo số ngẫu nhiên trong khoảng 1000 đến 9999

        // Tạo mã đơn hàng theo định dạng yêu cầu
        return "ORD " + datePart + "_" + randomNumber + "_" + accountId;
    }

}
