package com.gizmo.gizmoshop.dto.reponseDto;

<<<<<<< HEAD
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Builder
=======
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Builder
@Getter
@Setter
>>>>>>> 4c40860f7c07efedd4f702a9c9c844655679bce4
public class AccountResponse {
    private Long id;
    private String email;
    private String fullname;
    private String sdt;
    private Date birthday;
    private String image;
<<<<<<< HEAD
    private String extra_info;
    private Date create_at;
    private Date update_at;
    private Boolean deleted;
=======
    private String extraInfo;
    private Date createAt;
    private Date updateAt;
    private Boolean deleted;
    private Set<String> roles;

        public AccountResponse(Long id, String email, String fullname, String sdt, Date birthday, String image, String extraInfo, Date createAt, Date updateAt, Boolean deleted, Set<String> roles) {
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
>>>>>>> 4c40860f7c07efedd4f702a9c9c844655679bce4
}
