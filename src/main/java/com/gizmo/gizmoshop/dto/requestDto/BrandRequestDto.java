package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Data;

@Data
public class BrandRequestDto {
    private String name;
    private String description;
    private Boolean deleted;
}
