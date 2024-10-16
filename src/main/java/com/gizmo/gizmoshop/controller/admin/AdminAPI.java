package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.UpdateAccountByAdminRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.service.AccountService;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/admin")
@CrossOrigin("*")
public class AdminAPI {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/list/account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accountResponses = authService.getAllAccountResponses(); // Gọi phương thức trong AuthService
        ResponseWrapper<List<AccountResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", accountResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<AccountResponse>>> findUsersByCriteria(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "deleted", required = false) Boolean available,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort){
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));

        Page<AccountResponse> accountResponses = authService.findAccountByCriteria(keyword, available, roleName, pageable);
        ResponseWrapper<Page<AccountResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", accountResponses);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/account/{accountId}/availability")
    @Operation(summary = "Cập nhật trạng thái deleted của tài khoản",
            description = "API này được dùng để thay đổi trạng thái deleted của tài khoản. Chỉ có ADMIN mới được phép thực hiện.",
            tags = {"Account"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Account>> updateAccountAvailability(@PathVariable Long accountId) {
        Account updatedAccount = authService.updateAccountDeleted(accountId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật trạng thái thành công", updatedAccount));
    }
    @PatchMapping("account/{accountId}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@PathVariable Long accountId) {
        authService.resetPassword(accountId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đặt lại mật khẩu thành công", null));
    }

    @PutMapping("account/{accountId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> updateAccountByAdmin( @PathVariable Long accountId,
                                                                                  @RequestBody UpdateAccountByAdminRequest updateAccountByAdminRequest){
            AccountResponse accountResponse = accountService.updateAccountByAdmin(accountId, updateAccountByAdminRequest);
            ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Account update successful", accountResponse);
            return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/roles/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> addAccountRoles(
            @PathVariable Long accountId,
            @RequestBody List<String> roleNames) {
        System.out.println(roleNames);
        authService.addAccountRoles(accountId, roleNames);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật quyền thành công", null));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> getAccountId(
            @PathVariable Long accountId) {
        AccountResponse accountResponse = accountService.findById(accountId);
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin accountId:"+ accountId, accountResponse);
        return ResponseEntity.ok(response);
    }


}
