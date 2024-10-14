package com.gizmo.gizmoshop.dto.requestDto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccountRequest {
    private String fullname;
    private String sdt;
    private Date birthday;
    private String extraInfo;
    private String oldPassword;
    private String newPassword;

}
