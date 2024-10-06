package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "wishlist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class WishlistItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wishlist_id", nullable = false)
    private Long wishlistId; // Hoặc có thể sử dụng Wishlist nếu có quan hệ với lớp Wishlist

    @Column(name = "product_id", nullable = false)
    private Long productId; // Hoặc có thể sử dụng Product nếu có quan hệ với lớp Product

    @Column(name = "create_date")
    private LocalDateTime createDate;
}
