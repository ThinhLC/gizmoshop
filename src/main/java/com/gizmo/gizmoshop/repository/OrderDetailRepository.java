package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByIdOrder(Long idOrder);

    List<OrderDetail> findByIdProduct(Long idProduct);

    Optional<OrderDetail> findByIdOrderAndIdProduct(Long idOrder, Long idProduct);
}
