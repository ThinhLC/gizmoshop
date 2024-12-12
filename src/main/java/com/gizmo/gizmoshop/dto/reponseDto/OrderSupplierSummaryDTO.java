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
    private String orderCode;
    private String nameAccount;
    private boolean isSupplier;
//    private long statusOder;
    private Date createDateOrder;

}
