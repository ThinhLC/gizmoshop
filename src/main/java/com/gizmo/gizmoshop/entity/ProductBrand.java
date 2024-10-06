package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_brand")
public class ProductBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean active;

    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "name", length = 256)
    private String name;
}
