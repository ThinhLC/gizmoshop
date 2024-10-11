package com.gizmo.gizmoshop.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_author", nullable = false)
    private Account author; // Lớp Account cần được định nghĩa tương ứng

    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false)
    private Categories category; // Lớp Categories cần được định nghĩa tương ứng


    @ManyToOne
    @JoinColumn(name = "id_brand", nullable = false)
    private ProductBrand brand; // Lớp ProductBrand cần được định nghĩa tương ứng

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusProduct status; // Lớp StatusProduct cần được định nghĩa tương ứng

    @Column(name = "id_image", length = 256, nullable = false)
    private String imageId;

    private Boolean active = true;

    @Column(name = "long_description", columnDefinition = "LONGTEXT")
    private String longDescription;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    private Long view;

    @Column(name = "thumbnail", length = 256, nullable = false)
    private String thumbnail;

    private Boolean deleted;

    private Float weight;

    private Float acreage;

    private Long price;

    private String sku;
}
