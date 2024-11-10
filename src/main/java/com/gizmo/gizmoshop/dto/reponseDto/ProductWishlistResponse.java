package com.gizmo.gizmoshop.dto.reponseDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductWishlistResponse {
    private Long productId;
    private String productName;
    private Long likeCount;
}
