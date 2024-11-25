package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/admin/shipper")
@CrossOrigin("*")
public class ShipperApi {

    @Autowired
    private SupplierService supplierService;


    @PatchMapping("/approve-order/{orderId}")
    @PreAuthorize("hasRole('ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<Void>> approveOrderByShipper(
            @PathVariable("orderId") Long orderId,
            @RequestParam Boolean accept) {
        try {
            supplierService.ApproveOrderByShipper(orderId, accept);
            String message = accept
                    ? "Đơn hàng đã được shipper phê duyệt."
                    : "Đơn hàng đã bị shipper từ chối.";
            ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, message, null);
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND, ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Xảy ra lỗi không xác định.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
