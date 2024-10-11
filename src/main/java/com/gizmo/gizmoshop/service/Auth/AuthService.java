package com.gizmo.gizmoshop.service.Auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.LoginReponse;
import com.gizmo.gizmoshop.dto.requestDto.RegisterRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleRepository;
import com.gizmo.gizmoshop.sercurity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class    AuthService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtToPrincipalConverter jwtToPrincipalConverter;
    private final JwtIssuer jwtIssuer;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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

            String refreshToken = jwtIssuer.issuerRefeshToken(principal.getUserId(), principal.getEmail());

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
    }


    public AccountResponse getAccountResponse(String email){
        accountRepository.findByEmail(email);
    }

    public AccountResponse getCurrentAccount2(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidInputException("No authentication details found ");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserPrincipal userPrincipal)) {
            throw new InvalidInputException("Principal is not of type UserPrincipal");
        }

        Account account = accountRepository.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new InvalidInputException("Email not found"));

        Set<String> roles = account.getRoleAccounts().stream()
                .map(role -> role.getRole().getName())
                .collect(Collectors.toSet());

        return new AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getFullname(),
                account.getSdt(),
                account.getBirthday(),
                account.getImage(),
                account.getExtra_info(),
                account.getCreate_at(),
                account.getUpdate_at(),
                account.getDeleted(),
                roles // Thêm danh sách vai trò vào AccountResponse
        );
    }

    public List<AccountResponse> getAllAccountResponses() {
        List<Account> accounts = accountRepository.findAll(); // Gọi tới accountRepository để lấy tất cả tài khoản
        return accounts.stream()
                .map(this::convertToAccountResponse) // Chuyển đổi mỗi Account thành AccountResponse
                .collect(Collectors.toList()); // Thu thập vào danh sách
    }


    public Page<AccountResponse> findAccountByCriteria(String keyword, Boolean available, String roleName, Pageable pageable){
            Page<Account> accounts = accountRepository.findAccountsByCriteria(keyword, available, roleName, pageable);
            Page<AccountResponse> accountResponses = accounts.map(this::convertToAccountResponse);

            return accountResponses;
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
                .image(account.getImage() != null ? account.getImage() : "default-image.png")
                .extraInfo(account.getExtra_info() != null ? account.getExtra_info() : "")
                .createAt(account.getCreate_at())
                .updateAt(account.getUpdate_at() != null ? account.getUpdate_at() : new Date())
                .deleted(account.getDeleted())
                .roles(roles)
                .build();
    }



    public LoginReponse refreshAccessToken(String refreshToken) {
       DecodedJWT decodedJWT = jwtDecoder.decode(refreshToken);

        if (decodedJWT.getExpiresAt().before(new Date())) {
            throw new InvalidInputException("Invalid refresh token");
        }

        UserPrincipal userPrincipal = (UserPrincipal) jwtToPrincipalConverter.convert(decodedJWT);

        //tạo access token mới
        String newAccessToken = jwtIssuer.issuer(userPrincipal.getUserId(), userPrincipal.getEmail(),
                userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

        return LoginReponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
