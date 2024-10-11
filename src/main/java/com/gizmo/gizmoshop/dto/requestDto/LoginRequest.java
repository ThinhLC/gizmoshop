    package com.gizmo.gizmoshop.dto.requestDto;

    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import lombok.*;

    @Getter
    @Setter
    @Builder
    public class LoginRequest {
        @NotBlank(message = "Vui lòng nhập Email")
        @Email(message = "Email không đúng định dạng")
        private String email;

        @NotBlank(message = "Vui lòng nhập mật khẩu")
        @Size(min = 6, message = "Mật khẩu cần tối thiểu 6 ký tự")
        private String password;
    }
