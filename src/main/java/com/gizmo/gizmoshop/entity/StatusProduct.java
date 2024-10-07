package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status_product")
public class StatusProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 256)
    private String name;
}
