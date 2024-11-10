package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.ProductDemoResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.ProductImageRequest;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/admin/product")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductAdminAPI {
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
            @RequestParam(required = false) Optional<String> sort,
            @RequestParam(required = false) Boolean isSupplier) {

        Page<ProductResponse> products = productService.getAllProducts(productName, active, page, limit, sort, isSupplier);
        ResponseWrapper<Page<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy sản phẩm thành công", products);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<ProductResponse>> createProduct(@RequestBody CreateProductRequest createProductRequest,
                                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProductResponse newProduct = productService.createProduct(createProductRequest, userPrincipal.getUserId());
        ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Sản phẩm đã được tạo thành công", newProduct);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateimage")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<ProductResponse>> updateProductImage(
            @RequestParam long productId,
            @RequestParam(required = false) List<MultipartFile> files) {
        try {
            ProductResponse updatedProduct = productService.updateImage(productId, files);
            ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh sản phẩm đã được cập nhật thành công", updatedProduct);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.NOT_FOUND, "Lỗi khi cập nhật hình ảnh", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IOException e) {
            ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi cập nhật hình ảnh", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không xác định", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/demo")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<ProductDemoResponse>>> getProductsStatistics(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam int page) {
        List<ProductDemoResponse> products = productService.getProducts(month, year, page);

        ResponseWrapper<List<ProductDemoResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Products statistics fetched successfully",
                products
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<ProductResponse>> updateProduct(
            @PathVariable("id") Long productId,
            @RequestBody CreateProductRequest createProductRequest) {
        ProductResponse updatedProduct = productService.updateProduct(productId, createProductRequest);
        ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Sản phẩm đã được cập nhật thành công", updatedProduct);
        return ResponseEntity.ok(response);
    }


}
