package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.ProductImageMappingRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import com.gizmo.gizmoshop.utils.ConvertEntityToResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;

    ConvertEntityToResponse convertEntityToResponse = new ConvertEntityToResponse();


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public Page<ProductResponse> getAllProducts(String productName, Boolean active, int page, int limit, Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));

        Page<Product> productPage = productRepository.findAllByCriteria(productName, active, pageable);

        // Chuyển đổi Product thành ProductResponse
        return productPage.map(this::mapToProductResponse);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productImageMappingResponse(getProductImageMappings(product.getId()))
                .productInventoryResponse(getProductInventoryResponse(product))
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(convertEntityToResponse.mapToStatusResponse(product.getStatus()))
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .author(convertEntityToResponse.author(product.getAuthor()))
                .build();
    }

    public List<ProductImageMappingResponse> getProductImageMappings(Long productId) {
        List<ProductImageMapping> mappings = productImageMappingRepository.findByProductId(productId);

        if(mappings == null){
            return null;
        }

        return mappings.stream()
                .map(mapping -> ProductImageMappingResponse.builder()
                        .id(mapping.getId())
                        .idProduct(mapping.getProduct().getId()) // Lấy ID của Product
                        .idProductImage(mapping.getImage().getId()) // Lấy ID của ProductImage
                        .fileDownloadUri(mapping.getImage().getFileDownloadUri()) // Lấy đường dẫn hình ảnh
                        .build())
                .collect(Collectors.toList());
    }


    private ProductInventoryResponse getProductInventoryResponse(Product product) {
        ProductInventory productInventory = product.getProductInventory(); // Lấy ProductInventory từ Product

        if (productInventory == null) {
            return null;
        }

        return ProductInventoryResponse.builder()
                .id(productInventory.getId())
                .inventory(InventoryResponse.builder()
                        .id(productInventory.getInventory().getId())
                        .inventoryName(productInventory.getInventory().getInventoryName())
                        .build())
                .quantity(productInventory.getQuantity())
                .build();
    }

}
