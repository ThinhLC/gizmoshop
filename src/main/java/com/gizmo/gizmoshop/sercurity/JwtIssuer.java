package com.gizmo.gizmoshop.sercurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties jwtProperties;

    public String issuer(Long userId, String email, List<String> role) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .withClaim("e",email)
                .withClaim("a",role)
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }

    public String issuerRefeshToken(Long userId, String email) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(Instant.now().plus(Duration.of(30,ChronoUnit.DAYS)))
                .withClaim("e",email)
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }
}
