package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest {
    private Long Id;
    private String nameSupplier;
    private String tax_code;
    private Long balance;
    private Long frozen_balance;
    private String description;
}
