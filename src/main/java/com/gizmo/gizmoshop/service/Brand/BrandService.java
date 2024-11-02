package com.gizmo.gizmoshop.service.Brand;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.BrandStatisticsDto;
import com.gizmo.gizmoshop.dto.reponseDto.CategoryStatisticsDto;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.entity.ProductBrand;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.DuplicateBrandException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
import com.gizmo.gizmoshop.repository.ProductBrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private ProductBrandRepository productBrandRepository;

    public Page<BrandResponseDto> findBrandCriteria(String name, Boolean active, Pageable pageable) {
        return productBrandRepository.findBrandResponseDtos(name, active, pageable);
    }
    public BrandResponseDto getBrandById(long id) {
        ProductBrand brand = productBrandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        return buildBrandResponse(brand);
    }
    private BrandResponseDto buildBrandResponse(ProductBrand brand) {
        return BrandResponseDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .deleted(brand.getDeleted())
                .build();
    }

    public Page<BrandResponseDto> getAllBrands(Pageable pageable) {
        return productBrandRepository.findAll(pageable).map(this::mapToDto);
    }

    public BrandResponseDto createBrand(BrandRequestDto brandRequestDto) {
        // Ví dụ: kiểm tra xem thương hiệu đã tồn tại chưa (với tên trùng lặp)
        if (productBrandRepository.existsByName(brandRequestDto.getName())) {
            throw new InvalidInputException("Brand already exists with name: " + brandRequestDto.getName());
        }

        ProductBrand newBrand = new ProductBrand();
        newBrand.setName(brandRequestDto.getName());
        newBrand.setDescription(brandRequestDto.getDescription());
        newBrand.setDeleted(brandRequestDto.getDeleted());

        ProductBrand savedBrand = productBrandRepository.save(newBrand);
        return mapToDto(savedBrand);
    }

    public BrandResponseDto updateBrand(Long id, BrandRequestDto brandRequestDto) {
        Optional<ProductBrand> existingBrandOpt = productBrandRepository.findById(id);
        if (existingBrandOpt.isEmpty()) {
            throw new BrandNotFoundException("Brand not found with id: " + id);
        }

        ProductBrand existingBrand = existingBrandOpt.get();
        existingBrand.setName(brandRequestDto.getName());
        existingBrand.setDescription(brandRequestDto.getDescription());
        existingBrand.setDeleted(brandRequestDto.getDeleted());

        ProductBrand updatedBrand = productBrandRepository.save(existingBrand);
        return mapToDto(updatedBrand);
    }

    public void deleteBrand(Long id) {
        ProductBrand productBrand = productBrandRepository.findByIdAndDeletedFalse(id);
        if (productBrand == null) {
            throw new ResourceNotFoundException("Thương hiệu không tồn tại hoặc đã bị xóa");
        }
        productBrand.setDeleted(true);
        productBrandRepository.save(productBrand);
    }

    public BrandResponseDto changeActiveById(long id) {
        ProductBrand brand = productBrandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        brand.setDeleted(!brand.getDeleted());
        ProductBrand updatedInventory = productBrandRepository.save(brand);
        return buildBrandResponse(updatedInventory);
    }

    public List<BrandStatisticsDto> getBrandsProduct() {
        List<ProductBrand> brands = productBrandRepository.findAll();
        List<BrandStatisticsDto> result = brands.stream()
                .map(brand -> {
                    int quantity = brand.getProducts().size();
                    int quantityActive = Math.toIntExact(brand.getProducts().stream()
                            .filter(product -> product.getDeleted() != null && !product.getDeleted())
                            .count());
                    return BrandStatisticsDto.builder()
                            .active(brand.getDeleted() != null ? !brand.getDeleted() : false)
                            .id(brand.getId())
                            .name(brand.getName())
                            .quantity(quantity)
                            .quantityActive(quantityActive)
                            .build();
                })
                .collect(Collectors.toList());

        Collections.reverse(result); // Đảo ngược danh sách
        return result;
    }

    // Lấy tất cả thương hiệu chưa bị xóa (deleted = false)
    public Page<BrandResponseDto> getAllBrandsWithPagination(Pageable pageable) {
        Page<ProductBrand> brandPage = productBrandRepository.findByDeletedFalse(pageable);
        return brandPage.map(this::mapToDto);
    }

    private BrandResponseDto mapToDto(ProductBrand brand) {
        return new BrandResponseDto(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getDeleted()
        );
    }
}