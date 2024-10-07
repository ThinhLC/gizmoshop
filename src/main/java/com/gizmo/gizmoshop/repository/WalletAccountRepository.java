package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.WalletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {
    List<WalletAccount> findByAccountId(Long accountId);
}
