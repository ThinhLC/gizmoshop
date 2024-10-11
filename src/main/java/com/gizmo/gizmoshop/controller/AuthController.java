package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.LoginRequest;
import com.gizmo.gizmoshop.dto.requestDto.RefreshTokenRequest;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {
        @Autowired
        public AuthService authService;



        @PostMapping("/auth/login")
        @PreAuthorize("permitAll()")
        public ResponseEntity<ResponseWrapper<LoginReponse>> login(@RequestBody LoginRequest loginRequest) {
            ResponseWrapper<LoginReponse> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng Nhập Thành Công", authService.attemptLogin(loginRequest.getEmail(), loginRequest.getPassword()));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        @PostMapping("/refresh")
        @PreAuthorize("permitAll()")
        public ResponseEntity<ResponseWrapper<LoginReponse>> refreshAccessToken(@RequestBody RefreshTokenRequest refreshToken) {
            ResponseWrapper<LoginReponse> reponse = new ResponseWrapper<>(HttpStatus.OK, "",  authService.refreshAccessToken(refreshToken.getRefreshToken()));
          return new ResponseEntity<>(reponse, HttpStatus.OK);
        }


}
