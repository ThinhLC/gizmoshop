package com.gizmo.gizmoshop.service.Role;

import com.gizmo.gizmoshop.dto.reponseDto.RoleResponse;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;


    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toList());
    }




}