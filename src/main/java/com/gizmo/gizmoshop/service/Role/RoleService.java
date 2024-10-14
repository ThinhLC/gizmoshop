package com.gizmo.gizmoshop.service.Role;

import com.gizmo.gizmoshop.dto.reponseDto.RoleResponse;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
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

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;

    public List<RoleResponse> getAllRoles() throws AccessDeniedException {
        try {
            // Lấy thông tin người dùng hiện tại từ context bảo mật
            String username = getCurrentUsername();

            // Tìm tài khoản từ database bằng username
            Account account = accountRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));

            // Kiểm tra xem tài khoản có quyền Admin hay không
            boolean isAdmin = account.getRoleAccounts().stream()
                    .anyMatch(roleAccount -> roleAccount.getRole().getName().equals("ROLE_ADMIN"));

            // Nếu không phải Admin, ném ra ngoại lệ
            if (!isAdmin) {
                throw new AccessDeniedException("Bạn không có quyền truy cập vào tài nguyên này");
            }

            // Nếu là Admin, trả về danh sách các role
            List<Role> rolesList = roleRepository.findAll();
            return rolesList.stream()
                    .map(role -> new RoleResponse(role.getId(), role.getName(), role.getDescription()))
                    .collect(Collectors.toList());

        } catch (AccessDeniedException e) {
            // Xử lý ngoại lệ AccessDeniedException
            throw new AccessDeniedException("Bạn không có quyền truy cập vào tài nguyên này");
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác, có thể ghi log hoặc ném ra một ngoại lệ tùy chỉnh
            throw new RuntimeException("Đã xảy ra lỗi trong quá trình lấy danh sách vai trò", e);
        }
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }
}