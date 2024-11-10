package com.gizmo.gizmoshop.service.Categories;

import com.gizmo.gizmoshop.dto.reponseDto.CategoriesResponse;
import com.gizmo.gizmoshop.dto.reponseDto.CategoryStatisticsDto;
import com.gizmo.gizmoshop.dto.requestDto.CategoriesRequestDto;
import com.gizmo.gizmoshop.entity.Categories;
import com.gizmo.gizmoshop.excel.GenericExporter;
import com.gizmo.gizmoshop.exception.BrandNotFoundException;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.CategoriesRepository;
import com.gizmo.gizmoshop.repository.ProductRepository;
import com.gizmo.gizmoshop.service.Image.ImageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private GenericExporter<CategoriesResponse> genericExporter;

    // Phương thức để lấy tất cả các thể loại dưới dạng danh sách
    public List<CategoriesResponse> getAllCategories() {
        List<Categories> categories = categoriesRepository.findAll();
        return categories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Phương thức để lấy tất cả các thể loại dưới dạng danh sách
    public List<CategoryStatisticsDto> getCategoriesProduct() {
        List<Categories> categories = categoriesRepository.findAll();
        List<CategoryStatisticsDto> result = categories.stream()
                .map(category -> {
                    int quantity = category.getProducts().size();
                    int quantityActive = Math.toIntExact(category.getProducts().stream()
                            .filter(product -> product.getDeleted() != null && !product.getDeleted())
                            .count());
                    return CategoryStatisticsDto.builder()
                            .id(category.getId())
                            .active(category.getActive() != null ? category.getActive() : false)
                            .name(category.getName())
                            .quantity(quantity)
                            .quantityActive(quantityActive)
                            .build();
                })
                .collect(Collectors.toList());

        Collections.reverse(result); // Đảo ngược danh sách
        return result;
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
                .image(category.getImageId())
                .active(category.getActive())
                .createAt(category.getCreateAt())
                .updateAt(category.getUpdateAt())
                .build();
    }

    public CategoriesResponse createCategories(CategoriesRequestDto categoriesRequestDto){
        if (categoriesRepository.existsByName(categoriesRequestDto.getName())) {
            throw new InvalidInputException("Categories already exists with name: " + categoriesRequestDto.getName());
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
            throw new InvalidInputException("Categories not found with id: " + id);
        }

        Categories existingCategories = existingCategoriesOpt.get();
        existingCategories.setName(categoriesRequestDto.getName());
        existingCategories.setActive(categoriesRequestDto.getActive());
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
    public CategoriesResponse updateImage(long id, Optional <MultipartFile> file) {
        Optional<Categories> existingCategoriesOpt = categoriesRepository.findById(id);
        if (existingCategoriesOpt.isEmpty()) {
            throw new BrandNotFoundException("Danh mục không tồn tại với ID: " + id);
        }

        Categories existingCategories = existingCategoriesOpt.get();

        // Kiểm tra tệp hình ảnh có được cung cấp không
        if (file.isPresent() && !file.get().isEmpty()) {
            try {
                // Nếu danh mục đã có hình ảnh, xóa hình ảnh cũ
                if (!existingCategories.getImageId().isEmpty()) {
                    imageService.deleteImage(existingCategories.getImageId(), "category");
                }
                String imagePath = imageService.saveImage(file.get(), "category");
                existingCategories.setImageId(imagePath); // Cập nhật ID hình ảnh mới
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new InvalidInputException("Lỗi khi xử lý hình ảnh: " + e.getMessage());
            }
        } else {
            System.out.println("Tệp hình ảnh không được cung cấp.");
        }

        // Cập nhật thời gian sửa đổi
        existingCategories.setUpdateAt(LocalDateTime.now());

        // Lưu danh mục đã cập nhật vào cơ sở dữ liệu
        Categories updatedCategories = categoriesRepository.save(existingCategories);

        return mapToDto(updatedCategories);

    }
    public byte[] getImage(String filename){
        byte[] imageData = new byte[0];
        try {
            imageData = imageService.loadImageAsResource(filename,"category");
        } catch (IOException e) {
            throw new InvalidInputException("Could not load");
        }

        return imageData;
    }

    public CategoriesResponse getCategoryById(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Danh mục không tồn tại với ID: " + id));
        return mapToDto(category);
    }
    @Transactional
    public void importCategories(MultipartFile file) throws IOException {
        List<CategoriesResponse> categoriesResponses = genericExporter.importFromExcel(file, CategoriesResponse.class);

        for (CategoriesResponse categoryResponse : categoriesResponses) {
            Long id = categoryResponse.getId();
            System.out.println("Processing Category ID: " + id);

            Categories category;

            if (id != null) {
                Optional<Categories> existingCategoryOpt = categoriesRepository.findById(id);

                if (existingCategoryOpt.isPresent()) {
                    category = existingCategoryOpt.get();
                    category.setName(categoryResponse.getName());
                    category.setActive(categoryResponse.getActive());
                    category.setImageId("");
                    category.setUpdateAt(LocalDateTime.now());
                    categoriesRepository.save(category);
                    System.out.println("Updated existing category with ID: " + id);
                } else {
                    category = new Categories();
                    category.setId(id);
                    category.setName(categoryResponse.getName()== null ? " " : categoryResponse.getName());
                    category.setActive(categoryResponse.getActive());
                    category.setImageId("");
                    category.setCreateAt(LocalDateTime.now());
                    category.setUpdateAt(LocalDateTime.now());
                    categoriesRepository.save(category);
                    System.out.println("Created new category with ID: " + id);
                }
            } else {
                category = new Categories();
                category.setName(categoryResponse.getName() == null ? "" : categoryResponse.getName());
                category.setActive(categoryResponse.getActive());
                category.setImageId("");
                category.setCreateAt(LocalDateTime.now());
                category.setUpdateAt(LocalDateTime.now());
                categoriesRepository.save(category);
                System.out.println("Created new category without ID.");
            }
        }
    }

    public byte[] exportCategories(List<String> excludedFields) {
        List<Categories> categories = categoriesRepository.findAll();
        List<CategoriesResponse> categoryResponses = convertToDto(categories);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            genericExporter.exportToExcel(categoryResponses, CategoriesResponse.class, excludedFields, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu danh mục");
        }
    }
    public byte[] exportCategoryById(Long id, List<String> excludedFields) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy danh mục với ID: " + id));
        List<CategoriesResponse> categoryResponses = convertToDto(List.of(category));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            genericExporter.exportToExcel(categoryResponses, CategoriesResponse.class, excludedFields, outputStream);
            return outputStream.toByteArray(); // Trả về dữ liệu đã ghi vào outputStream dưới dạng byte[]
        } catch (IOException e) {
            throw new InvalidInputException("Lỗi khi xuất dữ liệu danh mục với ID: " + id);
        }
    }



    private List<CategoriesResponse> convertToDto(List<Categories> categories) {
        return categories.stream()
                .map(category -> CategoriesResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .active(category.getActive())
                        .build())
                .collect(Collectors.toList());
    }

}
