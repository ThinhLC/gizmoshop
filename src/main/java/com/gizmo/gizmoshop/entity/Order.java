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


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_wallet", nullable = false)
    private WalletAccount idWallet; // Hoặc có thể sử dụng Wallet nếu có quan hệ với lớp Wallet

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_account", nullable = false)
    private Account idAccount; // Hoặc có thể sử dụng Account nếu có quan hệ với lớp Account

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_status", nullable = false)
    private OrderStatus orderStatus; // Hoặc có thể sử dụng OrderStatus nếu có quan hệ với lớp OrderStatus

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_address", nullable = false)
    private AddressAccount addressAccount; // Hoặc có thể sử dụng Address nếu có quan hệ với lớp Address

    @Column(name = "note", length = 256)
    private String note;

    @Column(name = "oder_acreage")
    private Float oderAcreage ;

    @Column(name = "payment_methods", nullable = false)
    private Boolean paymentMethods;

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

    @Column(name="create_oder_time")
    private Date createOderTime;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Contract contract;
}