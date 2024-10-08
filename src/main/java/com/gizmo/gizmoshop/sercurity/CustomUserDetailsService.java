package com.gizmo.gizmoshop.sercurity;

import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountService accountService;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var account = accountService.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
        Set<GrantedAuthority> authorities = account.getRoleAccounts().stream()
                .map((roleaccount)-> new SimpleGrantedAuthority(roleaccount.getRole().getName()))
                .collect(Collectors.toSet());

        return UserPrincipal.builder()
                .userId(account.getId())
                .email(account.getEmail())
                .authorities(authorities)
                .password(account.getPassword())
                .build();

    }
}
