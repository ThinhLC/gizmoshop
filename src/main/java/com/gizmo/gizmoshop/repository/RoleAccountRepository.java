package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.RoleAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleAccount ra WHERE ra.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);

}
