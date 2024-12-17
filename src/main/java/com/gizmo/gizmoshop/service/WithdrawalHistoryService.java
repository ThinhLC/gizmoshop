package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.PendingWithdrawalResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WalletAccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WithdrawalHistoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.WithdrawalHistoryRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.WalletAccount;
import com.gizmo.gizmoshop.entity.WithdrawalHistory;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.WalletAccountRepository;
import com.gizmo.gizmoshop.repository.WithdrawalHistoryRepository;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class WithdrawalHistoryService {

    @Autowired
    private WithdrawalHistoryRepository withdrawalHistoryRepository;

    @Autowired
    private WalletAccountRepository walletAccountRepository;

    public Page<WithdrawalHistoryResponse> getWithdrawalHistoryForCustomer(UserPrincipal userPrincipal, Pageable pageable) {
        Long accountId = userPrincipal.getUserId();
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        if (walletAccounts.isEmpty()) {
            throw new InvalidInputException("Không tìm thấy ví cho tài khoản này.");
        }
        Page<WithdrawalHistory> histories = withdrawalHistoryRepository.findByAuthInNote("CUSTOMER", walletAccounts, pageable);

        return histories.map(this::convertToDto);
    }

    public Page<WithdrawalHistoryResponse> getWithdrawalHistoryForSupplier(UserPrincipal userPrincipal, Pageable pageable) {
        Long accountId = userPrincipal.getUserId();
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        if (walletAccounts.isEmpty()) {
            throw new InvalidInputException("Không tìm thấy ví cho tài khoản này.");
        }
        Page<WithdrawalHistory> histories = withdrawalHistoryRepository.findByAuthInNote("SUPPLIER", walletAccounts, pageable);

        return histories.map(this::convertToDto);
    }

    public Page<WithdrawalHistoryResponse> getWithdrawalHistoryForCustomerAndDateRange(
            Date startDate, Date endDate, UserPrincipal userPrincipal, Pageable pageable) {

        Long accountId = userPrincipal.getUserId();
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        if (walletAccounts.isEmpty()) {
            throw new InvalidInputException("Không tìm thấy ví cho tài khoản này.");
        }
        Page<WithdrawalHistory> histories = withdrawalHistoryRepository.findByAuthInNoteAndDateRange(walletAccounts, startDate, endDate, "CUSTOMER", pageable);

        return histories.map(this::convertToDto);
    }


    public Page<WithdrawalHistoryResponse> getWithdrawalHistoryForSupplierAndDateRange(
            Date startDate, Date endDate, UserPrincipal userPrincipal, Pageable pageable) {

        Long accountId = userPrincipal.getUserId();
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        if (walletAccounts.isEmpty()) {
            throw new InvalidInputException("Không tìm thấy ví cho tài khoản này.");
        }
        Page<WithdrawalHistory> histories = withdrawalHistoryRepository.findByAuthInNoteAndDateRange(walletAccounts, startDate, endDate, "SUPPLIER", pageable);

        return histories.map(this::convertToDto);
    }

    public Page<WithdrawalHistoryResponse> getHistoriesByAuthAndStatus(Long idTransaction, UserPrincipal userPrincipal, String auth, String status, Pageable pageable) {
        // Kiểm tra quyền truy cập của người dùng

        if (!userPrincipal.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_STAFF"))) {
            throw new InvalidInputException("Bạn không có quyền truy cập vào tài nguyên này.");
        }
        Page<WithdrawalHistory> historiesPage = withdrawalHistoryRepository.findByAuthAndStatus( auth, status,idTransaction, pageable);
        return historiesPage.map(this::convertToDto);
    }





    public WithdrawalHistoryResponse updateStatusAndNote(Long id, WithdrawalHistoryRequest request, UserPrincipal userPrincipal) {

        Long accountId = userPrincipal.getUserId();
        // Tìm WithdrawalHistory trong DB
        WithdrawalHistory history = withdrawalHistoryRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("WithdrawalHistory not found with id " + id));

        // Kiểm tra trạng thái hợp lệ
        String newStatus = request.getNewStatus();
        if (!Arrays.asList("COMPETED", "CANCEL", "PENDING").contains(newStatus)) {
            throw new InvalidInputException("Trạng thái không hợp lệ. Trạng thái hợp lệ là COMPETED, CANCEL, PENDING.");
        }

        // Chỉ thay đổi phần status và note
        String currentNote = history.getNote();
        String newNote = request.getNewNote();

        // Cập nhật lại note với status mới
        String updatedNote = currentNote.split("\\|")[0] + "|" + newNote + "|" + newStatus;
        history.setNote(updatedNote);

        // Lưu lại vào DB
        WithdrawalHistory updatedHistory = withdrawalHistoryRepository.save(history);

        return convertToDto(updatedHistory);
    }


    private WithdrawalHistoryResponse convertToDto(WithdrawalHistory history) {
        String[] noteParts = history.getNote().split("\\|");

        String auth = noteParts.length > 0 ? noteParts[0].trim() : "UNKNOWN";
        String note = noteParts.length > 1 ? noteParts[1].trim() : "";
        String status = noteParts.length > 2 ? noteParts[2].trim() : "UNKNOWN";

        // Lấy thông tin chi tiết từ walletAccount và account
        WalletAccount walletAccount = history.getWalletAccount();
        Account account = history.getAccount();

        // Tạo đối tượng WalletAccountResponse nếu walletAccount không null
        WalletAccountResponse walletAccountResponse = null;
        if (walletAccount != null) {
            walletAccountResponse = WalletAccountResponse.builder()
                    .id(walletAccount.getId())
                    .bankName(walletAccount.getBank_name())
                    .accountNumber(walletAccount.getAccount_number())
                    .branch(walletAccount.getBranch())
                    .swiftCode(walletAccount.getSwift_code())
                    .createAt(walletAccount.getCreate_at())
                    .updateAt(walletAccount.getUpdate_at())
                    .accountId(walletAccount.getAccount().getId())
                    .build();
        }

        // Tạo đối tượng AccountResponse nếu account không null
        AccountResponse accountResponse = null;
        if (account != null) {
            accountResponse = AccountResponse.builder()
                    .id(account.getId())
                    .email(account.getEmail())
                    .fullname(account.getFullname())
                    .sdt(account.getSdt())
                    .birthday(account.getBirthday())
                    .image(account.getImage())
                    .extraInfo(account.getExtra_info())
                    .createAt(account.getCreate_at())
                    .updateAt(account.getUpdate_at())
                    .build();
        }

        return WithdrawalHistoryResponse.builder()
                .id(history.getId())
                .amount(history.getAmount())
                .withdrawalDate(history.getWithdrawalDate())
                .note(note)
                .auth(auth)
                .status(status)
                .accountId(account != null ? account.getId() : null)
                .walletAccountId(walletAccount != null ? walletAccount.getId() : null)
                .account(accountResponse)
                .walletAccount(walletAccountResponse)
                .build();
    }

    public Page<PendingWithdrawalResponse> getPendingWithdrawals(Pageable pageable) {
        // Lấy danh sách các giao dịch PENDING từ repository
        Page<WithdrawalHistory> pendingWithdrawals = withdrawalHistoryRepository.findPendingWithdrawals(pageable);

        // Map sang DTO
        return pendingWithdrawals.map(withdrawal -> PendingWithdrawalResponse.builder()
                .id(withdrawal.getId())  // Lấy ID giao dịch
                .amount(withdrawal.getAmount())  // Lấy số tiền
                .auth(withdrawal.getNote() != null ? extractAuthFromNote(withdrawal.getNote()) : "UNKNOWN")  // Lấy role từ note
                .createAt(withdrawal.getWithdrawalDate())
                .build());
    }

    private String extractAuthFromNote(String note) {
        // Hàm này trích xuất giá trị 'auth' từ note, ví dụ từ "role|pending|someOtherInfo"
        String[] noteParts = note.split("\\|");
        return noteParts.length > 0 ? noteParts[0].trim() : "UNKNOWN";
    }
}
