package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByInventoryName(String inventoryName);
    @Query("SELECT i FROM Inventory i WHERE " +
            "(:inventoryName IS NULL OR i.inventoryName LIKE %:inventoryName%) " +
            "AND (:active IS NULL OR i.active = :active)")
    Page<Inventory> findByCriteria(@Param("inventoryName") String inventoryName,
                                   @Param("active") Boolean active,
                                   Pageable pageable);

    boolean existsByInventoryName(String inventoryName);
}
