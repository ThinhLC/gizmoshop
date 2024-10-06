package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address_account")
public class AddressAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String fullname;

    @Column(length = 256)
    private String specific_address;

    @Column(length = 13)
    private String sdt;

    @Column(length = 256)
    private String city;

    @Column(length = 256)
    private String district;

    @Column(length = 256)
    private String commune;

    @Column(length = 255)
    private String longitude;

    @Column(length = 256)
    private String latitude;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "id_account", nullable = false)
    private Account account;

    private Boolean is_default;
}
