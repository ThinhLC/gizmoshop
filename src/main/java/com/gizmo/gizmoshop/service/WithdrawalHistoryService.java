package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.WithdrawalHistoryResponse;
import com.gizmo.gizmoshop.entity.WalletAccount;
import com.gizmo.gizmoshop.entity.WithdrawalHistory;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.WalletAccountRepository;
import com.gizmo.gizmoshop.repository.WithdrawalHistoryRepository;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WithdrawalHistoryService {

    @Autowired
    private WithdrawalHistoryRepository withdrawalHistoryRepository;

    @Autowired
    private WalletAccountRepository walletAccountRepository;

    public List<WithdrawalHistoryResponse> getWithdrawalHistoryByAccount(Long accountId, UserPrincipal userPrincipal) {
        Long userAccountId = userPrincipal.getUserId();

        if (!userAccountId.equals(accountId)) {
            throw new InvalidInputException("Không có quyền truy cập dữ liệu của tài khoản này.");
        }
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        if (walletAccounts.isEmpty()) {
            throw new InvalidInputException("Không tìm thấy ví cho tài khoản này.");
        }
        System.out.println("Found " + walletAccounts.size() + " wallet accounts for accountId: " + accountId);
        List<WithdrawalHistory> histories = withdrawalHistoryRepository.findByWalletAccountIn(walletAccounts);
        System.out.println("Found " + histories.size() + " withdrawal histories.");

        return histories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<WithdrawalHistoryResponse> getWithdrawalHistoryByAccountAndDateRange(
            Long accountId, Date startDate, Date endDate, UserPrincipal userPrincipal) {

        Long userAccountId = userPrincipal.getUserId();

        if (!userAccountId.equals(accountId)) {
            throw new InvalidInputException("Không có quyền truy cập dữ liệu của tài khoản này.");
        }
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);

        if (startDate == null) startDate = new Date(0);
        if (endDate == null) endDate = new Date();
        List<WithdrawalHistory> histories = withdrawalHistoryRepository.findByWalletAccountInAndWithdrawalDateBetween(
                walletAccounts, startDate, endDate);

        return histories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private WithdrawalHistoryResponse convertToDto(WithdrawalHistory history) {
        return WithdrawalHistoryResponse.builder()
                .id(history.getId())
                .amount(history.getAmount())
                .withdrawalDate(history.getWithdrawalDate())
                .walletAccountId(history.getWalletAccount().getId())
                .accountId(history.getAccount().getId())
                .build();
    }
}