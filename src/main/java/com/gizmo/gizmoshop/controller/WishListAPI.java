package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WishListItemResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WishListResponse;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WishListService;
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
@RequestMapping("/api/public/WishList")
@RequiredArgsConstructor
@CrossOrigin("*")
public class WishListAPI {
    @Autowired
    WishListService wishListService;

    @PostMapping("/favourite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<WishListResponse>> addProductToWishlist(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long productId) {
        Long accountId = user.getUserId();
        WishListResponse wishListItemResponse = wishListService.toggleProductInWishlist(accountId, productId);
        ResponseWrapper<WishListResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", wishListItemResponse );
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/getAllFavourite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Page<WishListResponse>>> getAllFavouriteProducts(
            @AuthenticationPrincipal UserPrincipal user,  // Lấy thông tin người dùng hiện tại
            @RequestParam(defaultValue = "0") int page,   // Trang hiện tại (mặc định 0)
            @RequestParam(defaultValue = "7") int limit,  // Số lượng sản phẩm mỗi trang (mặc định 7)
            @RequestParam(required = false) Optional<String> sort) {  // Tham số sắp xếp (tuỳ chọn)

        Long accountId = user.getUserId();  // Lấy ID người dùng từ thông tin đăng nhập

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

        // Gọi service để lấy danh sách sản phẩm yêu thích của người dùng với phân trang
        Page<WishListResponse> wishListResponses = wishListService.getAllFavouriteProducts(accountId, pageable);

        // Tạo ResponseWrapper và trả về kết quả
        ResponseWrapper<Page<WishListResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", wishListResponses);
        return ResponseEntity.ok(responseWrapper);
    }
}
