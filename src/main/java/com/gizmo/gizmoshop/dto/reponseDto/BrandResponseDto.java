package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.ProductBrand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;

    public BrandResponseDto(ProductBrand brand) {
    }
}
