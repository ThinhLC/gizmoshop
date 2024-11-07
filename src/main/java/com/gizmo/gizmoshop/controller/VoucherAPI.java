package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherCardResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherResponse;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // lấy tất cả trả về kiểu boLean
    @GetMapping("/card")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<VoucherCardResponseDto>>> getAllVoucher(){
        List<VoucherCardResponseDto> VoucherCardResponseDto = voucherService.getVoucherCard();
        ResponseWrapper<List<VoucherCardResponseDto>> response = new ResponseWrapper<>(HttpStatus.OK, "Vouchers fetched successfully", VoucherCardResponseDto);
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
    ResponseEntity<ResponseWrapper<VoucherResponse>> createVoucher(@RequestBody VoucherRequestDTO request) {
        VoucherResponse voucherResponse = voucherService.createVoucher(request);
        ResponseWrapper<VoucherResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", voucherResponse);
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
    @PutMapping("/{id}/updateimage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<VoucherResponse>> updateImage(
            @PathVariable Long id,
            @RequestParam("file") Optional<MultipartFile> file) {

        // Gọi phương thức cập nhật hình ảnh từ service
        VoucherResponse updateVoucher = voucherService.updateImage(id,file);
        ResponseWrapper<VoucherResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Hình ảnh đã được cập nhật thành công",
                updateVoucher
        );
        return ResponseEntity.ok(response);
    }

    //API dành cho người dùng (Còn thời gian sử dụng và trạng thái = true)
    @GetMapping("/getallforuser")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<VoucherResponse>>> getActiveVouchers() {
        List<Voucher> vouchers = voucherService.getActiveAndValidVouchers();

        // Chuyển đổi danh sách Voucher sang VoucherResponse
        List<VoucherResponse> voucherResponses = vouchers.stream()
                .map(voucher -> new VoucherResponse(
                        voucher.getId(),
                        voucher.getCode(),
                        voucher.getDescription(),
                        voucher.getDiscountAmount(),
                        voucher.getDiscountPercent(),
                        voucher.getMaxDiscountAmount(),
                        voucher.getMinimumOrderValue(),
                        voucher.getValidFrom(),
                        voucher.getValidTo(),
                        voucher.getUsageLimit(),
                        voucher.getUsedCount(),
                        voucher.getStatus(),
                        voucher.getCreatedAt(),
                        voucher.getUpdatedAt(),
                        voucher.getImage(),
                        null// Nếu có
                ))
                .collect(Collectors.toList());

        ResponseWrapper<List<VoucherResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", voucherResponses);
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/getallforuser/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<VoucherResponse>> getVoucherById(@PathVariable Long id) {
        VoucherResponse voucher = voucherService.getVoucherByIdUser(id);

        if (voucher != null) {
            // Chuyển đổi Voucher sang VoucherResponse
            VoucherResponse voucherResponse = new VoucherResponse(
                    voucher.getId(),
                    voucher.getCode(),
                    voucher.getDescription(),
                    voucher.getDiscountAmount(),
                    voucher.getDiscountPercent(),
                    voucher.getMaxDiscountAmount(),
                    voucher.getMinimumOrderValue(),
                    voucher.getValidFrom(),
                    voucher.getValidTo(),
                    voucher.getUsageLimit(),
                    voucher.getUsedCount(),
                    voucher.getStatus(),
                    voucher.getCreatedAt(),
                    voucher.getUpdatedAt(),
                    voucher.getImage(),
                    null// Nếu có
            );

            ResponseWrapper<VoucherResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", voucherResponse);
            return ResponseEntity.ok(responseWrapper);
        } else {
            // Trả về lỗi nếu không tìm thấy voucher
            ResponseWrapper<VoucherResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.NOT_FOUND, "Voucher not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseWrapper);
        }
    }

    @GetMapping("/getallforuser/page")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<VoucherResponse>>> findVouchersForUser(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int limit,
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
        Page<Voucher> vouchers = voucherService.findVouchersForUser(code, status, pageable);

        // Chuyển đổi từ Voucher sang VoucherResponse
        Page<VoucherResponse> voucherResponses = vouchers.map(this::buildVoucherResponse);
        ResponseWrapper<Page<VoucherResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Vouchers fetched successfully", voucherResponses);
        return ResponseEntity.ok(response);
    }
    private VoucherResponse buildVoucherResponse(Voucher voucher) {
        // Chuyển đổi Voucher thành VoucherResponse
        return new VoucherResponse(
                voucher.getId(),
                voucher.getCode(),
                voucher.getDescription(),
                voucher.getDiscountAmount(),
                voucher.getDiscountPercent(),
                voucher.getMaxDiscountAmount(),
                voucher.getMinimumOrderValue(),
                voucher.getValidFrom(),
                voucher.getValidTo(),
                voucher.getUsageLimit(),
                voucher.getUsedCount(),
                voucher.getStatus(),
                voucher.getCreatedAt(),
                voucher.getUpdatedAt(),
                voucher.getImage(),
                null // Nếu có
        );
    }
    @GetMapping("/VoucherToOrder")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<VoucherResponse>>> getAllVoucherToOrder() {
        List<VoucherResponse> voucherResponses = voucherService.getAllVouchersWithOrders();
        ResponseWrapper<List<VoucherResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Vouchers fetched successfully",voucherResponses);
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> importVouchers(@RequestParam("file") MultipartFile file) throws IOException {
        voucherService.importVouchers(file);
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "Import thành công!", null);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportVouchers() {
        List<String> excludedFields = Arrays.asList("image", "createdAt", "updatedAt");
        byte[] excelData = voucherService.exportVouchers(excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=vouchers_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // Cho phép frontend đọc được header resp

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    @GetMapping("/export/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportVoucherById(@PathVariable Long id) {
        List<String> excludedFields = Arrays.asList("image", "createdAt", "updatedAt");
        byte[] excelData = voucherService.exportVoucherById(id, excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=voucher_" + id + "_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // Cho phép frontend đọc được header resp

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
