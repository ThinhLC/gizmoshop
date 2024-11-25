package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.SupplierInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuppilerInfoRepository extends JpaRepository <SupplierInfo, Long>{
    Optional<SupplierInfo> findByAccount_Id(Long id);

    Optional<SupplierInfo> findByTaxCode(String taxCode);

//    Page<SupplierInfo> findByDeleted(boolean deleted, Pageable pageable, String keyword);

    @Query("SELECT s FROM SupplierInfo s WHERE s.deleted = :deleted AND (:keyword IS NULL OR s.account.fullname LIKE %:keyword%)")
    Page<SupplierInfo> findByDeleted(@Param("deleted") boolean deleted,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);

    Page<SupplierInfo> findByDescriptionContaining(String description, Pageable pageable);



}
