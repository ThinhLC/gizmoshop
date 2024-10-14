package com.gizmo.gizmoshop.controller.categories;

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
    public ResponseEntity<Page<CategoriesResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sort[0])));

        Page<CategoriesResponse> categoriesPage = categoriesService.getAllCategoriesWithPagination(pageable);

        return new ResponseEntity<>(categoriesPage, HttpStatus.OK);
    }
}
