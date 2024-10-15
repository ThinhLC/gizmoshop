package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT v FROM Voucher v WHERE " +
            "(:code IS NULL OR v.code LIKE %:code%) " +
            "AND (:status IS NULL OR v.status = :status)")
    Page<Voucher> findByCriteria(@Param("code") String code,
                                 @Param("status") Boolean status,
                                 Pageable pageable);
}
