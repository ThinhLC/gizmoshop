package com.gizmo.gizmoshop.dto.requestDto;

import lombok.*;

@Getter
@Setter
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
