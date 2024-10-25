package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.ProductImage;
import lombok.Data;

@Data
public class ProductImageMappingResponse {

    private long id;

    private ProductResponse product;

    private ProductImage image;
}
