package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:code IS NULL OR v.code LIKE %:code%) " +
            "AND (:status IS NULL OR v.status = :status)")
    Page<Voucher> findByCriteria(@Param("code") String code,
                                 @Param("status") Boolean status,
                                 Pageable pageable);
    @Query("SELECT v FROM Voucher v WHERE v.status = true AND v.validTo >= :currentDateTime")
    List<Voucher> findActiveAndValidVouchers(LocalDateTime currentDateTime);
    @Query("SELECT v FROM Voucher v WHERE "
            + "(:code IS NULL OR v.code LIKE %:code%) AND "
            + "(:status IS NULL OR v.status = :status) AND "
            + "(v.status = true) AND " // Trạng thái phải là 1
            + "(v.usageLimit IS NULL OR v.usedCount < v.usageLimit) AND " // Còn lượt sử dụng
            + "(v.validFrom <= :currentDateTime) AND " // Còn hạn sử dụng
            + "(v.validTo >= :currentDateTime)") // Còn hạn sử dụng
    Page<Voucher> findVouchersForUser(@Param("code") String code,
                                      @Param("status") Boolean status,
                                      @Param("currentDateTime") LocalDateTime currentDateTime,
                                      Pageable pageable);
}
