package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishListItemResponse {
    private Long id;
    private ProductResponse product; // DTO cho Product
    private LocalDateTime createDate;
}
