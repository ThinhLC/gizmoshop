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
            if (category != null) {
                this.id = category.getId();
                this.name = category.getName();
                this.active = category.getActive();
                this.image = category.getImageId();
                this.createAt = category.getCreateAt();
                this.updateAt = category.getUpdateAt();
            }
        }

    }
