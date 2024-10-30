package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

@Data
@Getter
@Setter
public class ProductImageMappingResponse {
    private Long idProduct;
    private Long idProductImage;
    private String imageUri;

    public ProductImageMappingResponse(Long idProduct, Long idProductImage, String imageUri) {
        this.idProduct = idProduct;
        this.idProductImage = idProductImage;
        this.imageUri = imageUri;
    }
}
