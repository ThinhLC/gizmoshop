package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CategoryStatisticsDto {
    private long id;
    private String name;
    private boolean active;
    private int quantity ;
    private int quantityActive;
}


