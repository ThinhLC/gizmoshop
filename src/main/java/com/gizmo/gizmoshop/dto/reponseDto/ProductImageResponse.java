package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse {
    private Long id;
    private String fileDownloadUri;
}
