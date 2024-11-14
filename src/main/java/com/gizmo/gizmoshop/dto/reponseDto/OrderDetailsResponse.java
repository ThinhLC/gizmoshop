package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailsResponse {
    private Long id;
    private ProductResponse product;
    private Long price;
    private Long quantity;
    private Boolean accept;
    private Long total;

    public OrderDetailsResponse(Long id, ProductResponse product, Long price, Long quantity, Long total) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }
}
