package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.*;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import com.gizmo.gizmoshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    @Autowired
    public AuthService authService;

    @Autowired
    public SupplierService supplierService;

    @PostMapping("/auth/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<LoginReponse>> login(@RequestBody @Validated LoginRequest loginRequest) {
        ResponseWrapper<LoginReponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng Nhập Thành Công", authService.attemptLogin(loginRequest.getEmail(), loginRequest.getPassword()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/auth/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Void>> register(@RequestBody @Validated RegisterRequest registerRequest) {
        authService.register(registerRequest);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng kí thành công và tạo giỏ hàng thành công", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/account")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN','ROLE_STAFF','ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> getCurrentAccount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đã lấy được người dùng", authService.getCurrentAccount(email));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<LoginReponse>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshToken) {
        ResponseWrapper<LoginReponse> reponse = new ResponseWrapper<>(HttpStatus.OK, "", authService.refreshAccessToken(refreshToken.getRefreshToken()));
        return new ResponseEntity<>(reponse, HttpStatus.OK);
    }

    @PostMapping("/send-email")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<String>> sendEmail(@RequestBody @Validated ForgotPassRequest request) {
        authService.sendOtpToEmail(request.getEmail());
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "OTP đã được gửi qua email!", null);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/send-emailsignin")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<String>> sendOtpEmail(@RequestBody @Validated ForgotPassRequest request) {
        String otp = authService.sendOtpEmail(request.getEmail()); // Gọi service và nhận mã OTP
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "OTP đã được gửi qua email!", otp); // Đính kèm mã OTP vào response
        return ResponseEntity.ok(response);
    }


    @PostMapping("/confirm-otp-and-reset-password")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<String>> confirmOtpAndResetPassword(@RequestBody @Validated ForgotPassRequest request) {
        authService.validateOtp(request.getEmail(), request.getOtp());
        String message = authService.updatePassword(request.getEmail(), request.getNewPassword(), request.getConfirmPassword());
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, message, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")  // Chỉ cho phép người dùng đã đăng nhập
    public ResponseEntity<ResponseWrapper<String>> changePassword(
            @RequestBody @Validated ChangePassRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        String email = userPrincipal.getEmail();
        authService.validateOtp(email, request.getOtp());  // Kiểm tra OTP
        String message = authService.changePassword(email, request.getOldPassword(), request.getNewPassword(), request.getConfirmPassword());

        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, message, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-otp-changePassword")
    @PreAuthorize("isAuthenticated()")  // Chỉ cho phép người dùng đã đăng nhập
    public ResponseEntity<ResponseWrapper<String>> sendOtpForPasswordChange(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();  // Lấy email người dùng hiện tại từ principal
        authService.sendOtpToEmail(email);  // Gửi OTP đến email của người dùng
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "OTP đã được gửi qua email!", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register-supplier")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Void>> registerSupplier(@RequestBody @Validated SupplierRequest request,
                                                                  @AuthenticationPrincipal UserPrincipal UserPrincipal) {
        Long id = UserPrincipal.getUserId();
        supplierService.SupplierRegister(request,id);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Đã gửi đơn đăng kí thành công, vui lòng chờ xét duyệt", null);
        return ResponseEntity.ok(response);
    }


}
