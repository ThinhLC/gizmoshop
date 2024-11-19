package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.WithdrawalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gizmo.gizmoshop.entity.WalletAccount;

import java.util.Date;
import java.util.List;

@Repository
public interface WithdrawalHistoryRepository extends JpaRepository<WithdrawalHistory, Long> {
    @Query("SELECT w FROM WithdrawalHistory w WHERE w.walletAccount IN :walletAccounts AND w.note LIKE %:auth%")
    Page<WithdrawalHistory> findByAuthInNote(String auth, List<WalletAccount> walletAccounts, Pageable pageable);
    @Query("SELECT w FROM WithdrawalHistory w WHERE w.walletAccount IN :walletAccounts AND w.withdrawalDate BETWEEN :startDate AND :endDate AND w.note LIKE %:auth%")
    Page<WithdrawalHistory> findByAuthInNoteAndDateRange(List<WalletAccount> walletAccounts, Date startDate, Date endDate, String auth, Pageable pageable);

    @Query("SELECT w FROM WithdrawalHistory w WHERE " +
            "LOWER(FUNCTION('SUBSTRING_INDEX', w.note, '|', 1)) = LOWER(:auth) AND " +
            "LOWER(FUNCTION('SUBSTRING_INDEX', w.note, '|', -1)) = LOWER(:status)")
    List<WithdrawalHistory> findByAuthAndStatus(@Param("auth") String auth, @Param("status") String status);
}

