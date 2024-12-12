package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingWithdrawalResponse {
    private Long id;
    private Long amount;
    private String auth;
}
