package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher_to_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherToOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher; // Hoặc có thể sử dụng Voucher nếu có quan hệ với lớp Voucher

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Hoặc có thể sử dụng Order nếu có quan hệ với lớp Order

    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
