package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.ProductBrand;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public InventoryResponse getInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));

        // Sử dụng Builder để tạo InventoryResponse
        return InventoryResponse.builder()
                .id(inventory.getId())
                .inventoryName(inventory.getInventoryName())
                .city(inventory.getCity())
                .district(inventory.getDistrict())
                .commune(inventory.getCommune())
                .build();
    }

    public Inventory createInventory(CreateInventoryRequest request) {
        if (inventoryRepository.existsByInventoryName(request.getInventoryName())) {
            throw new RuntimeException("Inventory name already exists");
        }
        Inventory inventory = new Inventory();
        inventory.setInventoryName(request.getInventoryName());
        inventory.setCity(request.getCity());
        inventory.setDistrict(request.getDistrict());
        inventory.setCommune(request.getCommune());
        return inventoryRepository.save(inventory);
    }

    public InventoryResponse updateInventory(Long id, CreateInventoryRequest request) {
        Optional<Inventory> existingInventory = inventoryRepository.findById(id);
        if (existingInventory.isEmpty()) {
            throw new BrandNotFoundException("Inventory not found with id: " + id);
        }

        Inventory inventoryCheck = existingInventory.get();
        inventoryCheck.setInventoryName(request.getInventoryName());
        inventoryCheck.setCity(request.getCity());
        inventoryCheck.setDistrict(request.getDistrict());
        inventoryCheck.setCommune(request.getCommune());
        Inventory updatedInventory = inventoryRepository.save(inventoryCheck);
        InventoryResponse response = new InventoryResponse();
        response.setId(updatedInventory.getId());
        response.setInventoryName(updatedInventory.getInventoryName());
        response.setCity(updatedInventory.getCity());
        response.setDistrict(updatedInventory.getDistrict());
        response.setCommune(updatedInventory.getCommune());
        return response;
    }
    public void  deleteInventoryById(long id) {
        inventoryRepository.deleteById(id);
    }


}
