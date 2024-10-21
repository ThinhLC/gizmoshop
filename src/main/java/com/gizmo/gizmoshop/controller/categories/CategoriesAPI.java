package com.gizmo.gizmoshop.controller.categories;

import com.gizmo.gizmoshop.dto.reponseDto.AccountResponse;
import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.service.Categories.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CategoriesAPI {
    @Autowired
    CategoriesService categoriesService;

    @GetMapping("/list/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<CategoriesResponse>>> getAllCategories() {
        List<CategoriesResponse> categories = categoriesService.getAllCategories();
        ResponseWrapper<List<CategoriesResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", categories);
        return ResponseEntity.ok(responseWrapper);  // Trả về 200 OK với đối tượng ResponseWrapper
    }

    @GetMapping("/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<CategoriesResponse>>> getAllCategories(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "deleted", required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Optional<String> sort) {

        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(sortDirection, sortField)));
        Page<CategoriesResponse> categoriesResponses = categoriesService.getAllCategoriesWithPagination(keyword, available, pageable);
        ResponseWrapper<Page<CategoriesResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Categories fetched successfully", categoriesResponses);

        return ResponseEntity.ok(response);
    }
}
