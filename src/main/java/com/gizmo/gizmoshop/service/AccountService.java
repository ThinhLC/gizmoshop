package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleAccountRepository roleAccountRepository;

    public Optional<Account>findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));
    }
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

}
