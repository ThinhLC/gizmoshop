package com.gizmo.gizmoshop.dto.reponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderSupplierSummaryDTO {
    private Long id;
    private String orderCode;
    private String businessName;
    private Long totalPrice;
}
