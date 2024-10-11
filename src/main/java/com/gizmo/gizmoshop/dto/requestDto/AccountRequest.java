package com.gizmo.gizmoshop.dto.requestDto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccountRequest {
    @NotEmpty(message = "Họ và tên không được để trống")
    private String fullname;
    private Date birthday;
    private String extra_info;
    private String currentPassword;
    private String newPassword; // Mật khẩu mới
    private String confirmPassword;
    private String sdt;
}
