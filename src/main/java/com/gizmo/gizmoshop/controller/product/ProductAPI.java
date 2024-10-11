package com.gizmo.gizmoshop.controller.product;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductAPI {
    @Autowired
    private ProductService productService;
    @GetMapping("/list/product")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<ProductResponse>> getAllBrands(Pageable pageable){
        Page<ProductResponse> brandPage = productService.getAllProduct(pageable);
        return new ResponseEntity<>(brandPage, HttpStatus.OK);
    }
}
