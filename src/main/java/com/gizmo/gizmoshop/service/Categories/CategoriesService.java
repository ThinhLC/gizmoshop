package com.gizmo.gizmoshop.service.Categories;

import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.InventoryResponse;
import com.gizmo.gizmoshop.dto.requestDto.CategoriesRequestDto;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.entity.Inventory;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.DuplicateBrandException;
import com.gizmo.gizmoshop.exception.ResourceNotFoundException;
import com.gizmo.gizmoshop.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    // Phương thức để lấy tất cả các thể loại dưới dạng danh sách
    public List<CategoriesResponse> getAllCategories() {
        List<Categories> categories = categoriesRepository.findByActiveTrue();
        return categories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Phương thức để lấy thể loại với phân trang
    public Page<CategoriesResponse> getAllCategoriesWithPagination(String keyword, Boolean deleted, Pageable pageable) {
        Page<Categories> categoriesPage = categoriesRepository.findCategorissByCriteria(keyword, deleted, pageable);
        return categoriesPage.map(this::mapToDto);
    }
    // Phương thức để ánh xạ từ Categories entity sang CategoriesResponseDto
    private CategoriesResponse mapToDto(Categories category) {
        return CategoriesResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.getActive())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .build();
    }
    public CategoriesResponse createCategories(CategoriesRequestDto categoriesRequestDto) {
        if (categoriesRepository.existsByName(categoriesRequestDto.getName())) {
            throw new DuplicateBrandException("Brand already exists with name: " + categoriesRequestDto.getName());
        }

        Categories categories = new Categories();
        categories.setName(categoriesRequestDto.getName());
        categories.setActive(false);
        categories.setImageId(categoriesRequestDto.getImage());
        LocalDateTime now = LocalDateTime.now();
        categories.setCreateAt(now);
        categories.setUpdateAt(now);

        Categories savedCategories = categoriesRepository.save(categories);

        return mapToDto(savedCategories);
    }

    public CategoriesResponse updateCategories(Long id, CategoriesRequestDto categoriesRequestDto) {
        Optional<Categories> existingCategoriesOpt = categoriesRepository.findById(id);
        if (existingCategoriesOpt.isEmpty()) {
            throw new BrandNotFoundException("Categories not found with id: " + id);
        }

        Categories existingCategories = existingCategoriesOpt.get();
        existingCategories.setName(categoriesRequestDto.getName());
        existingCategories.setActive(categoriesRequestDto.getActive());
        existingCategories.setImageId(categoriesRequestDto.getImage());
        existingCategories.setUpdateAt(LocalDateTime.now());

        Categories updatedCategories = categoriesRepository.save(existingCategories);
        return mapToDto(updatedCategories);
    }

    public CategoriesResponse changeActiveById(long id) {
        Categories categories = categoriesRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Inventory not found with id: " + id));
        categories.setActive(!categories.getActive());
        Categories updatedCategories = categoriesRepository.save(categories);
        return mapToDto(updatedCategories);
    }
}
