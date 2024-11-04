package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderStatusResponse {
    private Long id;
    private String status;
    private Boolean roleStatus;

    public OrderStatusResponse(Long id, String status) {
        this.id = id;
        this.status = status;
    }
}
