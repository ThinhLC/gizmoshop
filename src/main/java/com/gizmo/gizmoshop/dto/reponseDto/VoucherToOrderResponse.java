package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherToOrderResponse {
    private Long id;
    private Long voucherId;
    private Long orderId;
    private LocalDateTime usedAt;
    private VoucherResponse voucher;
}

