package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter
@Setter
@Table(name = "product")
@EqualsAndHashCode(exclude = {"productInventories"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_author", nullable = false)
    private Account author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_category", nullable = false)
    private Categories category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_brand", nullable = false)
    private ProductBrand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private StatusProduct status;

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

    @Column(name = "thumbnail", length = 256, nullable = true)
    private String thumbnail;

    private Boolean deleted;

    private Float weight;

    private Float length;

    private Float height;

    private Float width;

    private Float volume;

    private Float area;

    private Long price;

    private int discountProduct;

    private Boolean isSupplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProductImageMapping> productImageMappings = new HashSet<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductInventory productInventory;
}
