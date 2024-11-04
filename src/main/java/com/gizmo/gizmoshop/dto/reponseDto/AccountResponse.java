package com.gizmo.gizmoshop.dto.reponseDto;


import com.gizmo.gizmoshop.entity.Account;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;


@AllArgsConstructor
@Builder
@Getter
@Setter

public class AccountResponse {
    private Long id;
    private String email;
    private String fullname;
    private String sdt;
    private LocalDate birthday;
    private String image;
    private String extra_info;
    private Boolean deleted;
    private String extraInfo;
    private Date createAt;
    private Date updateAt;
    private Set<String> roles;

        public AccountResponse(Long id, String email, String fullname, String sdt, LocalDate birthday, String image, String extraInfo, Date createAt, Date updateAt, Boolean deleted, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.fullname = fullname;
        this.sdt = sdt;
        this.birthday = birthday;
        this.image = image != null ? image : "default-image.png";
        this.extraInfo = extraInfo != null ? extraInfo : "";
        this.createAt = createAt;
        this.updateAt = updateAt != null ? updateAt : new Date();
        this.deleted = deleted;
        this.roles = roles;
    }

    public AccountResponse(Long id) {
        this.id = id;
    }
    public AccountResponse(Account author) {
    }
}
