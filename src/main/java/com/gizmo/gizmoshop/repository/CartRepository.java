package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByAccount_Id(Long userId);

    Optional<Cart> findByAccountId(@Param("accountId") Long accountId);
}