package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusResponse {
    private Long id;

    private String name;
}
