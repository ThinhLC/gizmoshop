package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipper_infor")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ShipperInfor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId; // Hoặc có thể sử dụng Account nếu có quan hệ với lớp Account

    @Column(name = "balance", nullable = false)
    private Long balance = 0L;

    @Column(name = "frozen_balance", nullable = false)
    private Long frozenBalance = 0L;

    @Column(name = "commission")
    private Long commission;

    @Column(name = "total_time")
    private Long totalTime;

    @Column(name = "total_order")
    private Long totalOrder;

    @Column(name = "total_distance")
    private Long totalDistance;
}
