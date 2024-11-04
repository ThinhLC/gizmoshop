package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.excel.ExcludeFromExport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherResponse {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minimumOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean status;
    @ExcludeFromExport
    private LocalDateTime createdAt;
    @ExcludeFromExport
    private LocalDateTime updatedAt;
    @ExcludeFromExport
    private String image;
    private List<OrderResponse> orderResponses;

}
