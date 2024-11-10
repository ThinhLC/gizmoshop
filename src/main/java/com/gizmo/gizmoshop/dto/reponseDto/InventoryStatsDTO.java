package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.excel.ExcludeFromExport;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStatsDTO {
    private Long id;
    private String name;
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
    private List<ProductInventoryResponse> productInventory;
}