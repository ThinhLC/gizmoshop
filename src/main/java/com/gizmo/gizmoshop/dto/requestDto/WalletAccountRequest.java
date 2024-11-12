package com.gizmo.gizmoshop.dto.requestDto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletAccountRequest {
    private String bankName;
    private String accountNumber;
    private String branch;
    private String swiftCode;
}
