package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inventory_name", length = 256)
    private String inventoryName;

    @Column(name = "city", length = 256)
    private String city;

    @Column(name = "district", length = 256)
    private String district;

    @Column(name = "commune", length = 256)
    private String commune;
}
