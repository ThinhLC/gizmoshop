package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageMappingResponse {

    private long id;

    private ProductResponse product;

    private ProductImageResponse image;
}
