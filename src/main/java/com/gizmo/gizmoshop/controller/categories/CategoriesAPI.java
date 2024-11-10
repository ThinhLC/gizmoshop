package com.gizmo.gizmoshop.controller.categories;


import com.gizmo.gizmoshop.dto.reponseDto.BrandResponseDto;
import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.CategoryStatisticsDto;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.requestDto.CategoriesRequestDto;
import com.gizmo.gizmoshop.service.Categories.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
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

    @PostMapping("/categories/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<CategoriesResponse>> createCategories(@RequestBody CategoriesRequestDto categoriesRequestDto) {
        CategoriesResponse newCategories = categoriesService.createCategories(categoriesRequestDto);
        ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Danh mục đã được tạo thành công", newCategories);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/categories/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<CategoriesResponse>> updateCategories(@PathVariable Long id, @RequestBody CategoriesRequestDto categoriesRequestDto) {
        CategoriesResponse updatedcategories = categoriesService.updateCategories(id, categoriesRequestDto);
        ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Danh mục đã được cập nhật", updatedcategories);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/changeactive/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<CategoriesResponse>> changeActive(@PathVariable Long id) {
        CategoriesResponse updatedCategories = categoriesService.changeActiveById(id);
        ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật thành công",
                updatedCategories
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping(value = "/categories/{id}/updateimage" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<CategoriesResponse>> updateImage(
            @PathVariable Long id,
            @RequestParam("file") Optional<MultipartFile> file) {

        // Gọi phương thức cập nhật hình ảnh từ service
        CategoriesResponse updatedCategory = categoriesService.updateImage(id, file);
        ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Hình ảnh đã được cập nhật thành công",
                updatedCategory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<CategoriesResponse>> getCategoryById(@PathVariable Long id) {
        CategoriesResponse category = categoriesService.getCategoryById(id);

        if (category != null) {
            ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(
                    HttpStatus.OK,
                    "Danh mục được tìm thấy",
                    category
            );
            return ResponseEntity.ok(response);
        } else {
            ResponseWrapper<CategoriesResponse> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy danh mục",
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/category-stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<CategoryStatisticsDto>>> getCategoriesStats() {
        List<CategoryStatisticsDto> categoryStatisticsDtos = categoriesService.getCategoriesProduct();
        ResponseWrapper<List<CategoryStatisticsDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin số lượng sản phẩm của từng danh mục thành công", categoryStatisticsDtos);
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/categories/import")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> importCategories(@RequestParam("file") MultipartFile file) throws IOException {
        categoriesService.importCategories(file);
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "Import thành công!", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories/export")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportCategories() {
        List<String> excludedFields = Arrays.asList("image", "createAt", "updateAt");
        byte[] excelData = categoriesService.exportCategories(excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=categories_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // Cho phép frontend đọc được header resp
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @GetMapping("/categories/export/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportCategoryById(@PathVariable Long id) {
        List<String> excludedFields = Arrays.asList("image", "createAt", "updateAt");
        byte[] excelData = categoriesService.exportCategoryById(id, excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=category_" + id + "_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // Cho phép frontend đọc được header resp

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @GetMapping("/categories/listCategories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<CategoriesResponse>>> getAllBrandsForClient(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<CategoriesResponse> categoriesResponses = categoriesService.getAllCategoriesForClient(pageable);
        ResponseWrapper<Page<CategoriesResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin brand thành công",categoriesResponses);
        return ResponseEntity.ok(response);
    }

}
