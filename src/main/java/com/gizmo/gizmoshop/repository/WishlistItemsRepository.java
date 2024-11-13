package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Product;
import com.gizmo.gizmoshop.entity.Wishlist;
import com.gizmo.gizmoshop.entity.WishlistItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemsRepository extends JpaRepository<WishlistItems, Long> {
    List<WishlistItems> findByWishlist(Wishlist wishlistId);
    int countByProductId(Product productId);
    Optional<WishlistItems> findByWishlistAndProduct(Wishlist wishlist, Product product);

    @Query("SELECT COUNT(wi) FROM WishlistItems wi JOIN wi.product p WHERE p.id = :productId AND FUNCTION('MONTH', wi.wishlist.createDate) = :month AND FUNCTION('YEAR', wi.wishlist.createDate) = :year")
    int countFavoritesByProductAndMonth(@Param("productId") Long productId, @Param("month") int month, @Param("year") int year);
    Page<WishlistItems> findByWishlistId(Long wishlistId, Pageable pageable);

}
