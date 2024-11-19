package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.SupplierInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuppilerInfoRepository extends JpaRepository <SupplierInfo, Long>{
    Optional<SupplierInfo> findByAccount_Id(Long id);

//    Optional<SupplierInfo> findByTax_code(String taxCode);
}
