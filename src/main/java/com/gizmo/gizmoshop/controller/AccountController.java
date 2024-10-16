package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.dto.requestDto.EmailUpdateRequest;
import com.gizmo.gizmoshop.dto.requestDto.OtpVerificationRequest;
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

        // Gọi tới service để thực hiện quá trình xác thực và cập nhật email
        accountService.verifyOtpAndUpdateEmail(request);

        // Trả về phản hồi khi thành công
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật email thành công", null));
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
