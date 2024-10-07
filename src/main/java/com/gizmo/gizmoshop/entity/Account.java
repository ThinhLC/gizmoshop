package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String email;

    @Column(length = 255)
    private String fullname;

    @Column(length = 13)
    private String sdt;

    @Column(length = 255, nullable = false)
    private String password;

    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Lob
    private String image;

    @Lob
    private String extra_info;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date update_at;

    private Boolean deleted;

}
