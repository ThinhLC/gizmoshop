package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.statisticDto;
import com.gizmo.gizmoshop.entity.Order;
import com.gizmo.gizmoshop.repository.OrderRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class statisticService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public statisticDto SalesRevenueStatistics(Date startDate, Date endDate) {
        long amountShop = 0;
        long amountSupplier = 0;
        if (startDate != null) {
            startDate = Date.from(startDate.toInstant().truncatedTo(ChronoUnit.DAYS));
        }
        if (endDate != null) {
            endDate = Date.from(endDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .with(LocalTime.MAX)
                    .toInstant());
        }
        List<Order> orders = orderRepository.findOrdersByOrderStatus(startDate, endDate,13L);
        for (Order order : orders) {
            amountShop += order.getTotalPrice();

            Boolean roleStatus = order.getOrderStatus() != null ? order.getOrderStatus().getRoleStatus() : null;
            if (Boolean.TRUE.equals(roleStatus)) {
                amountSupplier += order.getTotalPrice();
            }
        }
        return statisticDto.builder()
                .amountShop(amountShop)
                .amountSupplier(amountSupplier)
                .build();
    }


    public Page<statisticDto> getTopSellingProductsInTimeRange(Date startDate, Date endDate, Pageable pageable) {
        Page<Object[]> result = productRepository.findTopSellingProductsInTimeRange(startDate, endDate, pageable);
        return result.map(row -> {
            Long id = row[0] != null ? Long.parseLong(row[0].toString()) : null;
            String name = row[1] != null ? row[1].toString() : null;
            Long quantity = row[2] != null ? Long.parseLong(row[2].toString()) : 0L;
            return statisticDto.builder()
                    .id(id)
                    .name(name)
                    .quantity(quantity)
                    .build();
        });
    }


    // Lấy danh sách sản phẩm sắp hết hàng
    public Page<statisticDto> getLowStockProducts(int threshold, Pageable pageable) {
        Page<Object[]> result = productRepository.findLowStockProducts(threshold, pageable);
        return result.map(row -> {
            Long id = row[0] != null ? Long.parseLong(row[0].toString()) : null;
            String name = row[1] != null ? row[1].toString() : null;
            Long quantity = row[2] != null ? Long.parseLong(row[2].toString()) : 0L;
            return statisticDto.builder()
                    .id(id)
                    .name(name)
                    .quantity(quantity)
                    .build();
        });
    }

    //số lượng và danh thu theo khoảng thời gian của tất cả các nhà cug cấp
    public statisticDto getStatisticDtoByStatus(Date startDate, Date endDate) {
        Object[] result = productRepository.findTotalSoldAndRevenueInTimeRange(startDate, endDate);
        if (result == null || result.length < 2 || result[0] == null || result[1] == null) {
            return statisticDto.builder()
                    .quantity(0L) // Số lượng mặc định
                    .amountSupplier(0L) // Doanh thu mặc định
                    .build();
        }
        return statisticDto.builder()
                .quantity(Long.parseLong(result[0].toString())) // Tổng số lượng
                .amountSupplier(Long.parseLong(result[1].toString())) // Tổng doanh thu
                .build();
    }

    public Page<statisticDto> getTopSellingProductsBySupplier(Date startDate, Date endDate, Pageable pageable) {
        Page<Object[]> result = productRepository.findTopSellingProductsInTimeRangeBySupplier(startDate, endDate, pageable);
        return result.map(row -> statisticDto.builder()
                .id(Long.parseLong(row[0].toString()))
                .name(row[1].toString())
                    .nameAuth(row[2].toString())
                .quantity(Long.parseLong(row[3].toString()))
                .build());
    }



}
