package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByBrand_Id(Long brandId);

    List<Product> findByStatus_Id(Long statusId);


    Optional<Product> findById(Long id);
}
