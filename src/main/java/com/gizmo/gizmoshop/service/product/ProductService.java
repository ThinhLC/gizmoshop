package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.ProductImageMappingRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }



    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productImageMappingResponse(mapProductImageMappingsToResponse(product.getProductImageMappings())) // Trả về danh sách
                .productInventoryResponse(mapToProductInventoryResponse(product.getProductInventory()))
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(mapToBrandResponse(product.getBrand()))
                .productCategories(mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(mapToStatusResponse(product.getStatus()))
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .author(author(product.getAuthor()))
                .build();
    }
    private ProductInventoryResponse mapToProductInventoryResponse(ProductInventory productInventory) {
        return new ProductInventoryResponse(
                productInventory.getId(),
                productInventory.getProduct().getId(), // Trả về ID của Inventory trực tiếp
                productInventory.getInventory().getId(),
                productInventory.getQuantity()
        );
    }
    private AccountResponse author(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getEmail(),
                account.getFullname(),
                account.getSdt(),
                account.getBirthday(),
                account.getImage() != null ? account.getImage() : "Chưa có hình ảnh",
                account.getExtra_info(),
                account.getCreate_at(),
                account.getUpdate_at(),
                account.getDeleted(),
                account.getRoleAccounts().stream().map(roleAccount -> roleAccount.getRole().getName()).collect(Collectors.toSet())
        );
    }

    private ProductStatusResponse mapToStatusResponse(StatusProduct status) {
        if (status == null) {
            return null; // Trả về null nếu không có trạng thái
        }
        return ProductStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }
    private CategoriesResponse mapToCategoryResponse(Categories category) {
        if (category == null) {
            return null; // Trả về null nếu không có danh mục
        }
        return CategoriesResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.getActive())
                .image(category.getImageId())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .build();
    }

    private BrandResponseDto mapToBrandResponse(ProductBrand brand) {
        if (brand == null) {
            return null; // Trả về null nếu không có thương hiệu
        }
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .deleted(brand.getDeleted())
                .build();
    }

    private List<ProductImageMappingResponse> mapProductImageMappingsToResponse(Set<ProductImageMapping> mappings) {
        return mappings.stream()
                .map(mapping -> new ProductImageMappingResponse(
                        mapping.getProduct().getId(),
                        mapping.getImage().getId(),
                        mapping.getImage().getFileDownloadUri() // Nếu bạn muốn thêm URI
                ))
                .collect(Collectors.toList());
    }

}
