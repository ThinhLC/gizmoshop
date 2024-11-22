package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
public class OrderRequest {
    private Long addressId;
    private Boolean paymentMethod;//0: Payment //1 : COD
    private Long walletId;
    private String note;
    private Long voucherId;
    private MultipartFile image;
    private Float oderAcreage;
    private Long totalPrice;
    private Float totalWeight;
    private int quantity;
}
