package com.gizmo.gizmoshop.controller;


import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;

import com.gizmo.gizmoshop.service.Image.ImageService;
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
    public ResponseEntity<ResponseWrapper<String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            @PathVariable("type") String type) throws IOException {
        String savedImageName = imageService.saveImage(image, type);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được tải lên thành công", savedImageName));
    }

    @DeleteMapping("/image/delete/{type}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<String>> deleteImage(@RequestParam("imageName") String imageName, @PathVariable("type") String type) throws IOException {
        imageService.deleteImage(imageName, type);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Hình ảnh đã được xóa thành công", imageName));
    }

    @GetMapping("/image/load/{type}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<byte[]> loadImage(@RequestParam("imageName") String imageName, @PathVariable("type") String type) throws IOException {
        byte[] imageData = imageService.loadImageAsResource(imageName, type);
        return ResponseEntity.ok().body(imageData);
    }
}
