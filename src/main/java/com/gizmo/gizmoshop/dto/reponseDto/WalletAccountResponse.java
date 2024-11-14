package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletAccountResponse {
    private Long id;
    private String bankName;
    private String accountNumber;
    private String branch;
    private String swiftCode;
    private Date createAt;
    private Date updateAt;
    private Long accountId;
}
