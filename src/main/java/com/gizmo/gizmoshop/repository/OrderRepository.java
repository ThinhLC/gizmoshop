package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE (:sdt IS NULL OR o.idAccount.sdt = :sdt) " +
            "AND (:orderCode IS NULL OR o.orderCode = :orderCode)")
    Page<Order> findByPhoneOrOrderCode(@Param("sdt") String sdt, @Param("orderCode") String orderCode, Pageable pageable);

    Optional<Order> findByOrderCodeAndAddressAccount_Sdt(String orderCode, String sdt);

    @Query("SELECT o FROM Order o WHERE o.idAccount.id = :userId " +
            "AND (:idStatus IS NULL OR o.orderStatus.id = :idStatus) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    Page<Order> findOrdersByUserIdAndStatusAndDateRange(
            @Param("userId") Long userId,
            @Param("idStatus") Long idStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);
}
