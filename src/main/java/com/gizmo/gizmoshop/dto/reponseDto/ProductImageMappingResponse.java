package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.ProductImage;
import com.gizmo.gizmoshop.entity.ProductImageMapping;
import lombok.*;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageMappingResponse {
    private long id;
    private Long idProduct;
    private List<ProductImageResponse> image;

    public ProductImageMappingResponse(ProductImageMapping productImageMapping) {
    }
}
