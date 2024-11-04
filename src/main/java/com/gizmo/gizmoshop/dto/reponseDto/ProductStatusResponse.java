package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.StatusProduct;
import lombok.*;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusResponse {
    private Long id;

    private String name;

    public ProductStatusResponse(StatusProduct status) {
    }
}
