package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.ShipperInfor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperInforRepository extends JpaRepository<ShipperInfor, Long> {

    Optional<ShipperInfor> findByAccountId(Account AccountId);
}
