package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByAccountId_Id(Long accountId);
}