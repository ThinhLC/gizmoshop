package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBrandRepository extends JpaRepository<ProductBrand, Long> {
    // Tìm kiếm thương hiệu theo tên
    Optional<ProductBrand> findByName(String name);

    // Tìm tất cả thương hiệu đang hoạt động
    List<ProductBrand> findByActive(boolean active);

    boolean existsByName(String name);
    Page<ProductBrand> findByDeletedFalse(Pageable pageable);

    ProductBrand findByIdAndDeletedFalse(Long id);
}
