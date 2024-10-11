package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.LoginRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class testAPI {

    @Autowired
    public AuthService authService;

//    @GetMapping("/loginadmin")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<ResponseWrapper<LoginReponse>> loginadmin(@AuthenticationPrincipal ) {
//        ResponseWrapper<LoginReponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng Nhập Thành Công admin",null);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/loginshipper")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> loginShipper(@AuthenticationPrincipal UserPrincipal user) {
        String email = user.getEmail();
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng Nhập Thành Công",authService.getCurrentAccount(email));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/logincustomer")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<LoginReponse>> loginCustomer(@RequestBody LoginRequest loginRequest) {
        ResponseWrapper<LoginReponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng Nhập Thành Công", authService.attemptLogin(loginRequest.getEmail(), loginRequest.getPassword()));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/loginnocustomer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> checkRole() {

        String message = "Quyền truy cập đã được xác nhận.";
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, message, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
