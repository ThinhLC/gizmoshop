package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIdAccount(Long idAccount);

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByIdStatus(Long idStatus);

    List<Order> findByNgayGiaoHang(Date ngayGiaoHang);
}