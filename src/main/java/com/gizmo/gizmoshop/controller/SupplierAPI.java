package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/supplier")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SupplierAPI {

    @Autowired
    private SupplierService supplierService;

    @PostMapping("/createOrder")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<Void>> createOrderBySupplier(@RequestBody OrderRequest orderRequest,
                                                                       @AuthenticationPrincipal UserPrincipal userPrincipal){
        supplierService.CreateOrder(orderRequest, userPrincipal.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đã tạo đơn hàng thành công", null));
    }

    @PutMapping(value = "/createOrder/{id}/updateImage")
    @PreAuthorize("hasRole('ROLE_SUPPLIER')")
    public ResponseEntity<ResponseWrapper<Void>> updateImageForOrder(
            @PathVariable long id,
            @RequestParam("file") MultipartFile file
    ){
        supplierService.saveImageForOrder(id, file);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK,"Hình ảnh đã được cập nhật thành công", null);
        return ResponseEntity.ok(response);
    }
}
