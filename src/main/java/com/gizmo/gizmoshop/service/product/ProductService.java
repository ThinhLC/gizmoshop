package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.ProductImageMappingRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;

    public List<ProductResponse> findAll() {
        // Lấy danh sách tất cả sản phẩm
        List<Product> products = productRepository.findAll();

        // Chuyển đổi danh sách sản phẩm thành danh sách ProductResponse
        return products.stream()
                .map(product -> {
                    // Lấy số lượng từ ProductInventory tương ứng
                    Integer quantity = getProductQuantity(product);

                    // Lấy danh sách ảnh tương ứng với sản phẩm
                    List<ProductImageMappingResponse> imageMappings = getProductImageMappings(product);
                    System.out.println(imageMappings.size());
                    return convert(product, quantity, imageMappings); // Gọi phương thức convert và truyền số lượng và ảnh
                })
                .collect(Collectors.toList());
    }

    private Integer getProductQuantity(Product product) {
        // Lấy số lượng từ ProductInventory tương ứng
        return productInventoryRepository.findByProductId(product.getId())
                .map(ProductInventory::getQuantity)
                .orElse(0); // Nếu không tìm thấy thì gán số lượng là 0
    }

    private List<ProductImageMappingResponse> getProductImageMappings(Product product) {
        return product.getProductImageMappings().stream()
                .map(productImageMapping -> new ProductImageMappingResponse(
                        productImageMapping.getId(),
                        null, // Không cần truyền ProductResponse
                        new ProductImageResponse(productImageMapping.getImage().getId(), productImageMapping.getImage().getFileDownloadUri())
                ))
                .collect(Collectors.toList());
    }

    public ProductResponse convert(Product product, Integer quantity, List<ProductImageMappingResponse> imageMappings) {
        // Lấy danh sách hình ảnh
        imageMappings = product.getProductImageMappings().stream()
                .map(productImageMapping -> new ProductImageMappingResponse(
                        productImageMapping.getId(),
                        null, // Không cần truyền ProductResponse
                        new ProductImageResponse(productImageMapping.getImage().getId(), productImageMapping.getImage().getFileDownloadUri())
                ))
                .collect(Collectors.toList());

        // Lấy thông tin author
        Account author = product.getAuthor(); // Lấy thông tin tác giả

        return ProductResponse.builder()
                .productName(product.getName())
                .productImageUrl(imageMappings) // Sử dụng danh sách hình ảnh
                .quantity(new ProductInventoryResponse(null, null, null, quantity)) // Sử dụng ProductInventoryResponse với số lượng
                .productPrice(product.getPrice())
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea()) // Diện tích
                .productVolume(product.getVolume()) // Thể tích
                .productBrand(new BrandResponseDto(product.getBrand().getId(), product.getBrand().getName(), product.getBrand().getDescription(), product.getBrand().getDeleted()))
                .productCategories(new CategoriesResponse(product.getCategory().getId(), product.getCategory().getName(), product.getCategory().getActive(), product.getCategory().getImageId(), product.getCategory().getCreateAt(), product.getCategory().getUpdateAt()))
                .productStatusResponse(new ProductStatusResponse(product.getStatus().getId(), product.getStatus().getName()))
                .author(new AccountResponse(
                        author != null ? author.getId() : null,
                        author != null ? author.getEmail() : null,
                        author != null ? author.getFullname() : null,
                        author != null ? author.getSdt() : null,
                        author != null ? author.getBirthday() : null,
                        author != null ? author.getImage() : null,
                        author != null ? author.getExtra_info() : null,
                        author != null ? author.getCreate_at() : null,
                        author != null ? author.getUpdate_at() : null,
                        author != null ? author.getDeleted() : null,
                        author != null ? author.getRoleAccounts() : null
                ))
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .build();
    }
}
