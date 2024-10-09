package com.gizmo.gizmoshop.service.Auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.sercurity.JwtDecoder;
import com.gizmo.gizmoshop.sercurity.JwtIssuer;
import com.gizmo.gizmoshop.sercurity.JwtToPrincipalConverter;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final JwtIssuer jwtIssuer;
    private final JwtDecoder jwtDecoder;

    public LoginReponse attemptLogin(String email, String password) {
        if (email == null || email.isEmpty()) {
            throw new InvalidInputException("Email is empty");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password is empty");
        }

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidInputException("Email not found"));

        // Kiểm tra nếu trường deleted là null hoặc true
        if (account.getDeleted() != null && account.getDeleted()) {
            throw new UsernameNotFoundException("Username not found");
        }

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var principal = (UserPrincipal) authentication.getPrincipal();
            var roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtIssuer.issuer(principal.getUserId(), principal.getEmail(), roles);

            String refreshToken = jwtIssuer.issuerRefeshToken(principal.getUserId(), principal.getEmail());

            return LoginReponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new InvalidInputException("Invalid email or password");
        }
    }


    public LoginReponse refreshAccessToken(String refreshToken) {
       DecodedJWT decodedJWT = jwtDecoder.decode(refreshToken);


        if (decodedJWT.getExpiresAt().before(new Date())) {
            throw new InvalidInputException("Invalid refresh token");
        }

        UserPrincipal userPrincipal = (UserPrincipal) jwtToPrincipalConverter.convert(decodedJWT);

        //tạo access token mới
        String newAccessToken = jwtIssuer.issuer(userPrincipal.getUserId(), userPrincipal.getEmail(),
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());



        return LoginReponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();


    }
}
