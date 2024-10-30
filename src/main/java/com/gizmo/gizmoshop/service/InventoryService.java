package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.InventoryRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Autowired
    ProductInventoryRepository productInventoryRepository;
    public Page<Inventory> findInventoriesByCriteria(String inventoryName, Boolean active, Pageable pageable) {
        return inventoryRepository.findByCriteria(inventoryName, active, pageable);
    }

    public InventoryResponse getInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        return buildInventoryResponse(inventory);
    }

    public List<InventoryResponse> getInventoryArr() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream().map(this::mapToInventoryResponse).collect(Collectors.toList());
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setInventoryName(inventory.getInventoryName());
        response.setCity(inventory.getCity());
        response.setDistrict(inventory.getDistrict());
        response.setCommune(inventory.getCommune());
        response.setLatitude(inventory.getLatitude());
        response.setLongitude(inventory.getLongitude());
        response.setActive(inventory.getActive());
        response.setCreatedAt(inventory.getCreatedAt());
        response.setUpdatedAt(inventory.getUpdatedAt());
        return response;
    }

    public Inventory createInventory(CreateInventoryRequest request) {
        inventoryRepository.findByInventoryName(request.getInventoryName())
                .ifPresent(existingInventory -> {
                    throw new InvalidInputException("Inventory name already exists: " + request.getInventoryName());
                });
        Inventory inventory = new Inventory();
        inventory.setInventoryName(request.getInventoryName());
        inventory.setCity(request.getCity());
        inventory.setDistrict(request.getDistrict());
        inventory.setCommune(request.getCommune());
        inventory.setLatitude(request.getLatitude());
        inventory.setLongitude(request.getLongitude());
        inventory.setActive(request.getActive());
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
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
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setCreatedAt(request.getCreatedAt());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }

    public InventoryResponse deactivateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        inventory.setActive(false);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }

    public InventoryResponse activateInventoryById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        inventory.setActive(true);
        Inventory updatedInventory = inventoryRepository.save(inventory);
        return buildInventoryResponse(updatedInventory);
    }

    public InventoryResponse changeActiveById(long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        inventory.setActive(!inventory.getActive());
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
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public List<InventoryStatsDTO> getInventoryProduct() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventory -> {
                    List<ProductInventory> productInventories = productInventoryRepository.findByInventoryId(inventory.getId());
                    List<ProductInventoryResponse> productInventoryResponses = productInventories.stream()
                            .map(productInventory -> ProductInventoryResponse.builder()
                                    .id(productInventory.getId())
                                    .product(new ProductResponse(productInventory.getProduct().getName(), productInventory.getProduct().getPrice(), productInventory.getProduct().getShortDescription()))
                                    .inventory(new InventoryResponse(productInventory.getInventory().getId(), productInventory.getInventory().getInventoryName()))
                                    .quantity(productInventory.getQuantity())
                                    .build())
                            .collect(Collectors.toList());


                    return new InventoryStatsDTO(
                            inventory.getId(),
                            inventory.getInventoryName(),
                            productInventoryResponses
                    );
                })
                .sorted(Comparator.comparing(InventoryStatsDTO::getId).reversed())
                .collect(Collectors.toList());
    }
}
