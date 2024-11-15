package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/public/product")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProductClientAPI {
    @Autowired
    ProductService productService;

    @GetMapping


    @PreAuthorize("permitAll()") //sẽ thay đổi
    public ResponseEntity<ResponseWrapper<List<ProductResponse>>> findAll() {
        List<ProductResponse> products = productService.getAllProducts();
        ResponseWrapper<List<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    @Operation(description = "API này dùng được đa chức năng:\n" +
            "1: Giảm giá sốc\n" +
            "http://localhost:8081/api/public/product/all?sortFieldCase=discountproduct\n" +
            "Tương tự như vậy, chỉ cần truyền tham số 'view' vào sortFieldCase sẽ là tìm toàn bộ sản phẩm có lượt view nhiều nhất, \n" +
            "2:Tìm kiếm: \n" +
            "Chỉ cần truyền tham số vào param keyword\n" +
            "http://localhost:8081/api/public/product/all?keyword=tên mầy mún tìm\n" +
            "3:Có thể lọc theo khoảng giá\n" +
            "Chỉ cần truyền tham số vào param price1 và price2\n" +
            "Ví dụ: http://localhost:8081/api/public/product/all?price=29000&price2=5000000000\n" +
            "4:Có thể vừa tìm kiếm theo keyword và cả tìm kiếm theo giá tiền tùy vào những tham số muốn truyền\n")

    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<ProductResponse>>> findAllProductForClient(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int limit,
            @RequestParam Optional<String> sort,
            @RequestParam(required = false) String sortFieldCase,
            @RequestParam(required = false) Long price1,
            @RequestParam(required = false) Long price2,
            @RequestParam(required = false) String keyword
    ) {
        Page<ProductResponse> products = productService.findAllProductsForClient(page, limit, sort, price1,price2, sortFieldCase, keyword);
        ResponseWrapper<Page<ProductResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK, "Lấy danh sách sản phẩm thành công", products);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product-detail")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<ProductResponse>> findProductDetailForClient(@RequestParam Long idProduct) {
        ProductResponse productResponse = productService.findProductDetailForClient(idProduct);

        if (productResponse == null) {
            ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy sản phẩm với ID: " + idProduct,
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Nếu tìm thấy sản phẩm, trả về thông tin sản phẩm trong ResponseWrapper
        ResponseWrapper<ProductResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Tìm sản phẩm thành công",
                productResponse
        );
        return ResponseEntity.ok(response);
    }

}
