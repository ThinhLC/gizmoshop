package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SupplierDto {
    private Long Id;
    private String nameSupplier;
    private String tax_code;
    private Long balance;
    private Long frozen_balance;
    private String description;
    private boolean deleted;
    private long wallet;
}
