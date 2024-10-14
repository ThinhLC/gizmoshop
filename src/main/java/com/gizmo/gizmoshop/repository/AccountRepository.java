package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndDeletedFalse(String email);

    @Query("SELECT a FROM Account a " +
            "WHERE (:keyword IS NULL OR a.fullname LIKE %:keyword% OR a.email LIKE %:keyword%) AND " +
            "(:deleted IS NULL OR a.deleted = :deleted) AND " +
            "(:roleName IS NULL OR EXISTS (SELECT r FROM a.roleAccounts ra JOIN ra.role r WHERE r.name LIKE %:roleName%))")
    Page<Account> findAccountsByCriteria(@Param("keyword") String keyword,
                                         @Param("deleted") Boolean deleted,
                                         @Param("roleName") String roleName,
                                         Pageable pageable);




}
