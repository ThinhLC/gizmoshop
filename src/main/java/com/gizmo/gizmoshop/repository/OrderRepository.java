package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Order;
import com.gizmo.gizmoshop.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Order findAndLockOrderById(@Param("id") Long id);
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


    // đang làm ở đây nè dm
    @Query("SELECT o FROM Order o " +
            "WHERE (:idStatus IS NULL OR o.orderStatus.id IN :idStatus) " +
            "AND (:roleStatus IS NULL OR o.orderStatus.roleStatus = :roleStatus) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)"+
            "AND (:orderCode IS NULL OR o.orderCode LIKE %:orderCode%)")
    Page<Order> findOrdersByALlWithStatusRoleAndDateRange(
            @Param("orderCode") String orderCode,
            @Param("idStatus") List<Long> idStatus,
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
    @Query("SELECT o FROM Order o WHERE o.orderStatus.roleStatus = false"
            + " AND (:startDate IS NULL OR o.createOderTime >= :startDate)"
            + " AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    List<Order> findOrdersByAccountIdAndStatusRoleFalse(
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


    @Query("SELECT o FROM Order o " +
            "WHERE o.idAccount.id = :accountId " +
            "AND (:idStatus IS NULL OR o.orderStatus.id = :idStatus) " +
            "AND o.orderStatus.roleStatus = true OR o.orderStatus.roleStatus= null "+
            // Chấp nhận idStatus là NULL
            "AND (:keyword IS NULL OR " +
            "LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findAllOrderForSupplier(@Param("idStatus") Long idStatus,
                                        @Param("keyword") String keyword,
                                        @Param("accountId") long accountId,Pageable pageable);


    @Query("SELECT o FROM Order o " +
            "WHERE (:idStatus IS NULL OR o.orderStatus.id = :idStatus) " +
            "AND o.orderStatus.roleStatus = true OR o.orderStatus.roleStatus= null "+
            "AND (:keyword IS NULL OR " +
            "LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findAllOrderOfSupplierForAdmin(@Param("idStatus") Long idStatus,  // Thay `long` bằng `Long` để có thể nhận null
                                               @Param("keyword") String keyword,
                                               Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.orderStatus.id = :idOrderStatus "
            + "AND (:startDate IS NULL OR o.createOderTime >= :startDate) "
            + "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    List<Order> findOrdersByOrderStatus(@Param("startDate") Date startDate,
                                        @Param("endDate") Date endDate,
                                        @Param("idOrderStatus") long idOrderStatus);



    @Query("SELECT o FROM Order o " +
            "WHERE (:type IS NULL OR " +
            "  (:type = true AND o.orderStatus.id = 18) OR " +
            "  (:type = false AND o.orderStatus.id = 6)) " +
            "AND (:keyword IS NULL OR " +
            "  LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    Page<Order> findAllOrderByTypeAndDateAndKeyword(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("type") boolean type,  // 0 = CUSTOMER, 1 = SUPPLIER
            @Param("keyword") String keyword,
            Pageable pageable);

    //JOIN VI KHONG CÓ QUAN HỆ JPA
    @Query("SELECT o FROM Order o " +
            "JOIN ShipperOrder s ON o.id = s.orderId.id " +
            "WHERE (:type IS NULL OR " +
            "  (:type = true AND s.shipperInforId.id = :shipperId) AND (o.orderStatus.id = 15 OR o.orderStatus.id = 29) OR " +
            "  (:type = false AND s.shipperInforId.id = :shipperId AND (o.orderStatus.id = 20 OR o.orderStatus.id = 13))) " +
            "AND (:keyword IS NULL OR " +
            "  LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "  LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR o.createOderTime >= :startDate) " +
            "AND (:endDate IS NULL OR o.createOderTime <= :endDate)")
    Page<Order> findAllOrderByShipperAndDateAndKeyword(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("shipperId") Long shipperId,
            @Param("keyword") String keyword,
            @Param("type") boolean type,
            Pageable pageable);

    Page<Order> findByOrderStatusIdIn(List<Long> orderStatusIds, Pageable pageable);
}
