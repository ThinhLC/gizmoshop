package com.gizmo.gizmoshop.service.Auth;

import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.sercurity.JwtDecoder;
import com.gizmo.gizmoshop.sercurity.JwtIssuer;
import com.gizmo.gizmoshop.sercurity.JwtToPrincipalConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final JwtIssuer jwtIssuer;
    private final JwtDecoder jwtDecoder;
}
