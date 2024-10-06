package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "wallet_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String bank_name;

    @Column(length = 255)
    private String account_number;

    @Column(length = 255)
    private String branch;

    @Column(length = 11)
    private String swift_code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date update_at;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
