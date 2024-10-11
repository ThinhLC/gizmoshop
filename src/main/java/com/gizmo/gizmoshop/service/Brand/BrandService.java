package com.gizmo.gizmoshop.service.Brand;

import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.entity.ProductBrand;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.DuplicateBrandException;
import com.gizmo.gizmoshop.repository.ProductBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BrandService {

    @Autowired
    private ProductBrandRepository productBrandRepository;

    public Page<BrandResponseDto> getAllBrands(Pageable pageable) {
        return productBrandRepository.findAll(pageable).map(this::mapToDto);
    }

    public BrandResponseDto createBrand(BrandRequestDto brandRequestDto) {
        // Ví dụ: kiểm tra xem thương hiệu đã tồn tại chưa (với tên trùng lặp)
        if (productBrandRepository.existsByName(brandRequestDto.getName())) {
            throw new DuplicateBrandException("Brand already exists with name: " + brandRequestDto.getName());
        }

        ProductBrand newBrand = new ProductBrand();
        newBrand.setName(brandRequestDto.getName());
        newBrand.setDescription(brandRequestDto.getDescription());
        newBrand.setActive(brandRequestDto.getActive());

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
        existingBrand.setActive(brandRequestDto.getActive());

        ProductBrand updatedBrand = productBrandRepository.save(existingBrand);
        return mapToDto(updatedBrand);
    }

    private BrandResponseDto mapToDto(ProductBrand brand) {
        return new BrandResponseDto(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getActive()
        );
    }
}