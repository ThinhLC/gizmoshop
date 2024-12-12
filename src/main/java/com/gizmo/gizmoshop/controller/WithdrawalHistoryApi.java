package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.PendingWithdrawalResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WithdrawalHistoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.WithdrawalHistoryRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WithdrawalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/withdrawalhistory")
@RequiredArgsConstructor
@CrossOrigin("*")
public class WithdrawalHistoryApi {

    @Autowired
    private WithdrawalHistoryService withdrawalHistoryService;


    @GetMapping("/customer/getall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForCustomer(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<WithdrawalHistoryResponse> withdrawalHistories =
                withdrawalHistoryService.getWithdrawalHistoryForCustomer(userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của khách hàng đã được lấy thành công",
                withdrawalHistories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/getall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForSupplier(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<WithdrawalHistoryResponse> withdrawalHistories =
                withdrawalHistoryService.getWithdrawalHistoryForSupplier(userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của nhà cung cấp đã được lấy thành công",
                withdrawalHistories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForCustomerAndDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        System.out.println(startDate + "//" + endDate);
        Page<WithdrawalHistoryResponse> withdrawalHistories =
                withdrawalHistoryService.getWithdrawalHistoryForCustomerAndDateRange(startDate, endDate, userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của khách hàng lọc theo ngày, tháng, năm đã được lấy thành công",
                withdrawalHistories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForSupplierAndDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<WithdrawalHistoryResponse> withdrawalHistories =
                withdrawalHistoryService.getWithdrawalHistoryForSupplierAndDateRange(startDate, endDate, userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của nhà cung cấp lọc theo ngày, tháng, năm đã được lấy thành công",
                withdrawalHistories
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter-by-auth-status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getHistoriesByAuthAndStatus(
            @RequestParam String auth,
            @RequestParam String status,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        // Kiểm tra tham số sort, ví dụ: "field,DESC" hoặc "field,ASC"
        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));

        // Gọi service để lấy danh sách phân trang
        Page<WithdrawalHistoryResponse> withdrawalHistoryPage =
                withdrawalHistoryService.getHistoriesByAuthAndStatus(userPrincipal, auth, status, pageable);

        // Tạo ResponseWrapper bao bọc kết quả trả về
        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lọc lịch sử rút tiền theo auth và status thành công",
                withdrawalHistoryPage
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/update-status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<WithdrawalHistoryResponse>> updateStatusAndNote(
            @PathVariable Long id,
            @RequestBody WithdrawalHistoryRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // Gọi service để cập nhật trạng thái và note
        WithdrawalHistoryResponse updatedHistory = withdrawalHistoryService.updateStatusAndNote(id, request, userPrincipal);

        // Tạo response wrapper để trả về kết quả
        ResponseWrapper<WithdrawalHistoryResponse> responseWrapper = new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật trạng thái và ghi chú thành công",
                updatedHistory
        );

        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/withdrawals/pending")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<PendingWithdrawalResponse>>> getPendingWithdrawals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        // Xử lý tham số phân trang và sắp xếp
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));

        // Gọi dịch vụ để lấy các giao dịch đang chờ xử lý
        Page<PendingWithdrawalResponse> pendingWithdrawals = withdrawalHistoryService.getPendingWithdrawals(pageable);

        // Tạo ResponseWrapper bao bọc kết quả trả về
        ResponseWrapper<Page<PendingWithdrawalResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Danh sách giao dịch đang chờ xử lý đã được lấy thành công",
                pendingWithdrawals
        );

        return ResponseEntity.ok(response);
    }
}



