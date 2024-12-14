package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.AddressAccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/Address")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AddressApi {
    @Autowired
    private AddressService addressService;

    // Lấy danh sách địa chỉ của tài khoản đang đăng nhập
    @GetMapping("/getall")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<List<AddressAccountResponse>>> getAddressesByCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AddressAccountResponse> addresses = addressService.getAllAddressesByAccountId(userPrincipal);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách địa chỉ thành công", addresses));
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<AddressAccountResponse>> createAddress(
            @RequestBody AddressAccountResponse newAddress,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AddressAccountResponse createdAddress = addressService.createAddress(newAddress, userPrincipal);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.CREATED, "Thêm địa chỉ thành công", createdAddress));
    }

    @PutMapping(value = "/update/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<AddressAccountResponse>> updateAddress(
            @PathVariable Long addressId,
            @RequestBody AddressAccountResponse updatedAddress,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AddressAccountResponse updatedAddressDTO = addressService.updateAddress(addressId, updatedAddress, userPrincipal.getUserId());
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật địa chỉ thành công", updatedAddressDTO));
    }

    //
    @DeleteMapping("/delete/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Void>> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        addressService.deleteAddress(addressId, userPrincipal);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đã xóa địa chỉ thành công", null));
    }

    @PatchMapping("/setDeleted/{addressId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseWrapper<Void>> setDeletedAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
            addressService.setDeletedAddress(addressId, userPrincipal);
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK,"Đã ngưng hoạt động địa chỉ", null));
    }

}
