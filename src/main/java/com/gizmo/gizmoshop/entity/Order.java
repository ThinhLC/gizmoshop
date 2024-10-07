package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_wallet", nullable = false)
    private Long idWallet; // Hoặc có thể sử dụng Wallet nếu có quan hệ với lớp Wallet

    @Column(name = "id_account", nullable = false)
    private Long idAccount; // Hoặc có thể sử dụng Account nếu có quan hệ với lớp Account

    @Column(name = "id_status", nullable = false)
    private Long idStatus; // Hoặc có thể sử dụng OrderStatus nếu có quan hệ với lớp OrderStatus

    @Column(name = "id_purchase", nullable = false)
    private Long idPurchase; // Hoặc có thể sử dụng PurchaseOrder nếu có quan hệ với lớp PurchaseOrder

    @Column(name = "id_address", nullable = false)
    private Long idAddress; // Hoặc có thể sử dụng Address nếu có quan hệ với lớp Address

    @Column(name = "note", length = 256)
    private String note;

    @Column(name = "order_scenage")
    private Float orderScenage;

    @Column(name = "payment_methods", nullable = false)
    private Boolean paymentMethods;

    @Column(name = "ngayGiaoHang")
    private Date ngayGiaoHang;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "total_weight")
    private Float totalWeight;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "delivery_time")
    private Date deliveryTime;

    @Column(name = "fixed_cost")
    private Long fixedCost;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "order_code", length = 256)
    private String orderCode;
}