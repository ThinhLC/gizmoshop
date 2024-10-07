package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_product", nullable = false)
    private Long idProduct; // Hoặc có thể sử dụng Product nếu có quan hệ với lớp Product

    @Column(name = "id_order", nullable = false)
    private Long idOrder; // Hoặc có thể sử dụng Order nếu có quan hệ với lớp Order

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "accept")
    private Boolean accept;

    @Column(name = "total")
    private Long total;
}