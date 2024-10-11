package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/list/inventory")
    @PreAuthorize("permitAll()")
    ResponseEntity<ResponseWrapper<List<Inventory>>> getInventory() {
        ResponseWrapper<List<Inventory>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", inventoryService.getAllInventory());
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/get/{userId}")
    @PreAuthorize("permitAll()")
    ResponseEntity<ResponseWrapper<InventoryResponse>> getInventory(@PathVariable Long userId) {
        InventoryResponse inventoryResponse = inventoryService.getInventoryById(userId);

        ResponseWrapper<InventoryResponse> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", inventoryResponse);

        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/create")
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("permitAll()")
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
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<InventoryResponse>> activateInventory(@PathVariable Long id) {
        InventoryResponse updatedInventory = inventoryService.activateInventoryById(id);
        ResponseWrapper<InventoryResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Kho đã được chuyển sang trạng thái hoạt động",
                updatedInventory
        );

        return ResponseEntity.ok(response);
    }



}
