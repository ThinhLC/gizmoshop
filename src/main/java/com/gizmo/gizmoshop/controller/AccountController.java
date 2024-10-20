package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.dto.requestDto.EmailUpdateRequest;
import com.gizmo.gizmoshop.dto.requestDto.OtpVerificationRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AccountController {

    private final AccountService accountService;

    @PutMapping(value = "/account/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN','ROLE_STAFF','ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> updateLoggedInAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart AccountRequest accountRequest,
            @RequestParam("file") MultipartFile file) {

        AccountResponse updatedAccount = accountService.updateLoggedInAccount(userPrincipal, accountRequest, file);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật tài khoản thành công", updatedAccount));
    }

    @PostMapping("/account/email/otp")
    @PreAuthorize("isAuthenticated()") // Yêu cầu tài khoản phải đăng nhập
    public ResponseEntity<ResponseWrapper<Void>> sendOtpForEmailUpdate(@RequestBody EmailUpdateRequest request) {
        accountService.sendOtpForEmailUpdate(request);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Gửi mã OTP cho email mới thành công", null));
    }

    @PutMapping("/account/email/verify")
    @PreAuthorize("isAuthenticated()") // Yêu cầu tài khoản phải đăng nhập
    public ResponseEntity<ResponseWrapper<Void>> verifyOtpAndUpdateEmail(
            @RequestBody OtpVerificationRequest request) {
        accountService.verifyOtpAndUpdateEmail(request);

        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật email thành công", null));
    }

}
