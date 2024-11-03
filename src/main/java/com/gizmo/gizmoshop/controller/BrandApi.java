package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.BrandRequestDto;
import com.gizmo.gizmoshop.service.Brand.BrandService;
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
@RequestMapping("/api/public/brand")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BrandApi {

    @Autowired
    private BrandService brandService;


    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<BrandResponseDto>>> getBrand() {
        List<BrandResponseDto> response = brandService.getAllBrands();
        ResponseWrapper<List<BrandResponseDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", response);
        return ResponseEntity.ok(responseWrapper);
    }

    @GetMapping("/get/{Id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    ResponseEntity<ResponseWrapper<BrandResponseDto>> getListBrand(@PathVariable Long Id) {
        BrandResponseDto response = brandService.getBrandById(Id);
        ResponseWrapper<BrandResponseDto> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", response);
        return ResponseEntity.ok(responseWrapper);
    }
    /**
     * API tạo thương hiệu - chỉ dành cho ADMIN và STAFF
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')") // Chỉ cho phép ROLE_ADMIN và ROLE_STAFF truy cập
    public ResponseEntity<ResponseWrapper<BrandResponseDto>> createBrand(@RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto newBrand = brandService.createBrand(brandRequestDto);
        ResponseWrapper<BrandResponseDto> response = new ResponseWrapper<>(HttpStatus.CREATED, "Thương hiệu đã được tạo thành công", newBrand);
        return ResponseEntity.ok(response);
    }




    /**
     * API cập nhật thông tin thương hiệu - chỉ dành cho ADMIN và STAFF
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')") // Chỉ cho phép ROLE_ADMIN và ROLE_STAFF truy cập
    public ResponseEntity<ResponseWrapper<BrandResponseDto>> updateBrand(@PathVariable Long id, @RequestBody BrandRequestDto brandRequestDto) {
        BrandResponseDto updatedBrand = brandService.updateBrand(id, brandRequestDto);
        ResponseWrapper<BrandResponseDto> response = new ResponseWrapper<>(HttpStatus.OK, "Thương hiệu đã được cập nhật", updatedBrand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        ResponseWrapper<Void> response = new ResponseWrapper<>(HttpStatus.OK, "Thương hiệu đã được xóa thành công", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/changeactive/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<BrandResponseDto>> changeActive(@PathVariable Long id) {
        BrandResponseDto updated = brandService.changeActiveById(id);
        ResponseWrapper<BrandResponseDto> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Cập nhật thành công",
                updated
        );

        return ResponseEntity.ok(response);
    }
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<Page<BrandResponseDto>>> findInventoriesByCriteria(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "deleted", required = false) Boolean deleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
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

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<BrandResponseDto> brandResponseDtos = brandService.findBrandCriteria(name, deleted, pageable);
        ResponseWrapper<Page<BrandResponseDto>> response = new ResponseWrapper<>(HttpStatus.OK, "Inventories fetched successfully",brandResponseDtos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<List<BrandStatisticsDto>>> getBrandStats() {
        List<BrandStatisticsDto> brandStatisticsDtos = brandService.getBrandsProduct();
        ResponseWrapper<List<BrandStatisticsDto>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin số lượng sản phẩm của từng thương hiệu thành công", brandStatisticsDtos);
        return ResponseEntity.ok(responseWrapper);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<String>> importBrands(@RequestParam("file") MultipartFile file) throws IOException {
        brandService.importBrand(file);
        ResponseWrapper<String> response = new ResponseWrapper<>(HttpStatus.OK, "Import thành công!", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportProductBrands() {
        List<String> excludedFields = Arrays.asList("brand_image"); // Danh sách các trường không xuất
        byte[] excelData = brandService.exportProductBrands(excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=product_brands_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // thêm dòng này cho phép fe đọc đc header resp
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    @GetMapping("/export/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<byte[]> exportProductBrandById(@PathVariable Long id) {
        List<String> excludedFields = Arrays.asList("brand_image"); // Danh sách các trường không xuất
        byte[] excelData = brandService.exportProductBrandById(id, excludedFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=product_brand_" + id + "_export.xlsx");
        headers.add("Access-Control-Expose-Headers", "Content-Disposition"); // thêm dòng này cho phép fe đọc đc header resp

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

}