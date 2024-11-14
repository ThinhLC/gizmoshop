package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.CartItemResponse;
import com.gizmo.gizmoshop.dto.reponseDto.OrderResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderAPI {
    @Autowired
    OrderService orderService;

    @GetMapping("/Oder")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<OrderResponse>>> getOrdersByPhoneOrOrderCode(
            @RequestParam(required = false) String sdt, // Số điện thoại (tuỳ chọn)
            @RequestParam(required = false) String orderCode, // Mã đơn hàng (tuỳ chọn)
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
            @RequestParam(defaultValue = "7") int limit, // Số lượng đơn hàng mỗi trang (mặc định là 7)
            @RequestParam(required = false) Optional<String> sort) { // Tham số sắp xếp (tuỳ chọn)

        // Xử lý tham số sắp xếp (nếu có)
        String sortField = "id";  // Trường sắp xếp mặc định
        Sort.Direction sortDirection = Sort.Direction.ASC;  // Hướng sắp xếp mặc định (tăng dần)

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
        Page<OrderResponse> orderResponses = orderService.findOrdersByPhoneOrOrderCode(sdt, orderCode, pageable);

        // Tạo ResponseWrapper và trả về kết quả
        ResponseWrapper<Page<OrderResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", orderResponses);
        return ResponseEntity.ok(responseWrapper);
    }
}
