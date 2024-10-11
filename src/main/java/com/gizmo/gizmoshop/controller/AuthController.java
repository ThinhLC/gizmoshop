package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.LoginRequest;
import com.gizmo.gizmoshop.dto.requestDto.RefreshTokenRequest;
import com.gizmo.gizmoshop.dto.requestDto.RegisterRequest;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

        @Autowired
        public AuthService authService;

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
            ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK,"Đăng kí thành công",null);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/auth/account")
        @PreAuthorize("hasAnyRole('ROLE_CUSTOMER','ROLE_ADMIN','ROLE_STAFF','ROLE_SHIPPER')")
        public ResponseEntity<ResponseWrapper<AccountResponse>> getCurrentAccount(){
            ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK,"Đã lấy được người dùng", authService.getCurrentAccount());
            return ResponseEntity.ok(response);
        }



        @PostMapping("/refresh")
        @PreAuthorize("permitAll()")
        public ResponseEntity<ResponseWrapper<LoginReponse>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshToken) {
            ResponseWrapper<LoginReponse> reponse = new ResponseWrapper<>(HttpStatus.OK, "",  authService.refreshAccessToken(refreshToken.getRefreshToken()));
          return new ResponseEntity<>(reponse, HttpStatus.OK);
        }


}
