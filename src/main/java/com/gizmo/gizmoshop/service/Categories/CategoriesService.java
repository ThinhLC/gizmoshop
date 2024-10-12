package com.gizmo.gizmoshop.service.Categories;

import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;

    // Phương thức để lấy tất cả các thể loại dưới dạng danh sách
    public List<CategoriesResponse> getAllCategories() {
        List<Categories> categories = categoriesRepository.findByActiveFalse();
        return categories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Phương thức để lấy thể loại với phân trang
    public Page<CategoriesResponse> getAllCategoriesWithPagination(Pageable pageable) {
        Page<Categories> categoriesPage = categoriesRepository.findByActiveFalse(pageable);
        return categoriesPage.map(this::mapToDto);
    }
    // Phương thức để ánh xạ từ Categories entity sang CategoriesResponseDto
    private CategoriesResponse mapToDto(Categories category) {
        return CategoriesResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .active(category.getActive())
                .build();
    }
}
