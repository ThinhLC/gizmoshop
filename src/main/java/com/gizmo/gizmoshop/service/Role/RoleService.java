package com.gizmo.gizmoshop.service.Role;

import com.gizmo.gizmoshop.dto.reponseDto.RoleResponse;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;

    public RoleService(RoleRepository roleRepository, AccountRepository accountRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
    }

    // Phương thức lấy tất cả các roles chỉ cho Admin
    public List<RoleResponse> getAllRoles() throws AccessDeniedException {
        // Lấy thông tin người dùng hiện tại từ context bảo mật
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Tìm tài khoản từ database bằng username
        Account account = accountRepository.findByEmail(username)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy tài khoản"));

        // Kiểm tra xem tài khoản có quyền Admin hay không
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            throw new AccessDeniedException("Không tìm thấy vai trò Admin");
        }

        Set<RoleAccount> roles = account.getRoleAccounts();
        boolean isAdmin = roles.stream().anyMatch(roleAccount -> roleAccount.getRole().equals(adminRole));

        // Nếu không phải Admin, ném ra ngoại lệ AccessDeniedException
        if (!isAdmin) {
            throw new AccessDeniedException("Bạn không có quyền truy cập vào tài nguyên này");
        }

        // Nếu là Admin, trả về danh sách các role
        List<Role> rolesList = roleRepository.findAll();
        return rolesList.stream()
                .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toList());
    }
}