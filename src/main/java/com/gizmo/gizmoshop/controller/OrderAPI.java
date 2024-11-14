package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.CartItemResponse;
import com.gizmo.gizmoshop.dto.reponseDto.OrderResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.OrderService;
import lombok.Data;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderAPI {
    @Autowired
    OrderService orderService;

    @GetMapping("/OrderForUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<OrderResponse>>> getOrdersByPhoneOrOrderCode(
            @RequestParam(required = false) Long idStatus, // ID trạng thái đơn hàng (tuỳ chọn)
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, // Ngày bắt đầu (tuỳ chọn)
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, // Ngày kết thúc (tuỳ chọn)
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
            @RequestParam(defaultValue = "7") int limit, // Số lượng đơn hàng mỗi trang (mặc định là 7)
            @RequestParam(required = false) Optional<String> sort,
            @AuthenticationPrincipal UserPrincipal user) {

        Long accountId = user.getUserId();
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

        // Gọi service để lấy danh sách đơn hàng tìm theo số điện thoại hoặc mã đơn hàng
        Page<OrderResponse> orderResponses = orderService.findOrdersByUserIdAndStatusAndDateRange(accountId,idStatus, startDate,endDate, pageable);

        // Tạo ResponseWrapper và trả về kết quả
        ResponseWrapper<Page<OrderResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", orderResponses);
        return ResponseEntity.ok(responseWrapper);
    }


}
