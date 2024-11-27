package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.OrderResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/t/delivery")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DeliveryApi {
    @Autowired
    private DeliveryService deliveryService;
    @GetMapping("/all-order")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<Page<OrderResponse>>> findAllOrderForShipper(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false , defaultValue = "ORDER_CUSTOMER") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
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
        Page<OrderResponse> result = deliveryService.getAllOrderForDelivery(keyword, startDate , endDate , type, pageable);
        ResponseWrapper<Page<OrderResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy đơn hàng cho nhân viên giao hàng thành công", result);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/assign-order-to-shipper/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SHIPPER')")
    public ResponseEntity<ResponseWrapper<?>> assignOrderToShipper(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ){
        deliveryService.assignOrderToShipper(orderId,userPrincipal.getUserId());
        ResponseWrapper<Page<OrderResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Nhận đơn hàng thành công", null);
        return ResponseEntity.ok(response);
    }
}
