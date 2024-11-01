package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin/product")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductAPI {
    @Autowired
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<ProductResponse>>> findAll() {
        List<ProductResponse> products = productService.getAllProducts();
        ResponseWrapper<List<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<ProductResponse>>> getAllProducts(
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "deleted", required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort) {

        Page<ProductResponse> products = productService.getAllProducts(productName, active, page, limit, sort);
        ResponseWrapper<Page<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Products fetched successfully", products);
        return ResponseEntity.ok(response);
    }
}
