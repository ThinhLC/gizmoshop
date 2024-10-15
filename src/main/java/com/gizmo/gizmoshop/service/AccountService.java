package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.dto.requestDto.EmailUpdateRequest;
import com.gizmo.gizmoshop.dto.requestDto.OtpVerificationRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleAccountRepository roleAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final EmailService emailService;
    private final OtpService otpService;

    public Optional<Account> findByEmail(String email) {
        return Optional.ofNullable(accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public AccountResponse updateLoggedInAccount(AccountRequest accountRequest) {
        // Lấy thông tin tài khoản từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // Lấy email từ token của tài khoản đăng nhập

        // Tìm tài khoản dựa trên email
        Account account = accountRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + email));

        // Cập nhật từng thông tin nếu được truyền vào
        if (accountRequest.getFullname() != null) {
            account.setFullname(accountRequest.getFullname());
        }
        if (accountRequest.getBirthday() != null) {
            account.setBirthday(accountRequest.getBirthday());
        }
        if (accountRequest.getExtraInfo() != null) {
            account.setExtra_info(accountRequest.getExtraInfo());
        }

        // Kiểm tra nếu người dùng muốn thay đổi mật khẩu
        if (accountRequest.getOldPassword() != null && accountRequest.getNewPassword() != null) {
            // Kiểm tra mật khẩu cũ
            if (!passwordEncoder.matches(accountRequest.getOldPassword(), account.getPassword())) {
                throw new InvalidInputException("Mật khẩu cũ không chính xác");
            }
            // Mã hóa và cập nhật mật khẩu mới
            account.setPassword(passwordEncoder.encode(accountRequest.getNewPassword()));
        }

        // Lưu thông tin tài khoản đã cập nhật
        account = accountRepository.save(account);
        return createAccountResponse(account);
    }

    public void sendOtpForEmailUpdate(EmailUpdateRequest request) {
        String newEmail = request.getNewEmail();
        String otp = otpService.generateOtp(newEmail);
        emailService.sendOtpEmail(newEmail, otp);
    }

    // Xác thực OTP và cập nhật email
    public void verifyOtpAndUpdateEmail(OtpVerificationRequest request) {
        // Lấy email hiện tại từ ngữ cảnh bảo mật (hoặc tìm theo email cũ)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();  // Hoặc lấy email từ tài khoản đăng nhập hiện tại

        String otp = request.getOtp();
        String newEmail = request.getNewEmail();

        // Kiểm tra xem OTP có hợp lệ không
        if (otpService.validateOtp(newEmail, otp)) {
            // Tìm tài khoản bằng email hiện tại (email cũ)
            Account account = accountRepository.findByEmailAndDeletedFalse(currentEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + currentEmail));

            // Cập nhật email mới
            account.setEmail(newEmail);
            accountRepository.save(account);
        } else {
            throw new InvalidInputException("OTP không hợp lệ");
        }
    }
        private AccountResponse createAccountResponse(Account account) {
            return new AccountResponse(
                    account.getId(),
                    account.getEmail(),
                    account.getFullname(),
                    account.getSdt(),
                    account.getBirthday(),
                    null, // Bỏ qua hình ảnh
                    account.getExtra_info(),
                    account.getCreate_at(),
                    account.getUpdate_at(),
                    account.getDeleted(),
                    account.getRoleAccounts().stream().map(roleAccount -> roleAccount.getRole().getName()).collect(Collectors.toSet())
            );
        }

}
