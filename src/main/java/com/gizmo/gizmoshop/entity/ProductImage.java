package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "product_image")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_download_uri", length = 256, nullable = false)
    private String fileDownloadUri;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Sửa lại đây
    private Set<ProductImageMapping> productImageMappings = new HashSet<>();
}
