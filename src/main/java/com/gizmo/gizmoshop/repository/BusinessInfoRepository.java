package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.BusinessInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessInfoRepository extends JpaRepository<BusinessInfo, Long> {

}
