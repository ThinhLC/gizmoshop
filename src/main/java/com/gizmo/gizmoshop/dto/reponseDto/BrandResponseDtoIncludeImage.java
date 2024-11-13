package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BrandResponseDtoIncludeImage {
    private Long id;
    private String name;
    private String description;
    private boolean deleted;
    private String image;
}
