package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleAccountRepository roleAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Account>findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));
    }
    public List<Account> findAll() {
        return accountRepository.findAll();
    }


    public AccountResponse updateAccount(AccountRequest accountRequest) {
        // Tìm kiếm tài khoản theo số điện thoại
        Account account = accountRepository.findBySdtAndDeletedFalse(accountRequest.getSdt())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với số điện thoại: " + accountRequest.getSdt()));

        // Tạo một instance của BCryptPasswordEncoder
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // Kiểm tra mật khẩu hiện tại nếu muốn thay đổi mật khẩu mới
        if (accountRequest.getNewPassword() != null) {
            // Kiểm tra mật khẩu hiện tại
            if (!bCryptPasswordEncoder.matches(accountRequest.getCurrentPassword(), account.getPassword())) {
                throw new InvalidInputException("Mật khẩu hiện tại không đúng");
            }

            // Kiểm tra xem mật khẩu mới và xác nhận mật khẩu có khớp không
            if (!accountRequest.getNewPassword().equals(accountRequest.getConfirmPassword())) {
                throw new InvalidInputException("Mật khẩu mới và xác nhận mật khẩu không khớp");
            }

            // Cập nhật mật khẩu mới (mã hóa trước khi lưu)
            account.setPassword(bCryptPasswordEncoder.encode(accountRequest.getNewPassword()));
        }

        // Cập nhật các trường khác nếu có
        if (accountRequest.getFullname() != null) {
            account.setFullname(accountRequest.getFullname());
        }
        if (accountRequest.getBirthday() != null) {
            account.setBirthday(accountRequest.getBirthday());
        }
        if (accountRequest.getExtra_info() != null) {
            account.setExtra_info(accountRequest.getExtra_info());
        }
        // Cập nhật thời gian sửa đổi
        account.setUpdate_at(new Date());
        // Lưu tài khoản đã cập nhật
        accountRepository.save(account);
        // Chuyển đổi thành đối tượng phản hồi
        return convertToResponse(account);
    }

    private AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullname(account.getFullname())
                .sdt(account.getSdt())
                .birthday(account.getBirthday())
                .extra_info(account.getExtra_info())
                .createAt(account.getCreate_at())
                .updateAt(account.getUpdate_at())
                .deleted(account.getDeleted())
                .build();
    }
}
