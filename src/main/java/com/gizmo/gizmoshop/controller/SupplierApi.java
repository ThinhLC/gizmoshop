package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.ProductAndOrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.InvalidTokenException;
import com.gizmo.gizmoshop.exception.NotFoundException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<OrderResponse>> createOrderBySupplier(@RequestBody OrderRequest orderRequest,
                                                                                @AuthenticationPrincipal UserPrincipal userPrincipal) {
        OrderResponse orderResponse = supplierService.CreateOrder(orderRequest, userPrincipal.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đã tạo đơn hàng thành công", orderResponse));
    }

    @PostMapping("/create-product")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<ProductResponse>> createProduct(
            @RequestBody ProductAndOrderRequest productAndOrderRequest,
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam long idOrder) {
        ResponseWrapper<ProductResponse> response;
        ProductResponse productResponse = supplierService.createProductBySupplier(productAndOrderRequest.getCreateProductRequest(), productAndOrderRequest.getOrderRequest(), user.getUserId(), idOrder);
        response = new ResponseWrapper<>(HttpStatus.OK, "Đã thêm sản phẩm thành công", productResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/createOrder/{id}/updateImage")    
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<Void>> updateImageForOrder(
            @PathVariable long id,
            @RequestParam("file") MultipartFile file
    ) {
        supplierService.saveImageForOrder(id, file);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được cập nhật thành công", null);
        return ResponseEntity.ok(response);
    }

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

    @PutMapping("/supplier-update/{orderId}")
    public ResponseEntity<?> updateOrderBySupplier(
            @PathVariable long orderId,
            @RequestBody OrderRequest orderRequest,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            supplierService.UpdateOrderBySupplier(orderRequest, orderId, user.getUserId());
            return ResponseEntity.ok("Cập nhật đơn hàng thành công!");
        } catch (NotFoundException | InvalidInputException | InvalidTokenException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống");
        }
    }

    @GetMapping("/Order")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<Page<OrderResponse>>> findAllOrderForSupplier(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam Optional<String> sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idStatus,
            @AuthenticationPrincipal UserPrincipal user
    ){
        Page<OrderResponse> orderResponses = supplierService.findAllOrderForSupplier(page, limit, sort, keyword, idStatus, user.getUserId());
        ResponseWrapper<Page<OrderResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Tìm toàn bộ order thành công", orderResponses);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/approve-order/{orderId}")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<String>> approveOrderBySupplier(
            @PathVariable Long orderId,
            @RequestParam Boolean accept,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            // Gọi service để xử lý việc duyệt đơn hàng
            supplierService.ApproveOrderBySupplier(orderId, accept, user.getUserId());

            // Đóng gói phản hồi thành công với ResponseWrapper
            ResponseWrapper<String> response = new ResponseWrapper<>(
                    HttpStatus.OK, "Cập nhật trạng thái đơn hàng thành công!", "Success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Nếu có lỗi, trả về phản hồi lỗi với ResponseWrapper
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(
                    HttpStatus.BAD_REQUEST, "Lỗi: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/register-cancel-Supplier")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<Void>> cancelSupplier(@AuthenticationPrincipal UserPrincipal userPrincipal , @RequestParam(required = false) Long idwallet , @RequestParam(required = false) Long idAddress) {
        // Kiểm tra nếu idwallet không được cung cấp
        if (idwallet == null) {
            throw new InvalidInputException("ID Wallet là tham số bắt buộc");
        }
        if (idAddress == null) {
            throw new InvalidInputException("ID Address là tham số bắt buộc");
        }
        long accountId = userPrincipal.getUserId();
        supplierService.registerCancelSupplier(accountId, idwallet, idAddress);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng kí hủy hợp tác thành công", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/show-all-cancel-supplier-requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<SupplierDto>>> getCancelSupplierRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "id"));

        Page<SupplierDto> supplierDtos = supplierService.getCancelSupplierRequests(pageable);

        ResponseWrapper<Page<SupplierDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách yêu cầu hủy bỏ tư cách thành công", supplierDtos);

        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/accept-cancel-supplier/{accountId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> cancelSupplier(@PathVariable long accountId) {
        supplierService.AcceptCancelSupplier(accountId);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Đăng ký hủy hợp tác và tạo đơn hàng thành công", null);
        return ResponseEntity.ok(response);
    }
}
