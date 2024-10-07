package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.StatusProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusProductRepository extends JpaRepository<StatusProduct, Long> {

}
