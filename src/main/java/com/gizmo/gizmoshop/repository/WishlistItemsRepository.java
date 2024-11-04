package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.WishlistItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistItemsRepository extends JpaRepository<WishlistItems, Long> {
    @Query("SELECT COUNT(wi) FROM WishlistItems wi JOIN wi.productId p WHERE p.id = :productId AND FUNCTION('MONTH', wi.wishlistId.createDate) = :month AND FUNCTION('YEAR', wi.wishlistId.createDate) = :year")
    int countFavoritesByProductAndMonth(@Param("productId") Long productId, @Param("month") int month, @Param("year") int year);
}
