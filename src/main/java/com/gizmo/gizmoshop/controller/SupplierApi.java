package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.SupplierService;
import com.gizmo.gizmoshop.service.WithdrawalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/supplier/t")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SupplierApi {
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private WithdrawalHistoryService withdrawalHistoryService;



    @GetMapping("/info")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')") // Chỉ cho phép ROLE_SUPPLIER truy cập
    public ResponseEntity<ResponseWrapper<SupplierDto>> supplierInfo(
            @AuthenticationPrincipal UserPrincipal user
            ) {
        SupplierDto info = supplierService.getInfo(user.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin của đối tác thành công",info));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> withdraw(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SupplierDto supplier
    ) {
       supplierService.withdraw(user.getUserId(),supplier);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Rút tiền thành công",null));
    }


    //đơn hàng đã giao dịch của supplier theo trạng thái
    @GetMapping("/count-order-by-status")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> OrderCountBySupplier(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam List<String> statusIds
    ) {
        SupplierDto count = supplierService.OrderCountBySupplier(user.getUserId(),statusIds);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy số lượng đơn hàng của đối tác thành công",count));
    }

    @GetMapping("/Order-Total-Price-By-Supplier")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> OrderTotalPriceBySupplier(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam List<String> statusIds,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            endDate = Date.from(lastDayOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        }
        SupplierDto count = supplierService.OrderTotalPriceBySupplier(user.getUserId(),statusIds,startDate,endDate);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy số doanh thu của đối tác thành công",count));
    }


    @GetMapping("/product-supplier")
            @PreAuthorize("hasRole('ROLE_SUPPLIER')")
            public ResponseEntity<ResponseWrapper<Page<ProductResponse>>> getSupplierProducts(
                    @AuthenticationPrincipal UserPrincipal user,
                    @RequestParam(required = false) String keyword,
                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                    @RequestParam(required = false) String orderCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {
                Long supplierId = user.getUserId();

                String sortField = "id";
                Sort.Direction sortDirection = Sort.Direction.ASC;

                if (sort.isPresent()) {
                    String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        // Tạo đối tượng Pageable với các tham số phân trang và sắp xếp
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        // Gọi service để lấy danh sách đơn hàng của nhà cung cấp
        Page<ProductResponse> orderResponses = supplierService.getProductsBySupplier(
                supplierId, keyword, startDate, endDate, pageable);

        ResponseWrapper<Page<ProductResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", orderResponses);
        return ResponseEntity.ok(responseWrapper);
    }
}
