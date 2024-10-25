package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductInventoryResponse {
    private Long id;

    private Product product;

    private Inventory inventory;

    private Integer quantity;
}
