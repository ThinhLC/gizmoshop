package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    Optional<OrderStatus> findByStatus(String status);
}
