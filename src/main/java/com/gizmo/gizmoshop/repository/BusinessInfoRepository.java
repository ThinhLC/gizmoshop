package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.SupplierInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessInfoRepository extends JpaRepository<SupplierInfo, Long> {

}
