package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class BrandStatisticsDto {
    private long id;
    private String name;
    private Boolean active;
    private int quantity ;
    private int quantityActive;
}
