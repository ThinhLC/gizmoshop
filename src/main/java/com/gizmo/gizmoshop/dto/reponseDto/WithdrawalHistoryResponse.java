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
    private String note; //đối tác rút về vấn đề gì, Trạng thái của giao dịch này,
                        //khách hàng cũng tương tự
    private String auth;
    private String status;
    private AccountResponse account;
    private WalletAccountResponse walletAccount;

}
