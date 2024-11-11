package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.CartItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private ProductResponse productId;
    private Long quantity;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public CartItemResponse(Long id, Long quantity, LocalDateTime createDate, LocalDateTime updateDate) {
        this.id = id;
        this.quantity = quantity;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public CartItemResponse(CartItems cartItems) {
        this.id = cartItems.getId();
        List<ProductImageMappingResponse> productImageMappingResponses = cartItems.getProductId().getProductImageMappings().stream()
                .map(image -> new ProductImageMappingResponse(image)) // Giả sử bạn đã có constructor ánh xạ từ entity ProductImageMapping sang ProductImageMappingResponse.
                .collect(Collectors.toList());
        // Chỉ lấy các thông tin cần thiết cho ProductResponse
        this.productId = new ProductResponse(
                cartItems.getProductId().getId(),
                cartItems.getProductId().getName(),
                productImageMappingResponses,  // Danh sách hình ảnh đã được chuyển đổi sang DTO
                cartItems.getProductId().getPrice(),
                cartItems.getProductId().getThumbnail(),
                cartItems.getProductId().getLongDescription(),
                cartItems.getProductId().getShortDescription(),
                cartItems.getProductId().getWeight(),
                cartItems.getProductId().getArea(),
                cartItems.getProductId().getVolume(),
                cartItems.getProductId().getHeight(),
                cartItems.getProductId().getLength()
        );
        this.quantity = cartItems.getQuantity();
        this.createDate = cartItems.getCreateDate();
        this.updateDate = cartItems.getUpdateDate();
    }
}
