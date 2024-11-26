package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ContractResponse {
    private long contractId;
    private String notes;
    private LocalDateTime start_date;
    private LocalDateTime expirationDate;
    private long contractMaintenanceFee;
}
