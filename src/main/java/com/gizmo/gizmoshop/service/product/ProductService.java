package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.dto.requestDto.ProductRequest;
import com.gizmo.gizmoshop.exception.DuplicateBrandException;
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


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findByDeletedFalse();
        return products.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse convertToProductResponse(Product product) {
        //Lấy số lượng sản phẩm
        Optional<ProductInventory> optionalProductInventory = productInventoryRepository.findById(product.getId());
        Integer quantity = optionalProductInventory.map(ProductInventory::getQuantity).orElse(0);

        List<ProductImageMapping> imageMappings = productImageMappingRepository.findByProductId(product.getId());
        List<String> productImageUrls = imageMappings.stream()
                .map(imageMapping -> imageMapping.getImage().getFileDownloadUri())
                .toList();

        return ProductResponse.builder()
                .productName(product.getName())
                .productImageUrl(productImageUrls)
                .quantity(quantity)
                .productPrice(product.getPrice())
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(product.getBrand().getName())
                .productCategories(product.getCategory().getName())
                .productStatus(product.getStatus().getName())
                .author(product.getAuthor() != null ? product.getAuthor().getFullname() : null)
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .build();
    }
    public Page<ProductResponse> getAllProductsWithPagination(String keyword, Boolean available, Pageable pageable) {
        // Lấy danh sách sản phẩm từ repository
        Page<Product> products = productRepository.findByKeywordAndAvailability(keyword, available, pageable);
        // Chuyển đổi từ Product sang ProductResponse
        return products.map(this::convertToProductResponse);
    }
}
