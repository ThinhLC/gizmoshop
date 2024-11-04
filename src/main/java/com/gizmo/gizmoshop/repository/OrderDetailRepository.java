package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT COALESCE(SUM(od.quantity), 0) FROM OrderDetail od JOIN od.idProduct p WHERE p.id = :productId AND FUNCTION('MONTH', od.idOrder.createOderTime) = :month AND FUNCTION('YEAR', od.idOrder.createOderTime) = :year")
    Integer countQuantityByProductAndMonth(@Param("productId") Long productId, @Param("month") int month, @Param("year") int year);
}
