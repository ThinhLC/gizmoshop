package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipper_order")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ShipperOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId; // Hoặc có thể sử dụng Order nếu có quan hệ với lớp Order

    @Column(name = "shipper_infor_id", nullable = false)
    private Long shipperInforId; // Hoặc có thể sử dụng ShipperInfor nếu có quan hệ với lớp ShipperInfor
}
