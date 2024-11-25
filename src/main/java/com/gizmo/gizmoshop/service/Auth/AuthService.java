package com.gizmo.gizmoshop.service.Auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.requestDto.RegisterRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.entity.Wishlist;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.InvalidTokenException;
import com.gizmo.gizmoshop.exception.RoleNotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import com.gizmo.gizmoshop.repository.RoleRepository;
import com.gizmo.gizmoshop.repository.WishlistRepository;
import com.gizmo.gizmoshop.sercurity.*;
import com.gizmo.gizmoshop.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final JwtIssuer jwtIssuer;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleAccountRepository roleAccountRepository;
    private final WishlistRepository wishlistRepository;
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, LocalDateTime> otpTimestamp = new HashMap<>();

    public LoginReponse attemptLogin(String email, String password) {
        if (email == null || email.isEmpty()) {
            throw new InvalidInputException("Tài khoản không được để trống");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Mật khẩu không được để trống");
        }

        var account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidInputException("Email not found"));

        // Kiểm tra nếu trường deleted là null hoặc true
        if (account.getDeleted() != null && account.getDeleted()) {
            throw new UsernameNotFoundException("Tài khoản không tồn tại");
        }

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var principal = (UserPrincipal) authentication.getPrincipal();
            var roles = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String token = jwtIssuer.issuer(principal.getUserId(), principal.getEmail(), roles);

            String refreshToken = jwtIssuer.issuerRefreshToken(principal.getUserId(), principal.getEmail());

            return LoginReponse.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new InvalidInputException("Tài khoản và mật khẩu không hợp lệ");
        }
    }

    public void register(RegisterRequest request){
        if (request.getEmail() == null || request.getEmail().isEmpty() || request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new InvalidInputException("Tài khoản mật khẩu không được để trống");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidInputException("Mật khẩu và xác nhận mật khẩu không khớp");
        }

        accountRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("Tài khoản đã tồn tại");
                });

        Account newAccount = new Account();
        newAccount.setFullname(request.getFullName());
        newAccount.setEmail(request.getEmail());
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));
        newAccount.setDeleted(false);
        newAccount.setSdt(request.getSdt());
        newAccount.setBirthday(request.getBirthDay());
        newAccount.setCreate_at(new Date());

        newAccount.setRoleAccounts(new HashSet<>());
        Role role = roleRepository.findByName("ROLE_CUSTOMER");
        RoleAccount roleAccount = new RoleAccount();
        roleAccount.setRole(role);
        roleAccount.setAccount(newAccount);
        newAccount.getRoleAccounts().add(roleAccount);

        accountRepository.save(newAccount);
        Wishlist wishlist = new Wishlist();
        wishlist.setAccountId(newAccount);
        wishlist.setCreateDate(LocalDateTime.now());
        wishlistRepository.save(wishlist);
    }


    public AccountResponse getCurrentAccount(String email){
        Account accounts = accountRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidInputException("Email not found"));
       return convertToAccountResponse(accounts);
    }

    public List<AccountResponse> getAllAccountResponses() {
        List<Account> accounts = accountRepository.findAll(); // Gọi tới accountRepository để lấy tất cả tài khoản
        return accounts.stream()
                .map(this::convertToAccountResponse) // Chuyển đổi mỗi Account thành AccountResponse
                .collect(Collectors.toList()); // Thu thập vào danh sách
    }

    public Page<AccountResponse> findAccountByCriteria(String keyword, Boolean deleted, String roleName, Pageable pageable) {
        Page<Account> account = accountRepository.findAccountsByCriteria(keyword,deleted,roleName,pageable);
        return account.map(this::convertToAccountResponse);
    }

    // Phương thức chuyển đổi từ Account sang AccountResponse
    private AccountResponse convertToAccountResponse(Account account) {
        Set<String> roles = account.getRoleAccounts().stream()
                .map(roleAccount -> roleAccount.getRole().getName())
                .collect(Collectors.toSet());
        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullname(account.getFullname())
                .sdt(account.getSdt())
                .birthday(account.getBirthday())
                .image(account.getImage() != null ? account.getImage() : null)
                .extraInfo(account.getExtra_info() != null ? account.getExtra_info() : "")
                .createAt(account.getCreate_at())
                .updateAt(account.getUpdate_at() != null ? account.getUpdate_at() : new Date())
                .deleted(account.getDeleted())
                .roles(roles)
                .build();
    }

    @Transactional
    public void resetPassword(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        String newPassword = passwordEncoder.encode("00000000");
        account.setPassword(newPassword);

        accountRepository.save(account);
    }

    @Transactional
    public void addAccountRoles(Long accountId, List<String> roleNames) {
        System.out.println("line1");
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));
        account.setRoleAccounts(new HashSet<>());

        roleAccountRepository.deleteByAccountId(accountId);
        System.out.println("Roles after delete: " + account.getRoleAccounts().size());

        if (roleNames == null || roleNames.isEmpty()) {
            account.setRoleAccounts(new HashSet<>());
        } else {
            Set<RoleAccount> roleAccounts = new HashSet<>();
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName);
                if (role == null) {
                    throw new RoleNotFoundException("Quyền không tồn tại: " + roleName);
                }
                System.out.println("line2");
                RoleAccount roleAccount = new RoleAccount();
                roleAccount.setRole(role);
                roleAccount.setAccount(account);
                roleAccounts.add(roleAccount);
            }
            account.setRoleAccounts(roleAccounts);
            System.out.println("line3");
        }
        accountRepository.save(account);
    }

    public Account updateAccountDeleted(Long accountId) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(()-> new UsernameNotFoundException("Không tìm thấy người dùng" + accountId));
        account.setDeleted(!account.getDeleted());

        return accountRepository.save(account);
    }

    public void removeAccountRoles(Long accountId, List<String> roleNames) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản id: " + accountId));

        Set<RoleAccount> roleAccounts = account.getRoleAccounts();
        if (roleAccounts == null || roleAccounts.isEmpty()) {
            throw new IllegalArgumentException("Tài khoản không có vai trò nào được gán.");
        }

        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new IllegalArgumentException("User has no roles assigned.");
            }
            RoleAccount roleAccountToRemove = roleAccounts.stream()
                    .filter(ra -> ra.getRole().getName().equals(roleName))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Vai trò không được gán cho tài khoản: " + roleName));

            roleAccounts.remove(roleAccountToRemove);
        }

        account.setRoleAccounts(roleAccounts);
        accountRepository.save(account);
    }


    public LoginReponse refreshAccessToken(String refreshToken) {
        // Giải mã refreshToken
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtDecoder.decode(refreshToken);
        } catch (InvalidTokenException e) {
            throw new InvalidInputException("Invalid refresh token");
        }

        // Kiểm tra hạn sử dụng của refreshToken
        if (jwtDecoder.isTokenExpired(decodedJWT)) {
            throw new InvalidInputException("Refresh token has expired");
        }

        // Lấy thông tin người dùng từ refreshToken
        UserPrincipal userPrincipal = (UserPrincipal) jwtToPrincipalConverter.convert(decodedJWT);

        // Tạo accessToken mới  
        String newAccessToken = jwtIssuer.issuer(userPrincipal.getUserId(), userPrincipal.getEmail(),
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

        return LoginReponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // Giữ lại refresh token cũ
                .build();
    }

    public void sendOtpToEmail(String email) {
        if (!isEmailExist(email)) {
            throw new InvalidInputException("Email không tồn tại trong hệ thống.");
        }
        String otp = generateOtp();
        otpStorage.put(email, otp);  // Lưu OTP vào Map
        otpTimestamp.put(email, LocalDateTime.now());  // Lưu thời gian gửi OTP
        emailService.sendOtpEmail(email, otp);
    }

    private boolean isEmailExist(String email) {
        return accountRepository.findByEmail(email).isPresent();  // Kiểm tra email trong CSDL
    }
    // Tạo OTP ngẫu nhiên 6 chữ số
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    // Phương thức xác thực OTP
    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        LocalDateTime sentTime = otpTimestamp.get(email);

        // Kiểm tra OTP có tồn tại không
        if (storedOtp == null || sentTime == null) {
            throw new InvalidInputException("OTP không tồn tại hoặc đã hết hạn.");
        }

        // Kiểm tra xem OTP có đúng định dạng (6 chữ số) và khớp với OTP đã lưu
        if (!otp.matches("\\d{6}") || !otp.equals(storedOtp)) {
            throw new InvalidInputException("OTP không đúng hoặc không hợp lệ.");
        }

        // Kiểm tra OTP có hết hạn không (hết hạn sau 2 phút)
        if (sentTime.plusMinutes(2).isBefore(LocalDateTime.now())) {
            otpStorage.remove(email);  // Xóa OTP hết hạn
            otpTimestamp.remove(email);  // Xóa thời gian gửi OTP hết hạn
            throw new InvalidInputException("OTP đã hết hạn.");
        }

        return true;
    }

    // Phương thức cập nhật mật khẩu mới
    public String updatePassword(String email, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidInputException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }

        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        user.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(user);

        otpStorage.remove(email);
        otpTimestamp.remove(email);

        return "Mật khẩu đã được cập nhật thành công!";
    }

    public String changePassword(String email, String oldPassword, String newPassword, String confirmPassword) {
        // Kiểm tra nếu mật khẩu mới và xác nhận mật khẩu khớp
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidInputException("Mật khẩu mới và xác nhận mật khẩu không khớp.");
        }
        if (oldPassword.equals(newPassword)) {
            throw new InvalidInputException("Mật khẩu cũ và mật khẩu mới không được trùng nhau.");
        }
        // Lấy người dùng từ email
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidInputException("Mật khẩu cũ không chính xác.");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(user);

        return "Mật khẩu đã được cập nhật thành công!";
    }
}
