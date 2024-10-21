package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
    @Column(name = "active", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean active;
    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;
}
