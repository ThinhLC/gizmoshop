package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;

}
