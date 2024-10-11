package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "active", nullable = false)
    private Boolean active = false;
}
