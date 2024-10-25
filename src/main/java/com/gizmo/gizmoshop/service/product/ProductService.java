package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.ProductImageMappingRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        List<Product> products = productRepository.findAll(); // Lấy danh sách tất cả sản phẩm
        return products.stream()
                .map(this::convert) // Sử dụng phương thức convert để chuyển đổi thành ProductResponse
                .collect(Collectors.toList()); // Trả về danh sách ProductResponse
    }


    public ProductResponse convert(Product product) {
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
                        0 // Số lượng, bạn cần điều chỉnh logic nếu cần
                ))
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
