package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductResponse {
    private String productName;
    private List<String> productImageUrl;
    private Integer quantity;
    private Long productPrice;
    private String productLongDescription;
    private String productShortDescription;
    private Float productWeight;
    private Float productArea;//diện tích
    private Float productVolume;//thể tích
    private String productBrand;
    private String productCategories;
    private String productStatus;
    private String author;
    private LocalDateTime productCreationDate;
    private LocalDateTime productUpdateDate;
}
