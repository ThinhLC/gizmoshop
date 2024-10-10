package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AccountController {
    private final AccountService accountService;


}
