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

    private ProductResponse product;

    private InventoryResponse inventory;

    private Integer quantity;


    public ProductInventoryResponse(Long id, Long idProduct, Long idInventory, Integer quantity) {
    }
}
