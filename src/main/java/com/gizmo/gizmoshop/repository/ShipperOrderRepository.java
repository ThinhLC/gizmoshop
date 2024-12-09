package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Order;
import com.gizmo.gizmoshop.entity.ShipperInfor;
import com.gizmo.gizmoshop.entity.ShipperOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperOrderRepository extends JpaRepository<ShipperOrder, Long> {

    Optional<ShipperOrder> findByOrderIdAndShipperInforId(Order o, ShipperInfor s);

}
