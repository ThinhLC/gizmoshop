package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishListResponse {
    private Long id;
    private AccountResponse accountResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<WishListItemResponse> items;  // Danh sách các mục yêu thích, mỗi mục chứa thông tin sản phẩm và ngày tạo
}
