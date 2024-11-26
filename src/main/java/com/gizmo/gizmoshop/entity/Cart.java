package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // Hoặc có thể sử dụng Account nếu có quan hệ với lớp Account

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "total_price")
    private Long totalPrice;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY) // Mối quan hệ với CartItems
    private List<CartItems> items;
}