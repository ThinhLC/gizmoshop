package com.gizmo.gizmoshop.controller.product;


import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/product")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductAPI {
    @Autowired
    ProductService  productService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<ProductResponse>>> findAll() {
        List<ProductResponse> products = productService.findAll();
        ResponseWrapper<List<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", products);

        return ResponseEntity.ok(response);
    }



//    @GetMapping("/list/products")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<ResponseWrapper<List<ProductResponse>>> getAllProduct() {
//        List<ProductResponse> product = productService.getAllProducts();
//        ResponseWrapper<List<ProductResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", product);
//        return ResponseEntity.ok(responseWrapper);
//    }
//    @GetMapping("/page/product")
//    @PreAuthorize("permitAll()")
//    public ResponseEntity<ResponseWrapper<Page<ProductResponse>>> getAllProduct(
//            @RequestParam(value = "keyword", required = false) String keyword,
//            @RequestParam(value = "deleted", required = false) Boolean available,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "5") int size,
//            @RequestParam(required = false) Optional<String> sort) {
//        String sortField = "id";
//        Sort.Direction sortDirection = Sort.Direction.ASC;
//
//        if (sort.isPresent()) {
//            String[] sortParams = sort.get().split(",");
//            sortField = sortParams[0];
//            if (sortParams.length > 1) {
//                sortDirection = Sort.Direction.fromString(sortParams[1]);
//            }
//        }
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(sortDirection, sortField)));
//        Page<ProductResponse> productResponses = productService.getAllProductsWithPagination(keyword, available, pageable);
//        ResponseWrapper<Page<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Categories fetched successfully", productResponses);
//
//        return ResponseEntity.ok(response);
//    }

}
