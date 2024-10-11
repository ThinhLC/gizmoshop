package com.gizmo.gizmoshop.dto.requestDto;

import com.gizmo.gizmoshop.entity.RoleAccount;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Set;
@Data
@Builder
public class CreateAccountRequest {
    private String email;
    private String fullname;
    private String sdt;
    private String password;
    private Date birthday;
    private String image;
    private String extra_info;
    private Date create_at;
    private Date update_at;
    private Boolean deleted;
    private Set<RoleAccount> roleAccounts;

}
