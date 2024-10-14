package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.RoleResponse;
import com.gizmo.gizmoshop.service.Role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class RoleApi {

    private final RoleService roleService;

    @GetMapping("/roles /all")
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Chỉ cho phép Admin truy cập
    public ResponseEntity<ResponseWrapper<List<RoleResponse>>> getAllRoles() {
        try {
            List<RoleResponse> roles = roleService.getAllRoles();
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách vai trò thành công", roles));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseWrapper<>(HttpStatus.FORBIDDEN, e.getMessage(), null));
        }
    }
}
