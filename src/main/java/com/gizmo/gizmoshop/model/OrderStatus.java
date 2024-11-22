package com.gizmo.gizmoshop.model;

public enum OrderStatus {

    // Nhóm trạng thái chung
    WAITING_FOR_APPROVAL(1, "Đơn hàng đang chờ xét duyệt"),
    DELIVERY_FAILED(3, "Đơn hàng giao thất bại"),
    ORDER_CANCELLED(4, "Đơn hàng bị hủy"),
    ORDER_RECALLING(5, "Đơn hàng đang được thu hồi"),
    ORDER_RETURNING(6, "Đơn hàng đang được hoàn trả"),
    INVALID_ORDER(7, "Đơn hàng không đúng quy định"),
    ORDER_FROZEN(8, "Đơn hàng đang bị khóa (đóng băng)"),
    USER_CANCELLED_SUCCESSFULLY(25, "Đơn hàng của người dùng đã hủy thành công"),
    LOST_DURING_SHIPMENT(22, "Đơn hàng bị mất trong quá trình giao"),

    // Nhóm trạng thái thanh toán
    WAITING_FOR_PAYMENT(14, "Đơn hàng đang chờ thanh toán"),
    PAYMENT_FAILED(15, "Đơn hàng thanh toán thất bại"),
    PAYMENT_COMPLETED(16, "Đơn hàng đã thanh toán"),

    // Nhóm trạng thái giao hàng
    DELIVERED_SUCCESSFULLY(13, "Đơn hàng đã được giao thành công"),

    // Nhóm trạng thái bị từ chối
    ORDER_REJECTED(17, "Đơn hàng bị từ chối"),

    // Nhóm trạng thái liên quan đến nhà cung cấp (đã được định nghĩa trong SupplierOrderStatus)
    // Có thể tham chiếu hoặc tái sử dụng nếu cần

    ;

    private final int id;
    private final String status;

    OrderStatus(int id, String status) {
        this.id = id;
        this.status = status;
    }

    // Các phương thức tiện ích tương tự
}
