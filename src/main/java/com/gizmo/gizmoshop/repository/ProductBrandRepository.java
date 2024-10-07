package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long> {
    List<ProductBrand> findByNameContaining(String name);

    List<ProductBrand> findByActive(Boolean active);
}
