package com.gizmo.gizmoshop.repository;


import com.gizmo.gizmoshop.entity.Cart;
import com.gizmo.gizmoshop.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    @Query("SELECT ci FROM CartItems ci WHERE ci.cart.id = :cartId AND ci.productId.id = :productId")
    Optional<CartItems> findByCartIdAndProductId(Long cartId, Long productId);  // Tìm sản phẩm trong giỏ hàng theo cartId và productId
    // Tìm tất cả các mục trong giỏ hàng dựa trên cartId
    List<CartItems> findByCart(Cart cart);
    @Query("SELECT SUM(ci.quantity * ci.productId.price) FROM CartItems ci WHERE ci.cart.id = :cartId")
    Long findTotalPriceByCartId(@Param("cartId") Long cartId);
    List<CartItems> findAllByCart_Account_Id(Long accountId);

    void deleteByCartId(Long cartId);
    void deleteByCart(Cart cart);

}
