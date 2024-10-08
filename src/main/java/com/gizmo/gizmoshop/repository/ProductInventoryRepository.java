package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

}
