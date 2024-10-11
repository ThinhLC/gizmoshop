package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.service.AccountService;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/admin")
@CrossOrigin("*")
public class AdminAPI {

    @Autowired
    private AuthService authService;

    @GetMapping("/list/account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accountResponses = authService.getAllAccountResponses(); // Gọi phương thức trong AuthService
        ResponseWrapper<List<AccountResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", accountResponses);
        return ResponseEntity.ok(response);
    }


}
