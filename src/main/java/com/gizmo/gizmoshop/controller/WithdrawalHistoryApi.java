package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WithdrawalHistoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.WithdrawalHistoryRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WithdrawalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
            Pageable pageable) {
        Page<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryForCustomer(userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của khách hàng đã được lấy thành công",
                withdrawalHistory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/getall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForSupplier(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryForSupplier(userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của nhà cung cấp đã được lấy thành công",
                withdrawalHistory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForCustomerAndDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryForCustomerAndDateRange(startDate, endDate, userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của khách hàng lọc theo ngày, tháng, năm đã được lấy thành công",
                withdrawalHistory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WithdrawalHistoryResponse>>> getWithdrawalHistoryForSupplierAndDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryForSupplierAndDateRange(startDate, endDate, userPrincipal, pageable);

        ResponseWrapper<Page<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lịch sử rút tiền của nhà cung cấp lọc theo ngày, tháng, năm đã được lấy thành công",
                withdrawalHistory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter-by-auth-status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<WithdrawalHistoryResponse>>> getHistoriesByAuthAndStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String auth,
            @RequestParam String status) {

        // Gọi service và chỉ định quyền kiểm tra trong service
        List<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getHistoriesByAuthAndStatus(userPrincipal, auth, status);

        // Tạo response wrapper
        ResponseWrapper<List<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Lọc dữ liệu thành công",
                withdrawalHistory
        );

        // Trả về response
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
}



