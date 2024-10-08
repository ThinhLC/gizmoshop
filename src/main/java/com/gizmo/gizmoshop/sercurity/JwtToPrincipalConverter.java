package com.gizmo.gizmoshop.sercurity;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gizmo.gizmoshop.exception.InvalidTokenException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT decodedJWT) throws InvalidTokenException {
        return UserPrincipal.builder()
                .userId(Long.valueOf(decodedJWT.getSubject()))
                .email(decodedJWT.getClaim("e").asString())
                .authorities(extractAuthoritiesFromClaim(decodedJWT))
                .build();
    }

    private Set<SimpleGrantedAuthority> extractAuthoritiesFromClaim(DecodedJWT decodedJWT) {
                var claims = decodedJWT.getClaim("a");
                if (claims.isNull() || claims.isMissing()) return Set.of();
                return claims.asList(String.class).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
    }
}
