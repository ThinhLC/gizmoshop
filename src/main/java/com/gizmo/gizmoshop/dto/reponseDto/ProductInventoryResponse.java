package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInventoryResponse {
    private Long id;

    private Long idProduct;

    private Long ProductInventory;

    private Integer quantity;
}
