package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long> {
    @Query("SELECT os FROM OrderStatus os WHERE os.roleStatus = false")
    List<OrderStatus> findOrderStatusesForUser();


    @Query("SELECT os FROM OrderStatus os WHERE os.roleStatus = true")
    List<OrderStatus> findOrderStatusesForSupplier();

    @Query("SELECT os FROM OrderStatus os WHERE os.roleStatus IS NULL")
    List<OrderStatus> findOrderStatusesForCommon();

}
