package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "product_brand")
@Getter
@Setter
public class ProductBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "name", length = 256)
    private String name;

    @Column(name="brand_image")
    private String image;

    

    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean deleted = false;
}
