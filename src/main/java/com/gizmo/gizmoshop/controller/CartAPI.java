package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.CartItemResponse;
import com.gizmo.gizmoshop.dto.reponseDto.CartResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/cart")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CartAPI {
    @Autowired
    private CartService cartService;

    @GetMapping("/view")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<List<CartItemResponse>>> viewCart(
            @AuthenticationPrincipal UserPrincipal user) {
        // Gọi service để lấy danh sách sản phẩm trong giỏ hàng
        List<CartItemResponse> cartItems = cartService.getAllCartItems(user.getUserId());
        // Đóng gói phản hồi với ResponseWrapper
        ResponseWrapper<List<CartItemResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Cart items retrieved successfully", cartItems);
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<CartResponse>> addProductToCart(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Long quantity) {
    CartResponse cartResponse = cartService.addProductToCart(user.getUserId(), productId, quantity);
        ResponseWrapper<CartResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", cartResponse);
        return ResponseEntity.ok(responseWrapper);
    }

    @DeleteMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<CartResponse>> removeProductFromCart(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long productId) {
        // Xóa sản phẩm khỏi giỏ hàng
        CartResponse cartResponse = cartService.removeProductFromCart(user.getUserId(), productId);

        // Tạo phản hồi thành công
        ResponseWrapper<CartResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Product removed successfully", cartResponse);
        return ResponseEntity.ok(responseWrapper);
    }

    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Void>> clearCart(
            @AuthenticationPrincipal UserPrincipal user) {
        // Gọi service để xóa tất cả sản phẩm trong giỏ hàng
        cartService.clearCart(user.getUserId());
        // Tạo phản hồi thành công
        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Cart cleared successfully", null);
        return ResponseEntity.ok(responseWrapper);
    }
}

