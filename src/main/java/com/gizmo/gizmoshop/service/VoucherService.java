package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherResponse;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.VoucherRepository;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoucherService {
    private final VoucherRepository voucherRepository;
    @Autowired
    private ImageService imageService;
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
        voucher.setImage(request.getImage());
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
                .image(voucher.getImage())
                .build();
    }
    public VoucherResponse updateImage(long id, Optional<MultipartFile> file) {
        Optional<Voucher> existingVoucherOpt = voucherRepository.findById(id);
        if (existingVoucherOpt.isEmpty()) {
            throw new BrandNotFoundException("Danh mục không tồn tại với ID: " + id);
        }

        Voucher existingVoucher = existingVoucherOpt.get();

        // Kiểm tra tệp hình ảnh có được cung cấp không
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                // Nếu danh mục đã có hình ảnh, xóa hình ảnh cũ
                if (!existingVoucher.getImage().isEmpty()) {
                    imageService.deleteImage(existingVoucher.getImage(), "voucher");
                }


                String imagePath = imageService.saveImage(file.get(), "voucher");
                existingVoucher.setImage(imagePath); // Cập nhật ID hình ảnh mới
            } catch (IOException e) {
                throw new InvalidInputException("Lỗi khi xử lý hình ảnh: " + e.getMessage());
            }
        } else {
            System.out.println("Tệp hình ảnh không được cung cấp.");
        }

        // Cập nhật thời gian sửa đổi
        existingVoucher.setUpdatedAt(LocalDateTime.now());

        // Lưu danh mục đã cập nhật vào cơ sở dữ liệu
        Voucher updatedVoucher = voucherRepository.save(existingVoucher);

        return buildVoucherResponse(updatedVoucher);

    }
    public byte[] getImage(String filename){
        byte[] imageData = new byte[0];
        try {
            imageData = imageService.loadImageAsResource(filename,"voucher");
        } catch (IOException e) {
            throw new InvalidInputException("Could not load");
        }

        return imageData;
    }
}
