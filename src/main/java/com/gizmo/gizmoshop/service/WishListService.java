package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.ProductImageMappingResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ProductInventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ProductResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WishListResponse;
import com.gizmo.gizmoshop.dto.reponseDto.WishListItemResponse;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.entity.Wishlist;
import com.gizmo.gizmoshop.entity.WishlistItems;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import com.gizmo.gizmoshop.repository.WishlistRepository;
import com.gizmo.gizmoshop.repository.WishlistItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishListService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemsRepository wishlistItemsRepository;

    // Thêm sản phẩm vào danh sách yêu thích
    public WishListResponse toggleProductInWishlist(Long accountId, Long productId) {
        // Tìm tài khoản từ accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Tìm danh sách yêu thích của người dùng hoặc tạo mới nếu chưa tồn tại
        Wishlist wishlist = wishlistRepository.findByAccountId_Id(accountId).orElseGet(() -> {
            Wishlist newWishlist = new Wishlist();
            newWishlist.setAccountId(account);
            newWishlist.setCreateDate(LocalDateTime.now());
            newWishlist.setUpdateDate(LocalDateTime.now());
            return wishlistRepository.save(newWishlist);
        });

        // Tìm sản phẩm từ productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa
        Optional<WishlistItems> existingItemOpt = wishlistItemsRepository.findByWishlistAndProduct(wishlist, product);

        if (existingItemOpt.isPresent()) {
            // Nếu sản phẩm có trong danh sách yêu thích, xóa nó khỏi danh sách
            wishlistItemsRepository.delete(existingItemOpt.get());
        } else {
            // Nếu sản phẩm không có, thêm nó vào danh sách yêu thích
            WishlistItems wishlistItem = new WishlistItems();
            wishlistItem.setWishlist(wishlist);
            wishlistItem.setProduct(product);
            wishlistItem.setCreateDate(LocalDateTime.now());
            wishlistItemsRepository.save(wishlistItem);
        }

        // Trả về response DTO danh sách yêu thích với thông tin đầy đủ
        return toWishlistResponse(wishlist);
    }

    // Chuyển đổi Wishlist thành WishListResponse
    private WishListResponse toWishlistResponse(Wishlist wishlist) {
        List<WishlistItems> wishlistItems = wishlist.getWishlistItems();

        // Kiểm tra null trước khi sử dụng stream()
        if (wishlistItems == null) {
            wishlistItems = new ArrayList<>();
        }

        return new WishListResponse(
                wishlist.getId(),
                null,  // Nếu bạn có `AccountResponse`, thì hãy thay null bằng đối tượng AccountResponse phù hợp
                wishlist.getCreateDate(),
                wishlist.getUpdateDate(),
                wishlist.getWishlistItems().stream()
                        .map(wishlistItem -> {
                            Product product = wishlistItem.getProduct();
                            List<ProductImageMappingResponse> productImageMappingResponseList = product.getProductImageMappings().stream()
                                    .map(ProductImageMappingResponse::new)
                                    .collect(Collectors.toList());

                            return new WishListItemResponse(
                                    wishlistItem.getId(),
                                    new ProductResponse(
                                            product.getId(),
                                            product.getName(),
                                            productImageMappingResponseList,
                                            product.getPrice(),
                                            product.getThumbnail(),
                                            product.getLongDescription(),
                                            product.getShortDescription(),
                                            product.getWeight(),
                                            product.getArea(),
                                            product.getVolume(),
                                            product.getHeight(),
                                            product.getLength()
                                    ),
                                    wishlistItem.getCreateDate()
                            );
                        })
                        .collect(Collectors.toList())
        );
    }
}
