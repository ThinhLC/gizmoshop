package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherResponse;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.VoucherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoucherService {
    private final VoucherRepository voucherRepository;
    public Page<Voucher> findVoucherByCriteria(String inventoryName, Boolean active, Pageable pageable) {
        return voucherRepository.findByCriteria(inventoryName, active, pageable);
    }
    public VoucherResponse getVoucherById(long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        return buildVoucherResponse(voucher);
    }
    public Voucher createVoucher(VoucherRequestDTO request) {
        Voucher voucher = new Voucher();
        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountAmount(request.getDiscountAmount());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinimumOrderValue(request.getMinimumOrderValue());
        voucher.setValidFrom(request.getValidFrom());
        voucher.setValidTo(request.getValidTo());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setUsedCount(0);
        voucher.setStatus(request.getStatus());
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setUpdatedAt(LocalDateTime.now());
        return voucherRepository.save(voucher);
    }
    public VoucherResponse updateVoucher(Long id, VoucherRequestDTO request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Voucher not found with id: " + id));
        voucher.setCode(request.getCode());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountAmount(request.getDiscountAmount());
        voucher.setDiscountPercent(request.getDiscountPercent());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinimumOrderValue(request.getMinimumOrderValue());
        voucher.setValidFrom(request.getValidFrom());
        voucher.setValidTo(request.getValidTo());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStatus(request.getStatus());
        voucher.setCreatedAt(request.getCreatedAt());
        voucher.setUpdatedAt(LocalDateTime.now());
        Voucher updatedVoucher = voucherRepository.save(voucher);
        return buildVoucherResponse(updatedVoucher);
    }
    public VoucherResponse changeStatusById(long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Voucher not found with id: " + id));
        voucher.setStatus(!voucher.getStatus());
        Voucher updatedVoucher = voucherRepository.save(voucher);
        return buildVoucherResponse(updatedVoucher);
    }

    private VoucherResponse mapToVoucherResponse(Voucher voucher) {
        VoucherResponse response = new VoucherResponse();
        response.setId(voucher.getId());
        response.setCode(voucher.getCode());
        response.setDescription(voucher.getDescription());
        response.setDiscountAmount(voucher.getDiscountAmount());
        response.setDiscountPercent(voucher.getDiscountPercent());
        response.setMaxDiscountAmount(voucher.getMaxDiscountAmount());
        response.setMinimumOrderValue(voucher.getMinimumOrderValue());
        response.setValidFrom(voucher.getValidFrom());
        response.setValidTo(voucher.getValidTo());
        response.setUsageLimit(voucher.getUsageLimit());
        response.setUsedCount(voucher.getUsedCount());
        response.setStatus(voucher.getStatus());
        response.setCreatedAt(voucher.getCreatedAt());
        response.setUpdatedAt(voucher.getUpdatedAt());
        return response;
    }

    private VoucherResponse buildVoucherResponse(Voucher voucher) {
        return VoucherResponse.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .discountAmount(voucher.getDiscountAmount())
                .discountPercent(voucher.getDiscountPercent())
                .maxDiscountAmount(voucher.getMaxDiscountAmount())
                .minimumOrderValue(voucher.getMinimumOrderValue())
                .validFrom(voucher.getValidFrom())
                .validTo(voucher.getValidTo())
                .usageLimit(voucher.getUsageLimit())
                .usedCount(voucher.getUsedCount())
                .status(voucher.getStatus())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
