package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.repository.ProductRepository;
import com.gizmo.gizmoshop.repository.VoucherRepository;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VoucherService {
    private final VoucherRepository voucherRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProductRepository productRepository;

    public Page<Voucher> findVoucherByCriteria(String inventoryName, Boolean active, Pageable pageable) {
        return voucherRepository.findByCriteria(inventoryName, active, pageable);
    }
    public VoucherResponse getVoucherById(long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));

        return buildVoucherResponse(voucher);
    }
    public VoucherResponse createVoucher(VoucherRequestDTO request) {
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
        Voucher savedVoucher = voucherRepository.save(voucher);
        return mapToVoucherResponse(savedVoucher);
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
                if (existingVoucher.getImage() != null && !existingVoucher.getImage().isEmpty()  && !existingVoucher.getImage().equals("")) {
                    imageService.deleteImage(existingVoucher.getImage().trim(), "voucher");
                    System.out.println("Deleting image " + existingVoucher.getImage());
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

    public List<VoucherCardResponseDto> getVoucherCard() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<Voucher> listVoucher = voucherRepository.findAll();

        return listVoucher.stream()
                .map(voucher -> {
                    boolean hasRemainingDays = voucher.getValidTo().isAfter(currentDateTime); // Kiểm tra còn ngày sử dụng
                    boolean hasRemainingUses = voucher.getUsageLimit() == null || (voucher.getUsedCount() < voucher.getUsageLimit()); // Kiểm tra còn số lượng dùng

                    return new VoucherCardResponseDto(
                            voucher.getId(),
                            voucher.getStatus() != null ? voucher.getStatus() : false, // Gán giá trị status
                            hasRemainingDays,
                            hasRemainingUses
                    );
                })
                .collect(Collectors.toList()); // Thu thập kết quả vào danh sách
    }

    public List<Voucher> getActiveAndValidVouchers() {
            LocalDateTime currentDateTime = LocalDateTime.now();  // Lấy thời gian hiện tại
            List<Voucher> activeVouchers = voucherRepository.findActiveAndValidVouchers(currentDateTime);

            // Lọc ra các voucher có trạng thái là 1, còn hạn sử dụng và số lượng còn lại
            return activeVouchers.stream()
                    .filter(voucher -> voucher.getStatus() == true && //
                            (voucher.getUsageLimit() == null || voucher.getUsedCount() < voucher.getUsageLimit()) && // Còn lượt sử dụng
                            (voucher.getValidFrom().isBefore(currentDateTime) || voucher.getValidFrom().isEqual(currentDateTime)) && // Còn hạn sử dụng
                            (voucher.getValidTo().isAfter(currentDateTime) || voucher.getValidTo().isEqual(currentDateTime))) // Còn hạn sử dụng
                    .collect(Collectors.toList());
    }

    public VoucherResponse getVoucherByIdUser(long id) {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Lấy thời gian hiện tại
        try {
            Voucher voucher = voucherRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("Không có voucher id: " + id));

            // Kiểm tra các điều kiện
            boolean isInStock = (voucher.getUsageLimit() == null || voucher.getUsedCount() < voucher.getUsageLimit());
            boolean isValidDate = (voucher.getValidFrom().isBefore(currentDateTime) || voucher.getValidFrom().isEqual(currentDateTime)) &&
                    (voucher.getValidTo().isAfter(currentDateTime) || voucher.getValidTo().isEqual(currentDateTime));
            boolean isStatusActive = voucher.getStatus() == true; // Trạng thái là 1

            // Nếu voucher không hợp lệ, ném ngoại lệ
            if (!isInStock || !isValidDate || !isStatusActive) {
                throw new ValidationException("Voucher không tồn tại: " + id);
            }

            // Chuyển đổi voucher thành VoucherResponse
            return buildVoucherResponse(voucher);
        } catch (Exception e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    public Page<Voucher> findVouchersForUser(String code, Boolean status, Pageable pageable) {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Lấy thời gian hiện tại

        // Truy vấn voucher từ database với các điều kiện
        return voucherRepository.findVouchersForUser(code, status, currentDateTime, pageable);
    }





}
