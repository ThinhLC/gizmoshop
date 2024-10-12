package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.ProductBrand;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
import com.gizmo.gizmoshop.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    public Page<Inventory> findInventoriesByCriteria(String inventoryName, Boolean active, Pageable pageable) {
        return inventoryRepository.findByCriteria(inventoryName, active, pageable);
    }
    public InventoryResponse getInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        return buildInventoryResponse(inventory);
    }
    public Inventory createInventory(CreateInventoryRequest request) {
        Inventory existingInventory = inventoryRepository.findByInventoryName(request.getInventoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory name already exists: " + request.getInventoryName()));

        Inventory inventory = new Inventory();
        inventory.setInventoryName(request.getInventoryName());
        inventory.setCity(request.getCity());
        inventory.setDistrict(request.getDistrict());
        inventory.setCommune(request.getCommune());
        inventory.setLatitude(request.getLatitude());
        inventory.setLongitude(request.getLongitude());
        inventory.setActive(false);
        return inventoryRepository.save(inventory);
    }
    public InventoryResponse updateInventory(Long id, CreateInventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        inventory.setInventoryName(request.getInventoryName());
        inventory.setCity(request.getCity());
        inventory.setDistrict(request.getDistrict());
        inventory.setCommune(request.getCommune());
        inventory.setLatitude(request.getLatitude());
        inventory.setLongitude(request.getLongitude());
        inventory.setActive(request.getActive());

        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }
    public InventoryResponse deactivateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        inventory.setActive(false);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }
    public InventoryResponse activateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        inventory.setActive(true);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }
    private InventoryResponse buildInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .inventoryName(inventory.getInventoryName())
                .city(inventory.getCity())
                .district(inventory.getDistrict())
                .commune(inventory.getCommune())
                .latitude(inventory.getLatitude())
                .longitude(inventory.getLongitude())
                .active(inventory.getActive())
                .build();
    }


}
