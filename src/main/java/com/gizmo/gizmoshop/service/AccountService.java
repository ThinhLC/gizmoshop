package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.requestDto.AccountRequest;
import com.gizmo.gizmoshop.dto.requestDto.EmailUpdateRequest;
import com.gizmo.gizmoshop.dto.requestDto.OtpVerificationRequest;
import com.gizmo.gizmoshop.dto.requestDto.UpdateAccountByAdminRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Service
public class    AccountService {
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

    public AccountResponse updateLoggedInAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal, AccountRequest accountRequest, Optional <MultipartFile> file) {

        String email = userPrincipal.getEmail();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + email));

        if (account.getDeleted()) {
            throw new ResourceNotFoundException("Tài khoản không tồn tại");
        }
        if (accountRequest.getFullname() != null) {
            account.setFullname(accountRequest.getFullname());
        }
        if (accountRequest.getBirthday() != null) {
            try {
                validateBirthday(accountRequest.getBirthday()); // Kiểm tra ngày sinh hợp lệ
                account.setBirthday(accountRequest.getBirthday());
            } catch (DateTimeParseException e) {
                throw new InvalidInputException("Ngày sinh không hợp lệ, phải là định dạng yyyy-MM-dd");
            }
        }
        if (accountRequest.getExtraInfo() != null) {
            account.setExtra_info(accountRequest.getExtraInfo());
        }

        if (accountRequest.getSdt() != null) {
            if (!isValidPhoneNumber(accountRequest.getSdt())) {
                throw new InvalidInputException("Số điện thoại không hợp lệ");
            }
            account.setSdt(accountRequest.getSdt());
        }

        if (accountRequest.getOldPassword() != null && accountRequest.getNewPassword() != null) {
            if (!passwordEncoder.matches(accountRequest.getOldPassword(), account.getPassword())) {
                throw new InvalidInputException("Mật khẩu cũ không chính xác");
            }
            account.setPassword(passwordEncoder.encode(accountRequest.getNewPassword()));
        }

        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                if (account.getImage() != null) {
                    imageService.deleteImage(account.getImage(), "account");
                }

                String imagePath = imageService.saveImage(file.get(), "account");
                account.setImage(imagePath);

            } catch (IOException e) {
                throw new InvalidInputException("Lỗi khi xử lý hình ảnh: " + e.getMessage());
            }
        }else{
            System.out.println("file is not present");
        }
        account = accountRepository.save(account);
        return createAccountResponse(account);
    }

    public byte[] loadImage(String filename, String type){
        byte[] imageData = new byte[0];
        try {
            imageData = imageService.loadImageAsResource(filename,"account");
        } catch (IOException e) {
            throw new InvalidInputException("Could not load");
        }

        return imageData;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10}$");
    }

    private void validateBirthday(LocalDate birthday) {
        LocalDate today = LocalDate.now();
        if (birthday.isAfter(today)) {
            throw new InvalidInputException("Ngày sinh không hợp lệ");
        }
        if (Period.between(birthday, today).getYears() < 13) {
            throw new InvalidInputException("Người dùng phải ít nhất 13 tuổi");
        }
    }

    public void sendOtpForEmailUpdate(EmailUpdateRequest request) {
        String newEmail = request.getNewEmail();
        String otp = otpService.generateOtp(newEmail);
        emailService.sendOtpEmail(newEmail, otp);
    }

    public void verifyOtpAndUpdateEmail(OtpVerificationRequest request) {
        String newEmail = request.getNewEmail();
        String otp = request.getOtp();

        if (otpService.validateOtp(newEmail, otp)) {
            Account account = accountRepository.findByEmail(newEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản với email: " + newEmail));

            if (account.getDeleted()) {
                throw new ResourceNotFoundException("Tài khoản không tồn tại");
            }

            account.setEmail(newEmail);
            accountRepository.save(account);
        } else {
            throw new InvalidInputException("OTP không hợp lệ");
        }
    }

    @Transactional
    public AccountResponse updateAccountByAdmin(Long accountId,UpdateAccountByAdminRequest accountRequest) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new UsernameNotFoundException("Không tìm thấy user với id"));
        System.out.println(account.getEmail().toString());
        account.setFullname(accountRequest.getFullname());
        account.setBirthday(accountRequest.getBirthday());
        account.setExtra_info(accountRequest.getExtra_info());
        account.setBirthday(accountRequest.getBirthday());
        account.setUpdate_at(new Date());

        accountRepository.save(account);
        return createAccountResponse(account);

    }

    public AccountResponse findById(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));
        return createAccountResponse(account);
    }


        private AccountResponse createAccountResponse(Account account) {
            return new AccountResponse(
                    account.getId(),
                    account.getEmail(),
                    account.getFullname(),
                    account.getSdt(),
                    account.getBirthday(),
                    account.getImage() != null ? account.getImage() : "Chưa có hình ảnh",
                    account.getExtra_info(),
                    account.getCreate_at(),
                    account.getUpdate_at(),
                    account.getDeleted(),
                    account.getRoleAccounts().stream().map(roleAccount -> roleAccount.getRole().getName()).collect(Collectors.toSet())
            );
        }

}
