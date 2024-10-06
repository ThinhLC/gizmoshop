package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "withdrawal_history")
public class WithdrawalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date withdrawal_date;

    @ManyToOne
    @JoinColumn(name = "wallet_account_id", nullable = false)
    private WalletAccount walletAccount;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
