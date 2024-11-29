package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.WalletAccountResponse;
import com.gizmo.gizmoshop.dto.requestDto.WalletAccountRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.WalletAccount;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.WalletAccountRepository;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletAccountService {
    @Autowired
    private WalletAccountRepository walletAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    private WalletAccountResponse convertToDto(WalletAccount walletAccount) {
        return WalletAccountResponse.builder()
                .id(walletAccount.getId())
                .bankName(walletAccount.getBank_name())
                .accountNumber(walletAccount.getAccount_number())
                .branch(walletAccount.getBranch())
                .swiftCode(walletAccount.getSwift_code())
                .createAt(walletAccount.getCreate_at())
                .updateAt(walletAccount.getUpdate_at())
                .accountId(walletAccount.getAccount().getId())
                .build();
    }
    public List<WalletAccountResponse> getAllWalletAccountsByAccount(UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        return walletAccounts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public WalletAccountResponse createWalletAccount(WalletAccountRequest walletAccountRequest, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));
        WalletAccount walletAccount = new WalletAccount();
        walletAccount.setBank_name(walletAccountRequest.getBankName());
        walletAccount.setAccount_number(walletAccountRequest.getAccountNumber());
        walletAccount.setBranch(walletAccountRequest.getBranch());
        walletAccount.setSwift_code(walletAccountRequest.getSwiftCode());
        walletAccount.setDeleted(false);
        walletAccount.setCreate_at(new Date());
        walletAccount.setUpdate_at(new Date());
        walletAccount.setAccount(account);
        walletAccountRepository.save(walletAccount);
        return convertToDto(walletAccount);
    }

    public void deleteWalletAccount(Long walletAccountId, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        WalletAccount walletAccount = walletAccountRepository.findById(walletAccountId)
                .orElseThrow(() -> new InvalidInputException("Tài khoản ví không tồn tại"));
        if (!walletAccount.getAccount().getId().equals(accountId)) {
            throw new InvalidInputException("Không có quyền xóa tài khoản ví này");
        }
        walletAccount.setDeleted(true);
        walletAccountRepository.save(walletAccount);
    }

    public WalletAccountResponse updateWalletAccount(Long walletAccountId, WalletAccountRequest updatedWalletAccountRequest, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        WalletAccount walletAccount = walletAccountRepository.findById(walletAccountId)
                .orElseThrow(() -> new InvalidInputException("Tài khoản ví không tồn tại"));

        if (!walletAccount.getAccount().getId().equals(accountId)) {
            throw new InvalidInputException("Không có quyền cập nhật tài khoản ví này");
        }

        // Cập nhật thông tin mới từ updatedWalletAccountRequest
        walletAccount.setBank_name(updatedWalletAccountRequest.getBankName());
        walletAccount.setAccount_number(updatedWalletAccountRequest.getAccountNumber());
        walletAccount.setBranch(updatedWalletAccountRequest.getBranch());
        walletAccount.setSwift_code(updatedWalletAccountRequest.getSwiftCode());
        walletAccount.setUpdate_at(new Date());

        walletAccountRepository.save(walletAccount);

        return convertToDto(walletAccount);
    }
}
