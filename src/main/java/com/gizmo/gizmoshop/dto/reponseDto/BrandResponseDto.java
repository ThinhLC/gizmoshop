package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.ProductBrand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;




    public BrandResponseDto(ProductBrand brand) {
        if (brand != null) {
            this.id = brand.getId();
            this.name = brand.getName();
            this.description = brand.getDescription();
            this.deleted = brand.getDeleted();
        }
    }
}
