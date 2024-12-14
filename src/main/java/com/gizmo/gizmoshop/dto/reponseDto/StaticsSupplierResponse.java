package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.Date;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaticsSupplierResponse {
    private long quantityBr;
    private long quantityCC;
    private long quantityTK;
    private Date startDate;
    private Date endDate;
    private ProductResponse product;
}
