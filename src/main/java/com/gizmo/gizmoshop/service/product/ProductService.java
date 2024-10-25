package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.ProductImageMappingRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;


//    public List<ProductResponse> getAllProducts() {
//        List<Product> products = productRepository.findAll();
//        return products.stream()
//                .map(this::convertToProductResponse)
//                .collect(Collectors.toList());
//    }
//
////    public Page<ProductResponse> getAllProductsWithPagination(String keyword, Boolean available, Pageable pageable) {
////        // Lấy danh sách sản phẩm từ repository
////        Page<Product> products = productRepository.findByKeywordAndAvailability(keyword, available, pageable);
////        // Chuyển đổi từ Product sang ProductResponse
////        return products.map(this::convertToProductResponse);
////    }
//
//    public ProductResponse convertToProductResponse(Product product) {
//
//        //Lấy số lượng sản phẩ
//        return  ProductResponse.builder()
//                .productName(product.getName())
////                .productImageUrl()
////                .quantity(product.)
//                .productPrice(product.getPrice())
//                .productLongDescription(product.getLongDescription())
//                .productShortDescription(product.getShortDescription())
//                .productArea(product.getArea())
//                .productVolume(product.getVolume())
//                .productCategories(product.getCategory())
//                .productBrand(product.getBrand().getName())
//                .productCreationDate(product.getCreateAt())
//                .productUpdateDate(product.getUpdateAt())
//                .author(product.getAuthor().getFullname())
//                .productStatus(product.getStatus().getName())
//                .productWeight(product.getWeight())
//                //au
//                .build();
//    }

}
