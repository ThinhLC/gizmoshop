package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.CategoryStatisticsDto;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryStatsDTO;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/inventory")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;
    private final GenericExporter<InventoryResponse> genericExporter;

    //Chú thích tí: link truy câp se la nhu the nay http://localhost:8081/api/public/list
    // Neu muon sap xem theo ten thi http://localhost:8081/api/public/list?sort=inventoryName,asc voi cai sau sort=(truong muon sap xep)
    //http://localhost:8081/api/public/list?sort=inventoryName,asc&page=0&limit=10 với page la trang hien tai và limit la phan tu trong trang
    // test bang post man co the them cac truong do bang form - data
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<Inventory>>> findInventoriesByCriteria(
            @RequestParam(value = "inventoryName", required = false) String inventoryName,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Optional<String> sort) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<Inventory> inventories = inventoryService.findInventoriesByCriteria(inventoryName, active, pageable);
        ResponseWrapper<Page<Inventory>> response = new ResponseWrapper<>(HttpStatus.OK, "Inventories fetched successfully", inventories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getArr")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<List<InventoryResponse>>> getInventoryArr() {
        List<InventoryResponse> inventoryResponse = inventoryService.getInventoryArr();
        ResponseWrapper<List<InventoryResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", inventoryResponse);
        return ResponseEntity.ok(responseWrapper);
    }


    @GetMapping("/get/{Id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<InventoryResponse>> getInventory(@PathVariable Long Id) {
        InventoryResponse inventoryResponse = inventoryService.getInventoryById(Id);
        ResponseWrapper<InventoryResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", inventoryResponse);
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<Inventory>> createInventory(@RequestBody CreateInventoryRequest request) {
        Inventory inventoryResponse = inventoryService.createInventory(request);
        ResponseWrapper<Inventory> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", inventoryResponse);
        return ResponseEntity.ok(responseWrapper);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<InventoryResponse>> updateInventory(@PathVariable Long id, @RequestBody CreateInventoryRequest request) {
        InventoryResponse updatedInventory = inventoryService.updateInventory(id, request);
        ResponseWrapper<InventoryResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Kho đã được cập nhật", updatedInventory);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<InventoryResponse>> deleteInventory(@PathVariable Long id) {
        InventoryResponse updatedInventory = inventoryService.deactivateInventoryById(id);
        ResponseWrapper<InventoryResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Kho đã được chuyển sang trạng thái không hoạt động",
                updatedInventory
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/setactive/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<InventoryResponse>> activateInventory(@PathVariable Long id) {
        InventoryResponse updatedInventory = inventoryService.activateInventoryById(id);
        ResponseWrapper<InventoryResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Kho đã được chuyển sang trạng thái hoạt động",
                updatedInventory
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/changeactive/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<InventoryResponse>> changeActive(@PathVariable Long id) {
        InventoryResponse updatedInventory = inventoryService.changeActiveById(id);
        ResponseWrapper<InventoryResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật thành công",
                updatedInventory
        );

        return ResponseEntity.ok(response);
    }

    //do ra kho nao , trong do co bao nhieu mat hang , so luuong trong kho do la bao nhieu
    @GetMapping("/InventoryStats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<InventoryStatsDTO>>> getInventoryStats() {
        List<InventoryStatsDTO> inventoryStatsDTOS = inventoryService.getInventoryProduct();
        ResponseWrapper<List<InventoryStatsDTO>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy sản phẩm cho từng kho thành công", inventoryStatsDTOS);
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> exportInventories() {
        String filePath = new File("output/inventory_export.xlsx").getAbsolutePath();
        new File("output").mkdir();  // Tạo thư mục nếu chưa tồn tại

        try {
            List<InventoryResponse> inventories = inventoryService.getAllInventories();
            ByteArrayInputStream in = genericExporter.exportToExcel(inventories, InventoryResponse.class);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(in.readAllBytes());
            }
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Xuất kho hàng thành công. Tệp đã được lưu tại: " + filePath, filePath));
        } catch (IOException e) {
            log.error("Lỗi khi xuất dữ liệu kho hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xuất dữ liệu kho hàng.", null));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi xuất dữ liệu kho hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn khi xuất dữ liệu kho hàng.", null));
        }
    }

    @GetMapping("/export/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> exportInventoryById(@PathVariable Long id) {
        String filePath = new File("output/inventory_" + id + "_export.xlsx").getAbsolutePath();
        new File("output").mkdir();
        try {
            Optional<InventoryResponse> inventoryOpt = Optional.ofNullable(inventoryService.getInventoryById(id));
            if (inventoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(HttpStatus.NOT_FOUND, "Không tìm thấy kho hàng với id: " + id, null));
            }
            ByteArrayInputStream in = genericExporter.exportToExcel(Collections.singletonList(inventoryOpt.get()), InventoryResponse.class);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(in.readAllBytes());
            }
            return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Xuất kho hàng thành công. Tệp đã được lưu tại: " + filePath, filePath));
        } catch (IOException e) {
            log.error("Lỗi khi xuất dữ liệu kho hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseWrapper<>(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi xuất dữ liệu kho hàng.", null));
        }
    }
}
