package com.gizmo.gizmoshop.dto.requestDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
public class RegisterRequest {
    @NotBlank(message = "Vui lòng nhập Email")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 6, message = "Mật khẩu cần tối thiểu 6 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập xác minh mật khẩu")
    @Size(min = 6, message = "Mật khẩu cần tối thiểu 6 ký tự")
    private String confirmPassword;

    @NotBlank
    private String fullName;

    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    private LocalDate    birthDay;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Size(min = 10, max = 15, message = "Số điện thoại sai định dạng")
    private String sdt;

}
