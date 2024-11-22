package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.ProductAndOrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.ProductRequest;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.SupplierService;
import com.gizmo.gizmoshop.service.WithdrawalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin của đối tác thành công", info));
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> withdraw(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody SupplierDto supplier
    ) {
        supplierService.withdraw(user.getUserId(), supplier);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Rút tiền thành công", null));
    }


    //đơn hàng đã giao dịch của supplier theo trạng thái
    @GetMapping("/count-order-by-status")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> OrderCountBySupplier(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam List<String> statusIds
    ) {
        SupplierDto count = supplierService.OrderCountBySupplier(user.getUserId(), statusIds);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy số lượng đơn hàng của đối tác thành công", count));
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
}
