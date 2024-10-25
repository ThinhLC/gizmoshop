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

        // Lấy danh sách toàn bộ ProductInventory để ánh xạ số lượng
        List<ProductInventory> productInventories = productInventoryRepository.findAll();

        // Chuyển đổi danh sách sản phẩm thành danh sách ProductResponse
        return products.stream()
                .map(product -> {
                    // Lấy số lượng từ ProductInventory tương ứng
                    Optional<ProductInventory> inventoryOpt = productInventories.stream()
                            .filter(inventory -> inventory.getProduct().getId().equals(product.getId()))
                            .findFirst();

                    Integer quantity = inventoryOpt.map(ProductInventory::getQuantity).orElse(0); // Nếu không tìm thấy thì gán số lượng là 0

                    return convert(product, quantity); // Gọi phương thức convert và truyền số lượng
                })
                .collect(Collectors.toList());
    }

    public ProductResponse convert(Product product, Integer quantity) {
        return ProductResponse.builder()
                .productName(product.getName())
                .productImageUrl(product.getProductImageMappings().stream()
                        .map(productImageMapping -> new ProductImageMappingResponse(
                                productImageMapping.getId(),
                                null, // Không cần truyền ProductResponse
                                new ProductImageResponse(productImageMapping.getImage().getId(), productImageMapping.getImage().getFileDownloadUri())
                        ))
                        .collect(Collectors.toList()))
                .quantity(new ProductInventoryResponse(
                        null, // ID, có thể bỏ qua nếu không cần
                        product, // Sản phẩm
                        null, // Kho, có thể bỏ qua nếu không cần
                        quantity // Sử dụng số lượng lấy được
                ))
                .productPrice(product.getPrice())
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(new BrandResponseDto(product.getBrand().getId(), product.getBrand().getName(), product.getBrand().getDescription(), product.getBrand().getDeleted()))
                .productCategories(new CategoriesResponse(product.getCategory().getId(), product.getCategory().getName(), product.getCategory().getActive(), product.getCategory().getImageId(), product.getCategory().getCreateAt(), product.getCategory().getUpdateAt()))
                .productStatusResponse(new ProductStatusResponse(product.getStatus().getId(), product.getStatus().getName()))
                .author(new AccountResponse(
                        product.getAuthor().getId(),
                        product.getAuthor().getEmail(),
                        product.getAuthor().getFullname(),
                        product.getAuthor().getSdt(),
                        product.getAuthor().getBirthday(),
                        product.getAuthor().getImage(),
                        product.getAuthor().getExtra_info(),
                        product.getAuthor().getCreate_at(),
                        product.getAuthor().getUpdate_at(),
                        product.getAuthor().getDeleted(),
                        product.getAuthor().getRoleAccounts()
                ))
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .build();
    }

}
