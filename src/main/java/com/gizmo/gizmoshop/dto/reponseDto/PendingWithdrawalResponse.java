package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingWithdrawalResponse {
    private Long id;
    private Long amount;
    private String auth;
    private Date createAt;
}
