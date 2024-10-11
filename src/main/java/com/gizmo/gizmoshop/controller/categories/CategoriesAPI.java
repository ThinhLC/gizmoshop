package com.gizmo.gizmoshop.controller.categories;

import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.service.Categories.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoriesAPI {
    @Autowired
    CategoriesService categoriesService;

    @GetMapping("/list/categories")
    @PreAuthorize("permitAll()")
    public List<CategoriesResponse> getAllCategories() {
        return categoriesService.getAllCategories();
    }

    @GetMapping("/page")
    @PreAuthorize("permitAll()")
    public Page<CategoriesResponse> getCategoriesWithPagination(@RequestParam int page, @RequestParam int size) {
        return categoriesService.getCategoriesWithPagination(page, size);
    }
}
