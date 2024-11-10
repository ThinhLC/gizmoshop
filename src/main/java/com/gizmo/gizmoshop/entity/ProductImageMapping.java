    package com.gizmo.gizmoshop.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.Data;
    import lombok.Getter;
    import lombok.Setter;

    @Data
    @Entity
    @Getter
    @Setter
    @Table(name = "product_image_mapping")
    public class ProductImageMapping {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "id_product",nullable = false)
        @JsonIgnore
        private Product product;

        @ManyToOne
        @JoinColumn(name = "id_product_image", nullable = false)
        private ProductImage image; // Đảm bảo tên thuộc tính là 'image'
    }
