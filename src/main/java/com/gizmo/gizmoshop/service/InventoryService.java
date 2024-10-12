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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public Page<Inventory> findInventoriesByCriteria(String inventoryName, Boolean active, Pageable pageable) {
        return inventoryRepository.findByCriteria(inventoryName, active, pageable);
    }

//    public List<Inventory> getAllInventory() {
//        return inventoryRepository.findAll();
//    }

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
                .latitude(inventory.getLatitude())
                .longitude(inventory.getLongitude())
                .active(inventory.getActive())
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
        inventory.setLatitude(request.getLatitude());
        inventory.setLongitude(request.getLongitude());
        inventory.setActive(false);
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
        inventoryCheck.setLatitude(request.getLatitude());
        inventoryCheck.setLongitude(request.getLongitude());
        inventoryCheck.setActive(request.getActive());

        Inventory updatedInventory = inventoryRepository.save(inventoryCheck);
        return InventoryResponse.builder()
                .id(updatedInventory.getId())
                .inventoryName(updatedInventory.getInventoryName())
                .city(updatedInventory.getCity())
                .district(updatedInventory.getDistrict())
                .commune(updatedInventory.getCommune())
                .latitude(updatedInventory.getLatitude())
                .longitude(updatedInventory.getLongitude())
                .active(updatedInventory.getActive())
                .build();
    }

    public InventoryResponse deactivateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));

        inventory.setActive(false);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return InventoryResponse.builder()
                .id(updatedInventory.getId())
                .inventoryName(updatedInventory.getInventoryName())
                .city(updatedInventory.getCity())
                .district(updatedInventory.getDistrict())
                .commune(updatedInventory.getCommune())
                .latitude(updatedInventory.getLatitude())  // thêm latitude
                .longitude(updatedInventory.getLongitude()) // thêm longitude
                .active(updatedInventory.getActive()) // thêm active
                .build();
    }

    public InventoryResponse activateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));

        inventory.setActive(true);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return InventoryResponse.builder()
                .id(updatedInventory.getId())
                .inventoryName(updatedInventory.getInventoryName())
                .city(updatedInventory.getCity())
                .district(updatedInventory.getDistrict())
                .commune(updatedInventory.getCommune())
                .latitude(updatedInventory.getLatitude())  // thêm latitude
                .longitude(updatedInventory.getLongitude()) // thêm longitude
                .active(updatedInventory.getActive()) // thêm active
                .build();
    }


}
