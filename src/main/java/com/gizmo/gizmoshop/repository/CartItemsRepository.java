package com.gizmo.gizmoshop.repository;


import com.gizmo.gizmoshop.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    List<CartItems> findByCartId(Long cartId);


    CartItems findByCartIdAndProductId(Long cartId, Long productId);


    void deleteByCartId(Long cartId);
}
