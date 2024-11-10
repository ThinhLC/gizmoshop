package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateInventoryRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.InventoryRepository;
import com.gizmo.gizmoshop.repository.ProductInventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;



    @Autowired
    private GenericExporter<InventoryResponse> genericExporter;
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
    public List<InventoryResponse> getAllInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(this::buildInventoryResponse)
                .collect(Collectors.toList());
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
                            inventory.getCity(),
                            inventory.getDistrict(),
                            inventory.getCommune(),
                            inventory.getLatitude(),
                            inventory.getLongitude(),
                            inventory.getActive(),
                            inventory.getCreatedAt(),
                            inventory.getUpdatedAt(),
                            productInventoryResponses
                    );
                })
                .sorted(Comparator.comparing(InventoryStatsDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public void importInventories(MultipartFile file) throws IOException {
        List<InventoryResponse> inventories = genericExporter.importFromExcel(file, InventoryResponse.class);

        for (InventoryResponse inventoryResponse : inventories) {
            Long id = inventoryResponse.getId();
            System.out.println("Processing Inventory ID: " + id);

            if (id != null) {
                Optional<Inventory> existingInventoryOpt = inventoryRepository.findById(id);

                if (existingInventoryOpt.isPresent()) {
                    // Nếu tồn tại, cập nhật thông tin
                    Inventory existingInventory = existingInventoryOpt.get();
                    existingInventory.setInventoryName(inventoryResponse.getInventoryName());
                    existingInventory.setCity(inventoryResponse.getCity());
                    existingInventory.setDistrict(inventoryResponse.getDistrict());
                    existingInventory.setCommune(inventoryResponse.getCommune());
                    existingInventory.setLatitude(inventoryResponse.getLatitude());
                    existingInventory.setLongitude(inventoryResponse.getLongitude());
                    existingInventory.setActive(inventoryResponse.getActive());
                    existingInventory.setUpdatedAt(LocalDateTime.now());
                    inventoryRepository.save(existingInventory);
                    System.out.println("Updated existing inventory with ID: " + id);
                } else {
                    // Nếu không tồn tại, tạo mới
                    Inventory newInventory = new Inventory();
                    newInventory.setId(id); // Gán ID từ file Excel
                    newInventory.setInventoryName(inventoryResponse.getInventoryName() == null ? "" : inventoryResponse.getInventoryName());
                    newInventory.setCity(inventoryResponse.getCity() == null ? "" : inventoryResponse.getCity());
                    newInventory.setDistrict(inventoryResponse.getDistrict() == null ? "" : inventoryResponse.getDistrict());
                    newInventory.setCommune(inventoryResponse.getCommune() == null ? "" : inventoryResponse.getCommune());
                    newInventory.setLatitude(inventoryResponse.getLatitude() == null ? "" : inventoryResponse.getLatitude());
                    newInventory.setLongitude(inventoryResponse.getLongitude() == null ? "" : inventoryResponse.getLongitude());
                    newInventory.setActive(inventoryResponse.getActive());
                    newInventory.setCreatedAt(LocalDateTime.now());
                    newInventory.setUpdatedAt(LocalDateTime.now());
                    inventoryRepository.save(newInventory);
                    System.out.println("Created new inventory with ID: " + id);
                }
            } else {
                // Xử lý khi không có ID
                Inventory newInventory = new Inventory();
                newInventory.setInventoryName(inventoryResponse.getInventoryName() == null ? "" : inventoryResponse.getInventoryName());
                newInventory.setCity(inventoryResponse.getCity() == null ? "" : inventoryResponse.getCity());
                newInventory.setDistrict(inventoryResponse.getDistrict() == null ? "" : inventoryResponse.getDistrict());
                newInventory.setCommune(inventoryResponse.getCommune() == null ? "" : inventoryResponse.getCommune());
                newInventory.setLatitude(inventoryResponse.getLatitude() == null ? "" : inventoryResponse.getLatitude());
                newInventory.setLongitude(inventoryResponse.getLongitude() == null ? "" : inventoryResponse.getLongitude());
                newInventory.setActive(inventoryResponse.getActive());
                newInventory.setCreatedAt(LocalDateTime.now());
                newInventory.setUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(newInventory);
                System.out.println("Created new inventory without ID.");
            }
        }
    }





    public byte[] exportInventories(List<String> excludedFields) {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryResponse> inventoryResponses = convertToDto(inventories);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Ghi vào outputStream
            genericExporter.exportToExcel(inventoryResponses, InventoryResponse.class, excludedFields, outputStream);
            return outputStream.toByteArray(); // Trả về dữ liệu đã ghi vào outputStream
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu kho hàng");
        }
    }

    public ByteArrayInputStream exportInventoryById(Long id, List<String> excludedFields) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy kho hàng với ID: " + id));
        List<InventoryResponse> inventoryResponses = convertToDto(List.of(inventory));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            genericExporter.exportToExcel(inventoryResponses, InventoryResponse.class, excludedFields, outputStream);
            outputStream.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu kho hàng với ID: " + id);
        }
    }
    public void saveAll(CreateInventoryRequest request) {
        // Logic to save the inventory request to the database
        Inventory inventory = new Inventory();
        inventory.setInventoryName(request.getInventoryName());
        inventory.setCity(request.getCity());
        inventory.setDistrict(request.getDistrict());
        inventory.setCommune(request.getCommune());
        inventory.setLatitude(request.getLatitude());
        inventory.setLongitude(request.getLongitude());
        inventory.setActive(request.getActive());
        inventory.setCreatedAt(request.getCreatedAt());
        inventory.setUpdatedAt(request.getUpdatedAt());

        inventoryRepository.save(inventory); // Save to repository
    }

    public List<InventoryResponse> convertToDto(List<Inventory> inventories) {
        return inventories.stream()
                .map(inventory -> InventoryResponse.builder()
                        .id(inventory.getId())
                        .inventoryName(inventory.getInventoryName())
                        .city(inventory.getCity())
                        .district(inventory.getDistrict())
                        .commune(inventory.getCommune())
                        .latitude(inventory.getLatitude())
                        .longitude(inventory.getLongitude())
                        .active(inventory.getActive())
                        .build())
                .collect(Collectors.toList());
    }

}
