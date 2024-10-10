package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.service.Brand.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BrandApi {

    @Autowired
    private BrandService brandService;

    /**
     * API tạo thương hiệu - chỉ dành cho ADMIN và STAFF
     */
    @PostMapping("/brand/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')") // Chỉ cho phép ROLE_ADMIN và ROLE_STAFF truy cập
    public ResponseEntity<ResponseWrapper<BrandResponseDto>> createBrand(@RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto newBrand = brandService.createBrand(brandRequestDto);
        ResponseWrapper<BrandResponseDto> response = new ResponseWrapper<>(HttpStatus.CREATED, "Thương hiệu đã được tạo thành công", newBrand);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * API cập nhật thông tin thương hiệu - chỉ dành cho ADMIN và STAFF
     */
    @PutMapping("/brand/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')") // Chỉ cho phép ROLE_ADMIN và ROLE_STAFF truy cập
    public ResponseEntity<ResponseWrapper<BrandResponseDto>> updateBrand(@PathVariable Long id, @RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto updatedBrand = brandService.updateBrand(id, brandRequestDto);
        ResponseWrapper<BrandResponseDto> response = new ResponseWrapper<>(HttpStatus.OK, "Thương hiệu đã được cập nhật", updatedBrand);
        return ResponseEntity.ok(response);
    }

    /**
     * API lấy danh sách tất cả thương hiệu (ai cũng có thể truy cập)
     */
    @GetMapping("/brand")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<BrandResponseDto>> getAllBrands(Pageable pageable) {
        Page<BrandResponseDto> brandPage = brandService.getAllBrands(pageable);
        return new ResponseEntity<>(brandPage, HttpStatus.OK);
    }
}