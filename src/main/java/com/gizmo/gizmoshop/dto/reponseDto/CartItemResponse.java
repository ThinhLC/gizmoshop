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
}
