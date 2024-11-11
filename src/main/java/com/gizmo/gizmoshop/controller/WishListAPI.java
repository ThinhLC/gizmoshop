package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WishListItemResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WishListResponse;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public/WishList")
@RequiredArgsConstructor
@CrossOrigin("*")
public class WishListAPI {
    @Autowired
    WishListService wishListService;

    @PostMapping("/favourite")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<WishListResponse>> addProductToWishlist(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long productId) {
        Long accountId = user.getUserId();
        WishListResponse wishListItemResponse = wishListService.toggleProductInWishlist(accountId, productId);
        ResponseWrapper<WishListResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", wishListItemResponse );
        return ResponseEntity.ok(responseWrapper);
    }
}
