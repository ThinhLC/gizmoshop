package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String productName;
    private List<ProductImageMappingResponse> productImageMappingResponse;
    private ProductInventoryResponse productInventoryResponse;
    private Long productPrice;
    private int discountProduct;
    private String thumbnail;
    private String productLongDescription;
    private String productShortDescription;
    private Float productWeight;
    private Long view;
    private Boolean isSupplier;
    private Float productArea;//diện tích
    private Float productVolume;//thể tích
    private Float productHeight;// chiều cao
    private Float productLength;
    private BrandResponseDto productBrand;
    private CategoriesResponse productCategories;
    private ProductStatusResponse productStatusResponse;
    private AccountResponse author;


    private LocalDateTime productCreationDate;
    private LocalDateTime productUpdateDate;
    public ProductResponse(Long id, String productName, List<ProductImageMappingResponse> productImageMappingResponse,
                           ProductInventoryResponse productInventoryResponse, Long productPrice, int discountProduct,
                           String thumbnail, String productLongDescription, String productShortDescription,
                           Float productWeight, Long view, Boolean isSupplier, Float productArea,
                           Float productVolume, Float productHeight, Float productLength,
                           BrandResponseDto productBrand, CategoriesResponse productCategories,
                           ProductStatusResponse productStatusResponse, AccountResponse author,
                           LocalDateTime productCreationDate, LocalDateTime productUpdateDate) {
        this.id = id;
        this.productName = productName;
        this.productInventoryResponse = productInventoryResponse;
        this.productPrice = productPrice;
        this.discountProduct = discountProduct;
        this.thumbnail = thumbnail;
        this.productLongDescription = productLongDescription;
        this.productShortDescription = productShortDescription;
        this.productWeight = productWeight;
        this.view = view;
        this.isSupplier = isSupplier;
        this.productArea = productArea;
        this.productVolume = productVolume;
        this.productHeight = productHeight;
        this.productLength = productLength;
        this.productBrand = productBrand;
        this.productCategories = productCategories;
        this.productStatusResponse = productStatusResponse;
        this.author = author;
        this.productCreationDate = productCreationDate;
        this.productUpdateDate = productUpdateDate;
    }
    public ProductResponse(String productName, Long productPrice, String productShortDescription) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productShortDescription = productShortDescription;
    }

    public ProductResponse(Long id, String name, Long price, String shortDescription) {
        this.id = id;
        this.productName = name;
        this.productPrice = price;
        this.productShortDescription = shortDescription;
    }
}
