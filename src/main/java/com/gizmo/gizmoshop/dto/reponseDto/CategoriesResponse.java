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
public class CategoriesResponse {
    private Long id;
    private String name;
    private Boolean active;
    private String image;
    private LocalDateTime createAt; // Thêm trường này
    private LocalDateTime updateAt;
}
