package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.exception.InvalidInputException;
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
public class ImageApi {

    private final ImageService imageService;

    private static final String IMAGE_DIR = "image/directory/account/"; // Đường dẫn thư mục lưu trữ hình ảnh

    @PostMapping("/image/upload")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "oldImageName", required = false) Optional<String> oldImageName) {

        // Kiểm tra xem file hình ảnh có được cung cấp không
        if (image == null || image.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(HttpStatus.BAD_REQUEST, "Không có hình ảnh nào được tải lên", null));
        }

        try {
            // Nếu có tên hình ảnh cũ, thì xóa hình ảnh cũ trước khi lưu hình ảnh mới
            oldImageName.ifPresent(oldImage -> {
                try {
                    imageService.deleteImage(oldImage, IMAGE_DIR);
                } catch (IOException e) {
                    throw new InvalidInputException("Lỗi khi xóa hình ảnh cũ: " + e.getMessage());
                }
            });

            // Lưu hình ảnh mới và trả về tên file mới
            String newFilename = imageService.saveImage(image, IMAGE_DIR);
            ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được tải lên thành công", newFilename);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            // Xử lý lỗi trong quá trình tải lên hình ảnh
            ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi tải hình ảnh: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/image/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> deleteImage(@RequestParam("imageName") String imageName) {

        if (imageName == null || imageName.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseWrapper<>(HttpStatus.BAD_REQUEST, "Tên hình ảnh không hợp lệ", null));
        }

        try {
            imageService.deleteImage(imageName, IMAGE_DIR);
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được xóa thành công", imageName));

        } catch (IOException e) {
            // Xử lý lỗi khi xóa hình ảnh
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi xóa hình ảnh: " + e.getMessage(), null));
        }
    }

    @GetMapping("/image/load")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> loadImage(@RequestParam("imageName") String imageName) {
        try {
            byte[] imageData = imageService.loadImageAsResource(imageName, IMAGE_DIR);
            return ResponseEntity.ok().body(imageData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
