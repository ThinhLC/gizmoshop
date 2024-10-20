package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;


@Getter
@Setter
public class AccountRequest {
    private String fullname;
    private LocalDate birthday;
    private String sdt;
    private String extraInfo;
    private String oldPassword;
    private String newPassword;

}

