package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;

}
