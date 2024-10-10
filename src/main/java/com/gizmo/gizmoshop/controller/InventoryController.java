package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
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
}
