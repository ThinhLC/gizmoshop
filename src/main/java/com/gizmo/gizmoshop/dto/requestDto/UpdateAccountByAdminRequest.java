package com.gizmo.gizmoshop.dto.requestDto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Builder
@Getter
@Setter
public class UpdateAccountByAdminRequest {
    Long accountId;
    String fullname;
    String phone;
    LocalDate birthday;
    String extra_info;

    Date updated;
}
