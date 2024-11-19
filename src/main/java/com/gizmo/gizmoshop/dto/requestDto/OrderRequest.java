package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long addressId;
    private Boolean paymentMethod;
    private Long walletId;
    private String note;
    private Long voucherId;
}
