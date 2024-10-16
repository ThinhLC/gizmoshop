package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;

import com.gizmo.gizmoshop.service.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ImageApi {

    private final ImageService imageService;

    @PostMapping("/image/upload")
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người tải lên hình ảnh
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @RequestParam("image") MultipartFile image) {
        // Gọi service để lưu hình ảnh, không xử lý lỗi tại đây
        String savedImageName = imageService.saveImage(image, ImageService.IMAGE_DIR);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được tải lên thành công", savedImageName));
    }

    @DeleteMapping("/image/delete")
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người xóa hình ảnh
    public ResponseEntity<ResponseWrapper<String>> deleteImage(@RequestParam("imageName") String imageName) {
        // Gọi service để xóa hình ảnh, không xử lý lỗi tại đây
        imageService.deleteImage(imageName, ImageService.IMAGE_DIR);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được xóa thành công", imageName));
    }

    @GetMapping("/image/load")
    @PreAuthorize("permitAll()") // Cho phép tất cả mọi người tải hình ảnh
    public ResponseEntity<byte[]> loadImage(@RequestParam("imageName") String imageName) {
        // Gọi service để tải hình ảnh, không xử lý lỗi tại đây
        byte[] imageData = imageService.loadImageAsResource(imageName, ImageService.IMAGE_DIR);
        return ResponseEntity.ok().body(imageData);
    }
}
