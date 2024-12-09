package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemsRepository cartItemsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    public List<CartItemResponse> getAllCartItems(Long userId) {
        Cart cart = cartRepository.findByAccount_Id(userId);

        if (cart == null) {
            throw new InvalidInputException("No cart found for userId " + userId);
        }

        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);


        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProductId();
                    ProductResponse productResponse = ProductResponse.builder()
                            .id(product.getId())
                            .productName(product.getName())
                            .productImageMappingResponse(product.getProductImageMappings().stream()
                                    .map(ProductImageMappingResponse::new)
                                    .collect(Collectors.toList()))
                            .productPrice(product.getPrice())
                            .thumbnail(product.getThumbnail())
                            .discountProduct(product.getDiscountProduct())
                            .productLongDescription(product.getLongDescription())
                            .productShortDescription(product.getShortDescription())
                            .productWeight(product.getWeight())
                            .productArea(product.getArea())
                            .productVolume(product.getVolume())
                            .productHeight(product.getHeight())
                            .productLength(product.getLength())
                            .discountProduct(product.getDiscountProduct())
                            .build();

                    return CartItemResponse.builder()
                            .id(cartItem.getId())
                            .productId(productResponse)  // Gán ProductResponse thay vì productId kiểu String
                            .quantity(cartItem.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        long totalPrice = updateCartTotalPrice(cart);
        CartResponse cartResponse = new CartResponse();
        cartResponse.setItems(cartItemResponses);
        cartResponse.setTotalPrice(totalPrice);
        return cartItemResponses;
    }
    public CartResponse addProductToCart(Long accountId, Long productId, Long quantity) {
        // Tìm tài khoản từ accountId
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Tìm giỏ hàng của người dùng hoặc tạo mới nếu chưa tồn tại
        Cart cart = cartRepository.findByAccountId(accountId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setAccount(account);
            newCart.setCreateDate(LocalDateTime.now());
            newCart.setUpdateDate(LocalDateTime.now());
            return cartRepository.save(newCart);
        });

        // Tìm sản phẩm từ productId
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InvalidInputException("Sản phẩm không tồn tại"));
        ProductInventory productInventory = productInventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InvalidInputException("Thông tin kho sản phẩm không tồn tại"));

        if (quantity > productInventory.getQuantity()) {
            throw new InvalidInputException("Số lượng yêu cầu vượt quá số lượng trong kho. Chỉ còn "
                    + productInventory.getQuantity() + " sản phẩm có sẵn.");
        }
        Optional<CartItems> existingItemOpt = cartItemsRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existingItemOpt.isPresent()) {
            CartItems existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdateDate(LocalDateTime.now());
            cartItemsRepository.save(existingItem);
        } else {
            CartItems cartItem = new CartItems();
            cartItem.setCart(cart);
            cartItem.setProductId(product);
            cartItem.setQuantity(quantity);
            cartItem.setCreateDate(LocalDateTime.now());
            cartItem.setUpdateDate(LocalDateTime.now());
            cartItemsRepository.save(cartItem);
        }

        long totalPrice = updateCartTotalPrice(cart);
        System.out.println(totalPrice);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
        return toCartResponse(cart);
    }

    private long calculateDiscountedPrice(long price, long quantity, int discountPercent) {
        double discount = discountPercent / 100.0;
        return (long) ((price * quantity) - (price * quantity * discount));
    }

    private Long updateCartTotalPrice(Cart cart) {
        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);
        return cartItems.stream()
                .mapToLong(item -> calculateDiscountedPrice(item.getProductId().getPrice(),
                        item.getQuantity(),
                        item.getProductId().getDiscountProduct()))
                .sum();
    }


    private CartResponse toCartResponse(Cart cart) {
        CartResponse cartDTO = new CartResponse();
        cartDTO.setId(cart.getId());
        Account account = cart.getAccount();
        AccountResponse accountResponse = AccountResponse.builder()
                .id(account.getId())
                .fullname(account.getFullname())
                .email(account.getEmail())
                .build();
        cartDTO.setAccountId(accountResponse);  // Thêm thông tin tài khoản vào cart response
        cartDTO.setCreateDate(cart.getCreateDate());
        cartDTO.setUpdateDate(cart.getUpdateDate());
        cartDTO.setTotalPrice(cart.getTotalPrice());

        // Lấy danh sách các sản phẩm trong giỏ hàng và chuyển đổi thành DTO
        List<CartItemResponse> itemsDTO = cartItemsRepository.findByCart(cart).stream()
                .map(this::toCartItemsDTO)
                .collect(Collectors.toList());
        cartDTO.setItems(itemsDTO);

        return cartDTO;
    }

    private CartItemResponse toCartItemsDTO(CartItems item) {
        CartItemResponse itemDTO = new CartItemResponse();
        itemDTO.setId(item.getId());

        // Tạo ProductResponse từ thông tin sản phẩm trong CartItems
        Product product = item.getProductId();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setProductName(product.getName());
        productResponse.setProductPrice(product.getPrice());
        productResponse.setProductShortDescription(product.getShortDescription());
        productResponse.setProductLongDescription(product.getLongDescription());
        productResponse.setThumbnail(product.getThumbnail());
        productResponse.setProductWeight(product.getWeight());
        productResponse.setProductArea(product.getArea());
        productResponse.setProductVolume(product.getVolume());
        productResponse.setProductHeight(product.getHeight());
        productResponse.setProductLength(product.getLength());
        productResponse.setDiscountProduct(product.getDiscountProduct());
        // Thêm thông tin Brand
        if (product.getBrand() != null) {
            BrandResponseDto brandResponse = new BrandResponseDto();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setName(product.getBrand().getName());
            productResponse.setProductBrand(brandResponse);
        }

        // Thêm thông tin Categories
        if (product.getCategory() != null) {
            // Tạo CategoriesResponse từ Categories trong product
            CategoriesResponse categoriesResponse = new CategoriesResponse(product.getCategory());
            productResponse.setProductCategories(categoriesResponse);
        }

        // Thêm thông tin hình ảnh sản phẩm
        if (product.getProductImageMappings() != null) {
            List<ProductImageMappingResponse> imageResponses = product.getProductImageMappings().stream()
                    .map(imageMapping -> new ProductImageMappingResponse(imageMapping))  // Sử dụng constructor mới
                    .collect(Collectors.toList());

            productResponse.setProductImageMappingResponse(imageResponses);
        }
        // Thêm thông tin kho hàng (Inventory)
        if (product.getProductInventory() != null) {
            ProductInventoryResponse inventoryResponse = new ProductInventoryResponse();
            inventoryResponse.setQuantity(product.getProductInventory().getQuantity());
            productResponse.setProductInventoryResponse(inventoryResponse);
        }

        // Thêm thông tin sản phẩm vào CartItemResponse
        itemDTO.setProductId(productResponse);
        itemDTO.setQuantity(item.getQuantity());
        itemDTO.setCreateDate(item.getCreateDate());
        itemDTO.setUpdateDate(item.getUpdateDate());

        return itemDTO;
    }
    public CartResponse removeProductFromCart(Long accountId, Long productId) {
        // Tìm giỏ hàng của người dùng
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new InvalidInputException("Cart not found"));

        // Tìm sản phẩm trong giỏ hàng
        CartItems cartItem = cartItemsRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new InvalidInputException("Product not found in cart"));

        cartItemsRepository.delete(cartItem);

        long totalPrice = updateCartTotalPrice(cart);
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);

        CartResponse cartResponse = toCartResponse(cart);
        return cartResponse;
    }

    public void createCartForUser(Long accountId) {
        // Tìm tài khoản theo ID
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidInputException("Tài khoản không tồn tại"));
        // Tạo giỏ hàng mới
        Cart cart = new Cart();
        cart.setAccount(account);
        cart.setCreateDate(LocalDateTime.now());
        cart.setUpdateDate(LocalDateTime.now());
        cart.setTotalPrice(0L); // Giỏ hàng mới tạo, tổng giá trị = 0
        cartRepository.save(cart);
    }

    @Transactional
    public void   clearCart(Long userId) {
        // Lấy giỏ hàng của người dùng (nếu có), nếu không có sẽ ném ngoại lệ
        Cart cart = cartRepository.findByAccountId(userId)
                .orElseThrow(() -> new InvalidInputException("Cart not found for user"));

        // Xóa tất cả các cartItem trong giỏ hàng
        cartItemsRepository.deleteByCart(cart);
        cart.setTotalPrice(0L); // Set lại giá trị TotalPrice
        cartRepository.save(cart);
    }

}

