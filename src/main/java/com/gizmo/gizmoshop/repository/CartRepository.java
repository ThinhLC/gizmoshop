package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByAccountId(Long accountId);


    Cart findTopByAccountIdOrderByCreateDateDesc(Long accountId);


    void deleteByAccountId(Long accountId);
}