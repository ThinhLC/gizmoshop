package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "supplier_infor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInfo {
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
    private String taxCode;

    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "id_account", nullable = false)
    private Account account;

    private Date created;

}
