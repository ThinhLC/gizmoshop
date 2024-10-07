package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 256, nullable = false)
    private String name;

    @Column(name = "image_id", length = 256, nullable = false)
    private String imageId;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Categories parent;
}