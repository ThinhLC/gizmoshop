package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedFalse();
    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR p.name LIKE %:keyword%) AND (:available IS NULL OR p.deleted = :available)"+"AND p.status.id = 1"+"AND p.category.active=true")
    Page<Product> findByKeywordAndAvailability(String keyword, Boolean available, Pageable pageable);
}
