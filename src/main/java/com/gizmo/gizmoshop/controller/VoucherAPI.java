package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherResponse;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/public/voucher")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class VoucherAPI {
    private final VoucherService voucherService;
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<Voucher>>> findVouchersByCriteria(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
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
        Page<Voucher> vouchers = voucherService.findVoucherByCriteria(code, status, pageable);
        ResponseWrapper<Page<Voucher>> response = new ResponseWrapper<>(HttpStatus.OK, "Vouchers fetched successfully", vouchers);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<VoucherResponse>> getVoucher(@PathVariable Long id) {
        VoucherResponse voucherResponse = voucherService.getVoucherById(id);
        ResponseWrapper<VoucherResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", voucherResponse);
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<Voucher>> createVoucher(@RequestBody VoucherRequestDTO request) {
        Voucher voucherResponse = voucherService.createVoucher(request);
        ResponseWrapper<Voucher> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", voucherResponse);
        return ResponseEntity.ok(responseWrapper);
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<VoucherResponse>> updateVoucher(@PathVariable Long id, @RequestBody VoucherRequestDTO request) {
        VoucherResponse updatedVoucher = voucherService.updateVoucher(id, request);
        ResponseWrapper<VoucherResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Voucher đã được cập nhật", updatedVoucher);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changestatus/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<VoucherResponse>> changeStatus(@PathVariable Long id) {
        VoucherResponse updatedVoucher = voucherService.changeStatusById(id);
        ResponseWrapper<VoucherResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật trạng thái thành công",
                updatedVoucher
        );
        return ResponseEntity.ok(response);
    }

}
