package com.gizmo.gizmoshop.service.Image;

import com.gizmo.gizmoshop.exception.InvalidInputException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class ImageService {

    public static final String IMAGE_DIR_ACCOUNT = "image/account/";
    public static final String IMAGE_DIR_PRODUCT = "image/product/";
    public static final String IMAGE_DIR_CATEGORY = "image/category/";
    public static final String IMAGE_DIR_PRODUCTIMAGE = "image/productimage/";

    public String saveImage(MultipartFile image, String type) throws IOException {
        String IMAGE_DIR = getDirectory(type);

        if (image == null || image.isEmpty()) {
            throw new InvalidInputException("Hình ảnh không hợp lệ");
        }

        // Tạo thư mục nếu chưa tồn tại
        Path directoryPath = Paths.get(IMAGE_DIR);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Tạo tên tệp duy nhất với tiền tố "IMG", ngày giờ hiện tại và số ngẫu nhiên
        String originalFilename = image.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String uniqueFilename = generateUniqueFilename(fileExtension);

        byte[] bytes = image.getBytes();
        Path path = Paths.get(IMAGE_DIR + uniqueFilename);
        Files.write(path, bytes);
        return uniqueFilename;
    }

    public void deleteImage(String image, String type) throws IOException {
        String IMAGE_DIR = getDirectory(type);
        Path path = Paths.get(IMAGE_DIR + image);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    public byte[] loadImageAsResource(String imageName, String type) throws IOException {
        String IMAGE_DIR = getDirectory(type);
        Path imagePath = Paths.get(ResourceUtils.getFile(IMAGE_DIR + imageName).toURI());
        return Files.readAllBytes(imagePath);
    }

    private String getDirectory(String type) {
        switch (type.toLowerCase()) {
            case "account":
                return IMAGE_DIR_ACCOUNT;
            case "product":
                return IMAGE_DIR_PRODUCT;
            case "category":
                return IMAGE_DIR_CATEGORY;
            case "productimage":
                return IMAGE_DIR_PRODUCTIMAGE;
            default:
                throw new InvalidInputException("Loại hình ảnh không hợp lệ: " + type);
        }
    }

    private String generateUniqueFilename(String fileExtension) {
        String dateFormat = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randomNum = new Random().nextInt(1000); // Tạo số ngẫu nhiên từ 0 đến 999
        return "IMG_" + dateFormat + "_" + randomNum + fileExtension;
    }
}
