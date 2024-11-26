package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Contract;
import com.gizmo.gizmoshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Contract findByOrderId(Long orderId);
}
