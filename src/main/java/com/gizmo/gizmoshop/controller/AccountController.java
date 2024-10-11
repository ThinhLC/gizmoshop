package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AccountController {
    private final AccountService accountService;

    @PutMapping("/account/update") // Cập nhật thông tin tài khoản
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<AccountResponse>> updateAccount(@RequestBody AccountRequest accountRequest) {
        AccountResponse updatedAccount = accountService.updateAccount(accountRequest);
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Tài khoản đã được cập nhật", updatedAccount);
        return ResponseEntity.ok(response);
    }
}


