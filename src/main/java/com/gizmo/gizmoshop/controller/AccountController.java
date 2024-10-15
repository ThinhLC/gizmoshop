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

    @PutMapping("/account/update")
    @PreAuthorize("isAuthenticated()") // Yêu cầu tài khoản phải đăng nhập
    public ResponseEntity<ResponseWrapper<AccountResponse>> updateLoggedInAccount(
            @RequestBody AccountRequest accountRequest) {

        AccountResponse updatedAccount = accountService.updateLoggedInAccount(accountRequest);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật tài khoản thành công", updatedAccount));
    }

//    @PostMapping("/account/update-email")
//    @PreAuthorize("isAuthenticated()") // Yêu cầu tài khoản phải đăng nhập
//    public ResponseEntity<ResponseWrapper<String>> updateEmail(
//            @RequestParam String newEmail) {
////
////        String response = accountService.updateEmail(newEmail);
//        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật email thành công", response));
//    }
}
