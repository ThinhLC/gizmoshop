package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "business_infor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String business_name;

    @Lob
    private String description;

    private Long balance;

    private Long frozen_balance;

    @Column(length = 20)
    private String tax_code;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "id_account", nullable = false)
    private Account account;

    @Column(length = 255)
    private String website;
}
