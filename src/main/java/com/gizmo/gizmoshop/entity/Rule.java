package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rule") // Tên bảng trong database
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng
    private Long id;

    @Column(name = "rental_inventory_price", nullable = false)
    private Long rentalInventoryPrice;

    @Column(name = "limit_product", nullable = false)
    private Integer limitProduct;

    @Column(name = "business_rule", columnDefinition = "LONGTEXT")
    private String businessRule;

    @Column(name = "customer_rule", columnDefinition = "LONGTEXT")
    private String customerRule;

    @Column(name = "shipper_rule", columnDefinition = "LONGTEXT")
    private String shipperRule;

    @Column(name = "ngay_gia_han_hop_dong")
    private Long ngayGiaHanHopDong; // 2 ngày

    @Column(name = "quangduong", columnDefinition = "VARCHAR(255)")
    private Long quangduong; // 12k/3km

    @Column(name = "sokmvuot_qua_40", columnDefinition = "VARCHAR(255)")
    private Long sokmvuotQua40; // 2k/3km

    @Column(name = "sotieshipcodinh")
    private Long sotieshipcodinh;

    @Column(name = "trongluong", columnDefinition = "VARCHAR(255)")
    private Long trongluong; // 5k/1kg

    @Column(name = "sotienvuotqua40km")
    private Long sotienvuotqua40km; // 30k

    @Column(name = "hoa_hong")
    private Long hoaHong;

    @Column(name = "luong_cung_shipper")
    private Long luongCungShipper;

    @Column(name = "thoi_gian_lay_lai_hang")
    private Long thoiGianLayLaiHang;
}
