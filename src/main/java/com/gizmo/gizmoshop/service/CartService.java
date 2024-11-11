package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Cart;
import com.gizmo.gizmoshop.entity.CartItems;
import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.CartItemsRepository;
import com.gizmo.gizmoshop.repository.CartRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public List<CartItemResponse> getAllCartItems(Long userId) {
        Cart cart = cartRepository.findByAccount_Id(userId);

        if (cart == null) {
            throw new InvalidInputException("No cart found for userId " + userId);
        }

        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);

        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(cartItem -> new CartItemResponse(cartItem))
                .collect(Collectors.toList());

        long totalPrice = calculateTotalPrice(cart);
        CartResponse cartResponse = new CartResponse();
        cartResponse.setItems(cartItemResponses);
        cartResponse.setTotalPrice(totalPrice);
        return cartItemResponses;
    }

    private long calculateTotalPrice(Cart cart) {
        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);
        return cartItems.stream()
                .mapToLong(item -> item.getProductId().getPrice() * item.getQuantity())  // Tính tổng giá trị
                .sum();
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
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa, nếu có thì cập nhật số lượng
        Optional<CartItems> existingItemOpt = cartItemsRepository.findByCartIdAndProductId(accountId, productId);
        if (existingItemOpt.isPresent()) {
            CartItems existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity); // Cập nhật số lượng sản phẩm
            existingItem.setUpdateDate(LocalDateTime.now());
            cartItemsRepository.save(existingItem);
        } else {
            // Thêm sản phẩm mới vào giỏ hàng
            CartItems cartItem = new CartItems();
            cartItem.setCart(cart);
            cartItem.setProductId(product);
            cartItem.setQuantity(quantity);
            cartItem.setCreateDate(LocalDateTime.now());
            cartItem.setUpdateDate(LocalDateTime.now());

            cartItemsRepository.save(cartItem);
        }

        // Cập nhật tổng giá trị giỏ hàng
        updateCartTotalPrice(cart);

        // Trả về response DTO giỏ hàng với thông tin đầy đủ
        return toCartResponse(cart);
    }

    private void updateCartTotalPrice(Cart cart) {
        List<CartItems> cartItems = cartItemsRepository.findByCart(cart);
        long totalPrice = cartItems.stream()
                .mapToLong(item -> item.getProductId().getPrice() * item.getQuantity()) // Giả sử Product có thuộc tính price
                .sum();
        cart.setTotalPrice(totalPrice);
        cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        CartResponse cartDTO = new CartResponse();
        cartDTO.setId(cart.getId());
        Account account = cart.getAccount();
        AccountResponse accountResponse = new AccountResponse(account.getId(), account.getFullname(), account.getEmail());
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
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Tìm sản phẩm trong giỏ hàng
        CartItems cartItem = cartItemsRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        // Xóa sản phẩm khỏi giỏ hàng
        cartItemsRepository.delete(cartItem);

        // Cập nhật thông tin giỏ hàng và trả về phản hồi
        CartResponse cartResponse = toCartResponse(cart);
        return cartResponse;
    }
}

