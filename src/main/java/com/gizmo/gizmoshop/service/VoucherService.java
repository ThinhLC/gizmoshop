package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherCardResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.VoucherResponse;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.VoucherRequestDTO;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.OrderDetailRepository;
import com.gizmo.gizmoshop.repository.OrderRepository;
import com.gizmo.gizmoshop.repository.VoucherRepository;
import com.gizmo.gizmoshop.repository.VoucherToOrderRepository;
import com.gizmo.gizmoshop.service.Image.ImageService;
import com.gizmo.gizmoshop.service.product.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
    private VoucherToOrderRepository voucherToOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GenericExporter<VoucherResponse> genericExporter;

    @Autowired
    private OrderDetailRepository orderDetailRepository;
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
    public List<VoucherResponse> getAllVouchersWithOrders() {
        List<Voucher> vouchers = voucherRepository.findAll();

        return vouchers.stream()
                .filter(voucher -> !voucherToOrderRepository.findByVoucher(voucher).isEmpty()) // Chỉ lấy voucher có người dùng
                .map(voucher -> {
            // Lấy tất cả VoucherToOrder liên quan đến voucher hiện tại
            List<VoucherToOrder> voucherToOrders = voucherToOrderRepository.findByVoucher(voucher); // Đảm bảo phương thức này tồn tại trong repository

            List<OrderResponse> orderResponses = voucherToOrders.stream()
                    .map(voucherToOrder -> {
                        Order order = voucherToOrder.getOrder();

                        // Lấy thông tin tài khoản từ đơn hàng
                        Account account = order.getIdAccount(); // Giả sử phương thức này tồn tại

                        // Ánh xạ thông tin tài khoản
                        AccountResponse accountResponse = new AccountResponse(
                                account.getId(),
                                account.getEmail(),
                                account.getFullname(),
                                account.getSdt(),
                                account.getBirthday(),
                                account.getImage(),
                                account.getExtra_info(), // Hoặc trường tương tự
                                account.getCreate_at(), // Nếu bạn có trường này trong Account
                                account.getUpdate_at(), // Nếu bạn có trường này trong Account
                                account.getDeleted(),
                                null// Nếu bạn có trường này trong Account
                        );

                        // Lấy danh sách OrderDetail cho đơn hàng hiện tại
                        List<OrderDetail> orderDetails = orderDetailRepository.findByIdOrder(order);
                        List<OrderDetailsResponse> orderDetailsResponses = orderDetails.stream()
                                .map(orderDetail -> new OrderDetailsResponse(
                                        orderDetail.getId(),
                                        new ProductResponse(
                                                orderDetail.getIdProduct().getId(),
                                                orderDetail.getIdProduct().getName(),
                                                orderDetail.getIdProduct().getPrice(),
                                                null // Hoặc giá trị thích hợp khác
                                        ),
                                        orderDetail.getPrice(),
                                        orderDetail.getQuantity(),
                                        orderDetail.getTotal()
                                )).collect(Collectors.toList());

                        return new OrderResponse(
                                order.getId(),
                                accountResponse,
                                new OrderStatusResponse(order.getOrderStatus().getId(), order.getOrderStatus().getStatus()),
                                order.getNote(),
                                order.getOderAcreage(),
                                order.getPaymentMethods(),
                                order.getTotalPrice(),
                                order.getTotalWeight(),
                                order.getDistance(),
                                order.getDeliveryTime(),
                                order.getFixedCost(),
                                order.getImage(),
                                order.getOrderCode(),
                                order.getCreateOderTime(),
                                orderDetailsResponses
                        );
                    }).collect(Collectors.toList());

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
                    orderResponses
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void importVouchers(MultipartFile file) throws IOException {
        List<VoucherResponse> voucherResponses = genericExporter.importFromExcel(file, VoucherResponse.class);

        for (VoucherResponse voucherResponse : voucherResponses) {
            Long id = voucherResponse.getId();
            System.out.println("Đang xử lý Voucher ID: " + id);

            Voucher voucher;
            if (id != null) {
                Optional<Voucher> existingVoucherOpt = voucherRepository.findById(id);
                if (existingVoucherOpt.isPresent()) {
                    voucher = existingVoucherOpt.get();
                    voucher.setCode(voucherResponse.getCode());
                    voucher.setDescription(voucherResponse.getDescription());
                    voucher.setDiscountAmount(voucherResponse.getDiscountAmount());
                    voucher.setDiscountPercent(voucherResponse.getDiscountPercent());
                    voucher.setMaxDiscountAmount(voucherResponse.getMaxDiscountAmount());
                    voucher.setMinimumOrderValue(voucherResponse.getMinimumOrderValue());
                    voucher.setValidFrom(voucherResponse.getValidFrom());
                    voucher.setValidTo(voucherResponse.getValidTo());
                    voucher.setUsageLimit(voucherResponse.getUsageLimit());
                    voucher.setUsedCount(voucherResponse.getUsedCount());
                    voucher.setStatus(voucherResponse.getStatus());
                    voucher.setUpdatedAt(LocalDateTime.now());
                    voucherRepository.save(voucher);
                    System.out.println("Đã cập nhật voucher tồn tại với ID: " + id);
                } else {
                    voucher = new Voucher();
                    voucher.setId(id);
                    voucher.setCode(voucherResponse.getCode() == null ? " " : voucherResponse.getCode());
                    voucher.setDescription(voucherResponse.getDescription() == null ? "" : voucherResponse.getDescription());
                    voucher.setDiscountAmount(voucherResponse.getDiscountAmount() == null ? BigDecimal.ZERO : voucherResponse.getDiscountAmount());
                    voucher.setDiscountPercent(voucherResponse.getDiscountPercent() == null ? BigDecimal.ZERO : voucherResponse.getDiscountPercent());
                    voucher.setMaxDiscountAmount(voucherResponse.getMaxDiscountAmount() == null ? BigDecimal.ZERO : voucherResponse.getMaxDiscountAmount());
                    voucher.setMinimumOrderValue(voucherResponse.getMinimumOrderValue() == null ? BigDecimal.ZERO : voucherResponse.getMinimumOrderValue());
                    voucher.setValidFrom(voucherResponse.getValidFrom() == null ? LocalDateTime.now() : voucherResponse.getValidFrom());
                    voucher.setValidTo(voucherResponse.getValidTo() == null ? LocalDateTime.now().plusDays(1) : voucherResponse.getValidTo());
                    voucher.setUsageLimit(voucherResponse.getUsageLimit() == null ? 0 : voucherResponse.getUsageLimit());
                    voucher.setUsedCount(voucherResponse.getUsedCount() == null ? 0 : voucherResponse.getUsedCount());
                    voucher.setStatus(voucherResponse.getStatus());
                    voucher.setCreatedAt(LocalDateTime.now());
                    voucher.setUpdatedAt(LocalDateTime.now());
                    voucherRepository.save(voucher);
                    System.out.println("Đã tạo voucher mới với ID: " + id);
                }

            } else {
                voucher = new Voucher();
                voucher.setCode(voucherResponse.getCode() == null ? " " : voucherResponse.getCode());
                voucher.setDescription(voucherResponse.getDescription() == null ? "" : voucherResponse.getDescription());
                voucher.setDiscountAmount(voucherResponse.getDiscountAmount() == null ? BigDecimal.ZERO : voucherResponse.getDiscountAmount());
                voucher.setDiscountPercent(voucherResponse.getDiscountPercent() == null ? BigDecimal.ZERO : voucherResponse.getDiscountPercent());
                voucher.setMaxDiscountAmount(voucherResponse.getMaxDiscountAmount() == null ? BigDecimal.ZERO : voucherResponse.getMaxDiscountAmount());
                voucher.setMinimumOrderValue(voucherResponse.getMinimumOrderValue() == null ? BigDecimal.ZERO : voucherResponse.getMinimumOrderValue());
                voucher.setValidFrom(voucherResponse.getValidFrom() == null ? LocalDateTime.now() : voucherResponse.getValidFrom());
                voucher.setValidTo(voucherResponse.getValidTo() == null ? LocalDateTime.now().plusDays(1) : voucherResponse.getValidTo());
                voucher.setUsageLimit(voucherResponse.getUsageLimit() == null ? 0 : voucherResponse.getUsageLimit());
                voucher.setUsedCount(voucherResponse.getUsedCount() == null ? 0 : voucherResponse.getUsedCount());
                voucher.setStatus(voucherResponse.getStatus());
                voucher.setCreatedAt(LocalDateTime.now());
                voucher.setUpdatedAt(LocalDateTime.now());
                voucherRepository.save(voucher);
                System.out.println("Đã tạo voucher mới mà không có ID.");
            }
        }
    }





    public byte[] exportVouchers(List<String> excludedFields) {
        List<Voucher> vouchers = voucherRepository.findAll();
        List<VoucherResponse> voucherResponses = convertToDto(vouchers);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            genericExporter.exportToExcel(voucherResponses, VoucherResponse.class, excludedFields, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu voucher");
        }
    }

    public byte[] exportVoucherById(Long id, List<String> excludedFields) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy voucher với ID: " + id));
        List<VoucherResponse> voucherResponses = convertToDto(List.of(voucher));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            genericExporter.exportToExcel(voucherResponses, VoucherResponse.class, excludedFields, outputStream);
            return outputStream.toByteArray(); // Trả về dữ liệu đã ghi vào outputStream dưới dạng byte[]
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu voucher với ID: " + id);
        }
    }
    private List<VoucherResponse> convertToDto(List<Voucher> vouchers) {
        return vouchers.stream()
                .map(voucher -> VoucherResponse.builder()
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
                        .build())
                .collect(Collectors.toList());
    }



}

