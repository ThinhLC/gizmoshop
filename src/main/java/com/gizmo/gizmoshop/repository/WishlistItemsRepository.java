package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.entity.Wishlist;
import com.gizmo.gizmoshop.entity.WishlistItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemsRepository extends JpaRepository<WishlistItems, Long> {
    List<WishlistItems> findByWishlistId(Wishlist wishlistId);
    int countByProductId(Product productId);
    Optional<WishlistItems> findByWishlistAndProduct(Wishlist wishlist, Product product);

}
