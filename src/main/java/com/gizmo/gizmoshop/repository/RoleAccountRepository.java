package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.RoleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount, Long> {
}
