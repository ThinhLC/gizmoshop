package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WalletAccountResponse;
import com.gizmo.gizmoshop.dto.requestDto.WalletAccountRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WalletAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/walletaccount")
@RequiredArgsConstructor
@CrossOrigin("*")
public class WalletApi {
    @Autowired
    private WalletAccountService walletAccountService;

    @GetMapping("/getall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<List<WalletAccountResponse>>> getWalletAccountsByCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WalletAccountResponse> walletAccounts = walletAccountService.getAllWalletAccountsByAccount(userPrincipal);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách tài khoản ví thành công", walletAccounts));
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<WalletAccountResponse>> createWalletAccount(
            @RequestBody WalletAccountRequest walletAccountRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        WalletAccountResponse createdWalletAccount = walletAccountService.createWalletAccount(walletAccountRequest, userPrincipal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper<>(HttpStatus.CREATED, "Thêm tài khoản ví thành công", createdWalletAccount));
    }

    @PutMapping("/update/{walletAccountId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<WalletAccountResponse>> updateWalletAccount(
            @PathVariable Long walletAccountId,
            @RequestBody WalletAccountRequest updatedWalletAccountRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        WalletAccountResponse updatedWalletAccount = walletAccountService.updateWalletAccount(
                walletAccountId,
                updatedWalletAccountRequest,
                userPrincipal
        );
        return ResponseEntity.ok(new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật tài khoản ví thành công",
                updatedWalletAccount)
        );
    }

    @DeleteMapping("/delete/{walletAccountId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Void>> deleteWalletAccount(
            @PathVariable Long walletAccountId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        walletAccountService.deleteWalletAccount(walletAccountId, userPrincipal);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đã xóa tài khoản ví thành công", null));
    }
}

