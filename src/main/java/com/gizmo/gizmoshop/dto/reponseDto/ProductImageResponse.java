package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductImageResponse {
    private Long id;
    private String fileDownloadUri;
}
