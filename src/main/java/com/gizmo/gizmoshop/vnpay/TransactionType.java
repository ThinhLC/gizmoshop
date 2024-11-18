package com.gizmo.gizmoshop.vnpay;

public enum TransactionType {
    ORDER_PAYMENT("order_payment"),    // Thanh toán cho đơn hàng
    ACCOUNT_TOPUP("account_topup");    // Nộp tiền vào tài khoản

    private final String type;

    TransactionType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
