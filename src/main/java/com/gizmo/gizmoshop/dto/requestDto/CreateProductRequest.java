package com.gizmo.gizmoshop.dto.requestDto;

import com.gizmo.gizmoshop.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private Long authorId;  // ID của tác giả
    private String productName;
    private Long productCategoryId;
    private Long productPrice;
    private String thumbnail;
    private String productLongDescription;
    private String productShortDescription;
    private Float productWeight;
    private Float productArea; // diện tích
    private Float productVolume; // thể tích
    private Float Width;
    private Float productHeight;
    private Float productLength;
    private Long productBrandId;
    private Long productStatusResponseId;

    private LocalDateTime productCreationDate;
    private LocalDateTime productUpdateDate;
}
