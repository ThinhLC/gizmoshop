package com.gizmo.gizmoshop.utils;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConvertEntityToResponse {
    public BrandResponseDto mapToBrandResponse(ProductBrand brand) {
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
    public ProductStatusResponse mapToStatusResponse(StatusProduct status) {
        if (status == null) {
            return null; // Trả về null nếu không có trạng thái
        }
        return ProductStatusResponse.builder()
                .id(status.getId())
                .name(status.getName())
                .build();
    }

    public ProductInventoryResponse mapToProductInventoryResponse(ProductInventory productInventory) {
        if (productInventory == null) {
            return null;
        }
        return new ProductInventoryResponse(
                productInventory.getId(),
                productInventory.getProduct().getId(), // Trả về ID của Inventory trực tiếp
                productInventory.getInventory().getId(),
                productInventory.getQuantity()
        );
    }

    public CategoriesResponse mapToCategoryResponse(Categories category) {
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

    public AccountResponse author(Account account) {
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

}
