package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStatsDTO {
    private Long id;
    private String name;
    private List<ProductInventoryResponse> productInventory;
}