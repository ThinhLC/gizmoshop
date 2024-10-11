package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private Boolean active;
    private Boolean deleted;
    private Float acreage;
    private Float weight;
    private Long price;
    private Long view;
    private String thumbnail;
    private String imageId;
    private Long authorId; // Map từ Account entity
    private Long categoryId; // Map từ Categories entity
    private Long brandId; // Map từ ProductBrand entity
    private Long statusId; // Map từ StatusProduct entity
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String sku;
}
