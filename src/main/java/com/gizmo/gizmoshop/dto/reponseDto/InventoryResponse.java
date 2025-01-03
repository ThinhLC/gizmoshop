package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.excel.ExcludeFromExport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryResponse {
    private Long id;
    private String inventoryName;
    private String city;
    private String district;
    private String commune;
    private String latitude;
    private String longitude;
    private Boolean active;
    @ExcludeFromExport
    private LocalDateTime createdAt;
    @ExcludeFromExport
    private LocalDateTime updatedAt;

    public InventoryResponse(Long id, String inventoryName) {
        this.id = id;
        this.inventoryName = inventoryName;
    }
}
