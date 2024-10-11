package com.gizmo.gizmoshop.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @NotBlank(message = "Mô tả ngắn không được để trống")
    private String shortDescription;

    private String longDescription;

    @NotNull(message = "Giá sản phẩm không được để trống")
    private Long price;

    private Float acreage;

    private Float weight;

    private String thumbnail;

    private String imageId;

    @NotNull(message = "ID của tác giả không được để trống")
    private Long authorId; // ID của tác giả

    @NotNull(message = "ID của danh mục không được để trống")
    private Long categoryId; // ID của danh mục

    @NotNull(message = "ID của thương hiệu không được để trống")
    private Long brandId; // ID của thương hiệu

    @NotNull(message = "ID của trạng thái không được để trống")
    private Long statusId; // ID của trạng thái

    private String sku; // SKU của sản phẩm
}
