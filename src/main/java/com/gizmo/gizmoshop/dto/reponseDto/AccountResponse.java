package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String email;
    private String fullname;
    private String sdt;
    private Date birthday;
    private String image;
    private String extra_info;
    private Date create_at;
    private Date update_at;
    private Boolean deleted;
}
