package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.Voucher;
import com.gizmo.gizmoshop.entity.VoucherToOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoucherToOrderRepository extends JpaRepository<VoucherToOrder, Long> {
//    List<VoucherToOrder> findByVoucherId(Long voucherId);
    List<VoucherToOrder> findByVoucher(Voucher voucher);

//    List<VoucherToOrder> findByOrderId(Long orderId);
}
