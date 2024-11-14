package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "withdrawal_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "withdrawal_date")
    private Date withdrawalDate;

    @ManyToOne
    @JoinColumn(name = "wallet_account_id", nullable = false)
    private WalletAccount walletAccount;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
