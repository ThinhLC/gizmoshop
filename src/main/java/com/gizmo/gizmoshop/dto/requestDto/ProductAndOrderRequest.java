package com.gizmo.gizmoshop.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndOrderRequest {
    private CreateProductRequest createProductRequest;
    private OrderRequest orderRequest;
}
