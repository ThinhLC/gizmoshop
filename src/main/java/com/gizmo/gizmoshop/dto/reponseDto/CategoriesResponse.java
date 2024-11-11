package com.gizmo.gizmoshop.dto.reponseDto;

import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.excel.ExcludeFromExport;
import jakarta.persistence.Transient;
import lombok.*;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class CategoriesResponse {
        private Long id;
        private String name;
        private Boolean active;
        @ExcludeFromExport
        private String image;
        @ExcludeFromExport
        private LocalDateTime createAt;
        @ExcludeFromExport
        private LocalDateTime updateAt;

        public CategoriesResponse(Categories category) {
        }

        public CategoriesResponse(Long id, String name, String image) {
            this.id = id;
            this.name = name;
            this.image = image;
        }
    }
