package com.gizmo.gizmoshop.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationRequest {
    private String newEmail;
    private String otp;
}
