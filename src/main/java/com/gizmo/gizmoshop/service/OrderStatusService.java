package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.OrderStatusResponse;
import com.gizmo.gizmoshop.entity.OrderStatus;
import com.gizmo.gizmoshop.repository.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderStatusService {
    @Autowired
    OrderStatusRepository orderStatusRepository;

    public List<OrderStatusResponse> getOrderStatusesByType(Integer type) {
        List<OrderStatus> orderStatuses;

        if (type == null) {
            orderStatuses = orderStatusRepository.findOrderStatusesForCommon();
        } else if (type == 0) {
            orderStatuses = orderStatusRepository.findOrderStatusesForUser();
        } else if (type == 1) {
            orderStatuses = orderStatusRepository.findOrderStatusesForSupplier();
        } else {
            throw new IllegalArgumentException("Invalid type parameter. Use 0 for customer or 1 for supplier.");
        }

        return orderStatuses.stream()
                .map(orderStatus -> new OrderStatusResponse(orderStatus.getId(), orderStatus.getStatus(), orderStatus.getRoleStatus()))
                .collect(Collectors.toList());
    }
}
