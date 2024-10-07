package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.WithdrawalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalHistoryRepository extends JpaRepository<WithdrawalHistory, Long> {
    List<WithdrawalHistory> findByAccount_Id(Long accountId);

    List<WithdrawalHistory> findByWalletAccount_Id(Long walletAccountId);
}

