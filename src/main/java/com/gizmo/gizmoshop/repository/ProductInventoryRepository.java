package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {
    Optional<ProductInventory> findByProductId(Long productId);
    List<ProductInventory> findByInventoryId(Long inventoryId);
}
