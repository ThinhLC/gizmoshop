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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                null,  // Nếu bạn có `AccountResponse`, hãy thay `null` bằng đối tượng AccountResponse phù hợp
                wishlist.getCreateDate(),
                wishlist.getUpdateDate(),
                wishlistItems.stream()
                        .map(wishlistItem -> {
                            Product product = wishlistItem.getProduct();
                            List<ProductImageMappingResponse> productImageMappingResponseList = product.getProductImageMappings().stream()
                                    .map(ProductImageMappingResponse::new)
                                    .collect(Collectors.toList());

                            ProductResponse productResponse = ProductResponse.builder()
                                    .id(product.getId())
                                    .productName(product.getName())
                                    .productImageMappingResponse(productImageMappingResponseList)
                                    .productPrice(product.getPrice())
                                    .thumbnail(product.getThumbnail())
                                    .productLongDescription(product.getLongDescription())
                                    .productShortDescription(product.getShortDescription())
                                    .productWeight(product.getWeight())
                                    .productArea(product.getArea())
                                    .productVolume(product.getVolume())
                                    .productHeight(product.getHeight())
                                    .productLength(product.getLength())
                                    .build();

                            return WishListItemResponse.builder()
                                    .id(wishlistItem.getId())
                                    .createDate(wishlistItem.getCreateDate())
                                    .product(productResponse)
                                    .build();
                        })
                        .collect(Collectors.toList()) // Đóng stream thành List
        );
    }
    public Page<WishListResponse> getAllFavouriteProducts(Long accountId, Pageable pageable) {
        Page<Wishlist> wishListItems = wishlistRepository.findByAccountId(accountId, pageable);

        // Chuyển đổi từng WishList item thành WishListResponse
        return wishListItems.map(item -> {
            // Lấy danh sách sản phẩm từ các WishlistItem (đã có thông tin sản phẩm)
            List<WishListItemResponse> wishListItemResponses = item.getWishlistItems().stream()
                    .map(wishlistItem -> {
                        Product product = wishlistItem.getProduct(); // Lấy sản phẩm từ WishlistItem
                        if (product != null) {
                            // Tạo danh sách ProductImageMappingResponse từ Product
                            List<ProductImageMappingResponse> productImageMappingResponseList = product.getProductImageMappings().stream()
                                    .map(ProductImageMappingResponse::new)
                                    .collect(Collectors.toList());

                            // Tạo ProductResponse từ thông tin sản phẩm
                            ProductResponse productResponse = ProductResponse.builder()
                                    .id(product.getId())
                                    .productName(product.getName())
                                    .productImageMappingResponse(productImageMappingResponseList)
                                    .productPrice(product.getPrice())
                                    .thumbnail(product.getThumbnail())
                                    .productLongDescription(product.getLongDescription())
                                    .productShortDescription(product.getShortDescription())
                                    .productWeight(product.getWeight())
                                    .productArea(product.getArea())
                                    .productVolume(product.getVolume())
                                    .productHeight(product.getHeight())
                                    .productLength(product.getLength())
                                    .build();

                            // Trả về WishListItemResponse cho từng sản phẩm yêu thích
                            return WishListItemResponse.builder()
                                    .id(wishlistItem.getId())
                                    .createDate(wishlistItem.getCreateDate())
                                    .product(productResponse)
                                    .build();
                        }
                        return null; // Nếu không tìm thấy sản phẩm, trả về null
                    })
                    .filter(Objects::nonNull)  // Loại bỏ phần tử null
                    .collect(Collectors.toList()); // Thu thập thành danh sách

            // Trả về WishListResponse với danh sách WishListItemResponse
            return new WishListResponse(
                    item.getId(),
                    null,  // Nếu bạn có AccountResponse, hãy thay null bằng đối tượng AccountResponse
                    item.getCreateDate(),
                    item.getUpdateDate(),
                    wishListItemResponses // Danh sách sản phẩm yêu thích
            );
        });
    }



}
