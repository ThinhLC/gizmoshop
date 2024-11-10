package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse {
    private Long id;
    private String fileDownloadUri;
}