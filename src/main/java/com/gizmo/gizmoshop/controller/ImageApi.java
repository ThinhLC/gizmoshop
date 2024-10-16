package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;

import com.gizmo.gizmoshop.service.Image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ImageApi {

    private final ImageService imageService;


    @PostMapping("/image/upload/{type}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Đây là phần đẩy hình ảnh lên",
            description = "API này được dùng để đẩy hỉnh ảnh lên, type ở đây có thể được sử dụng khi có sự thay thế cho nhiều phần khác nhau," +
                    "type có thể được thay thế bằng các phần như là account, prodcut, productimage,category",
            tags = {"imageapi"})
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @PathVariable("type") String type) throws IOException {
        String savedImageName = imageService.saveImage(image, type);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được tải lên thành công", savedImageName));
    }

    @DeleteMapping("/image/delete/{type}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Đây là phần xóa hình ảnh",
            description = "API này được dùng để đẩy hỉnh ảnh lên, type ở đây có thể được sử dụng khi có sự thay thế cho nhiều phần khác nhau," +
                    "type có thể được thay thế bằng các phần như là account, prodcut, productimage,category",
            tags = {"imageapi"})
    public ResponseEntity<ResponseWrapper<String>> deleteImage(@RequestParam("imageName") String imageName, @PathVariable("type") String type) throws IOException {
        imageService.deleteImage(imageName, type);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được xóa thành công", imageName));
    }

    @GetMapping("/image/load/{type}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Đây là phần tải hình ảnh",
            description = "API này được dùng để đẩy hỉnh ảnh lên, type ở đây có thể được sử dụng khi có sự thay thế cho nhiều phần khác nhau," +
                    "type có thể được thay thế bằng các phần như là account, prodcut, productimage,category",
            tags = {"imageapi"})
    public ResponseEntity<byte[]> loadImage(@RequestParam("imageName") String imageName, @PathVariable("type") String type) throws IOException {
        byte[] imageData = imageService.loadImageAsResource(imageName, type);
        return ResponseEntity.ok().body(imageData);
    }
}
