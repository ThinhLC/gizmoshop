package com.gizmo.gizmoshop.model;

public enum SupplierOrderStatus {

    // Nhóm trạng thái đơn hàng mới và đang chờ xử lý
    WAITING_FOR_SUPPLIER_CONFIRMATION(9, "Đơn hàng đang chờ nhà cung cấp xác nhận"),
    PENDING_PARTNER_APPROVAL(26, "Đơn hàng của đối tác đang chờ được xét duyệt"),
    WAITING_FOR_EMPLOYEE_APPROVAL(27, "Đơn hàng đang chờ nhân viên xét duyệt"),

    // Nhóm trạng thái đơn hàng đã được chấp nhận và đang xử lý
    ORDER_ACCEPTED(10, "Đơn hàng đã được chấp nhận"),
    PREPARING_ORDER(18, "Đơn hàng đang được chuẩn bị"),
    WAITING_FOR_DELIVERY_PARTNER(20, "Đơn hàng đang chờ đối tác giao hàng"),

    // Nhóm trạng thái đơn hàng gặp vấn đề
    OUT_OF_STOCK(11, "Đơn hàng đang hết sản phẩm"),
    WAITING_FOR_EXTENSION(12, "Đơn hàng đang chờ được gia hạn"),
    REJECTED_BY_SUPPLIER(19, "Đơn hàng bị từ chối bởi nhà cung cấp"),
    RETURNED_TO_SUPPLIER(21, "Đơn hàng bị hoàn trả cho nhà cung cấp");

    private final int id;
    private final String status;

    SupplierOrderStatus(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    // Phương thức tiện ích để lấy enum từ id
    public static SupplierOrderStatus fromId(int id) {
        for (SupplierOrderStatus status : SupplierOrderStatus.values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái với id: " + id);
    }
}
