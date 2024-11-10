package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductImageMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageMappingRepository extends JpaRepository<ProductImageMapping, Long> {
    List<ProductImageMapping> findByProductId(long productId);
}
