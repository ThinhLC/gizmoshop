package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductInventoryResponse {
    private Long id;

    private ProductResponse product;

    private InventoryResponse inventory;

    private Integer quantity;
}
