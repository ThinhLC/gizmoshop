package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.dto.requestDto.ProductRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.DuplicateBrandException;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToDto);
    }
    private ProductResponse mapToDto(Product product) {
        return ProductResponse.builder() // Sử dụng builder để tạo đối tượng
                .id(product.getId())
                .name(product.getName())
                .shortDescription(product.getShortDescription())
                .longDescription(product.getLongDescription())
                .active(product.getActive())
                .deleted(product.getDeleted())
                .acreage(product.getAcreage())
                .weight(product.getWeight())
                .price(product.getPrice())
                .view(product.getView())
                .thumbnail(product.getThumbnail())
//                .imageId(product.getImageId())
                .authorId(product.getAuthor() != null ? product.getAuthor().getId() : null) // Lấy ID của author
                .brandId(product.getBrand() != null ? product.getBrand().getId() : null)   // Lấy ID của brand
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null) // Lấy ID của category
                .statusId(product.getStatus() != null ? product.getStatus().getId() : null)   // Lấy ID của status
                .createAt(product.getCreateAt())
                .updateAt(product.getUpdateAt())
//                .sku(product.getSku())
                .build(); // Xây dựng đối tượng ProductResponse
    }
}
