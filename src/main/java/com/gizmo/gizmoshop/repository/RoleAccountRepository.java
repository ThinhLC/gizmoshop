package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.RoleAccount;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM RoleAccount ra WHERE ra.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);

    List<RoleAccount> findByAccount_IdAndRole_Name(Long accountId, String roleName);
    @Query("SELECT CASE WHEN COUNT(ra) > 0 THEN true ELSE false END FROM RoleAccount ra " +
            "WHERE ra.account.id = :accountId AND ra.role.name = :roleName")
    Boolean findByAccountAndRole(Long accountId, String roleName);


}
