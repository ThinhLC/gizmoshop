package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class StatusDto {
    private Long id;
    private String name;
}
