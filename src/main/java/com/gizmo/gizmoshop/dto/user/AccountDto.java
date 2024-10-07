package com.gizmo.gizmoshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private int id;
    private String fullname;
    private String email;
    private Date birthday;
    private String sdt;
    private String image;
    private String extra_info;
    private Date create_at;
    private Date update_at;
    private Boolean deleted;

}
