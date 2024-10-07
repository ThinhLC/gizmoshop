package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.ShipperOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipperOrderRepository extends JpaRepository<ShipperOrder, Long> {
}
