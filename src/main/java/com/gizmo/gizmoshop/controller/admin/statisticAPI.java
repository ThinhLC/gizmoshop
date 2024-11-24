package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.OrderStatusResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.statisticDto;
import com.gizmo.gizmoshop.repository.ProductRepository;
import com.gizmo.gizmoshop.service.statisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/admin/statistic/t")
@CrossOrigin("*")
public class statisticAPI {
    @Autowired
    private statisticService statisticsService;


    @GetMapping("/SalesRevenueStatistics")//thong ke doanh thu ban hang
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<statisticDto>> SalesRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        if (startDate == null || endDate == null) {
            LocalDate now = LocalDate.now();
            LocalDate firstDayOfMonth = now.withDayOfMonth(1);
            LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            endDate = Date.from(lastDayOfMonth.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        }
        statisticDto salesRevenueStatistics = statisticsService.SalesRevenueStatistics(startDate, endDate);
        ResponseWrapper<statisticDto> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy doanh thu thành công",salesRevenueStatistics );
        return ResponseEntity.ok(response);
    }


    // API sản phẩm bán chạy nhất
    @GetMapping("/top-selling-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<statisticDto>>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<statisticDto> result = statisticsService.getTopSellingProductsInTimeRange(startDate, endDate, pageable);

        ResponseWrapper<Page<statisticDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy top sản phẩm bán chạy nhất thành công", result);
        return ResponseEntity.ok(responseWrapper);
    }


    // API sản phẩm sắp hết hàng
    @GetMapping("/low-stock-products")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<statisticDto>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<statisticDto> result = statisticsService.getLowStockProducts(threshold, pageable);

        ResponseWrapper<Page<statisticDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy sản phẩm sắp hết hàng thành công", result);
        return ResponseEntity.ok(responseWrapper);
    }
    @GetMapping("/partner-statistics")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<statisticDto>> getPartnerStatisticsByStatus(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
         ) {
        statisticDto result = statisticsService.getStatisticDtoByStatus(startDate, endDate);

        ResponseWrapper<statisticDto> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy tổng số  sản phẩm  & doanh thu của tất cả đối tác thành công thành công", result);
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/top-selling-by-supplier")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<statisticDto>>> getTopSellingProductsBySupplier(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<statisticDto> result = statisticsService.getTopSellingProductsBySupplier(startDate, endDate, pageable);

        ResponseWrapper<Page<statisticDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy top sản phẩm bán chạy nhất của nhà cung cấp thành công", result);
        return ResponseEntity.ok(responseWrapper);
    }




}
