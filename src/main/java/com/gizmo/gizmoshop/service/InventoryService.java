package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
