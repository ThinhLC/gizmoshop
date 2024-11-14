package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalHistoryResponse {
    private Long id;
    private Long amount;
    private Date withdrawalDate;
    private Long walletAccountId;
    private Long accountId;
}
