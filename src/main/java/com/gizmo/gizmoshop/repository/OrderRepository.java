package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Order;
import com.gizmo.gizmoshop.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
            "AND o.orderStatus.roleStatus = false " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    Page<Order> findOrdersByUserIdAndStatusAndDateRange(
            @Param("userId") Long userId,
            @Param("idStatus") Long idStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.idAccount.id = :userId " +
            "AND (:idStatus IS NULL OR o.orderStatus.id = :idStatus) " +
            "AND o.orderStatus.roleStatus = false " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    List<Order> totalOrder(
            @Param("userId") Long userId,
            @Param("idStatus") Long idStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);


    @Query("SELECT o FROM Order o " +
            "WHERE (:idStatus IS NULL OR o.orderStatus.id = :idStatus) " +
            "AND (:roleStatus IS NULL OR o.orderStatus.roleStatus = :roleStatus) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    Page<Order> findOrdersByALlWithStatusRoleAndDateRange(
            @Param("idStatus") Long idStatus,
            @Param("roleStatus") Boolean roleStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);



    @Query("SELECT o FROM Order o WHERE o.idAccount.id = :idAccount AND o.orderStatus.roleStatus = true")
    List<Order> findOrdersByAccountIdAndStatusRoleOne(@Param("idAccount") Long idAccount);

    @Query("SELECT o FROM Order o WHERE o.idAccount.id = :idAccount AND o.orderStatus.roleStatus = true"
            + " AND (:startDate IS NULL OR o.createOderTime >= :startDate)"
            + " AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    List<Order> findOrdersByAccountIdAndStatusRoleOne(
            @Param("idAccount") Long idAccount,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

    @Query("SELECT o FROM Order o " +
            "JOIN o.idAccount acc " +
            "WHERE acc.id = :supplierId " +
            "AND (:keyword IS NULL OR o.orderCode LIKE %:keyword%) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate) " +
            "AND (:orderCode IS NULL OR o.orderCode = :orderCode)")
    Page<Order> findOrdersBySupplier(
            @Param("supplierId") Long supplierId,
            @Param("keyword") String keyword,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("orderCode") String orderCode,
            Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.orderStatus.id = :idOrderStatus "
            + "AND (:startDate IS NULL OR o.createOderTime >= :startDate) "
            + "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    List<Order> findOrdersByOrderStatus(@Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("idOrderStatus") long idOrderStatus);

}
