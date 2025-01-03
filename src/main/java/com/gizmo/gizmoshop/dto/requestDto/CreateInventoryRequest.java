package com.gizmo.gizmoshop.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInventoryRequest {
    private String inventoryName;
    private String city;
    private String district;
    private String commune;
    private String latitude;
    private String longitude;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
