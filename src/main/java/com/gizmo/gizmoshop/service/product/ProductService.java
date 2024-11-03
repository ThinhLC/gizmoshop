package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import com.gizmo.gizmoshop.utils.ConvertEntityToResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private StatusProductRepository statusProductRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private ImageService imageService;

    ConvertEntityToResponse convertEntityToResponse = new ConvertEntityToResponse();


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElse(null);
    }

    public Page<ProductResponse> getAllProducts(String productName, Boolean active, int page, int limit, Optional<String> sort) {

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

        Page<Product> productPage = productRepository.findAllByCriteria(productName, active, pageable);

        // Chuyển đổi Product thành ProductResponse
        return productPage.map(this::mapToProductResponse);
    }


    @Transactional
    public ProductResponse updateImage(long productId, List<MultipartFile> files) throws IOException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<ProductImageMapping> productImageMappings = productImageMappingRepository.findByProductId(existingProduct.getId());
        deleteExistingImages(productImageMappings);

        if (files != null && files.size() > 7) {
            throw new InvalidInputException("Chỉ có thể gửi tối đa 7 hình ảnh.");
        }

        if (files != null) {
            saveNewImages(existingProduct, files);
        }

        updateProductThumbnail(existingProduct);

        return mapToProductResponse(existingProduct);
    }

    private void deleteExistingImages(List<ProductImageMapping> productImageMappings) {
        for (ProductImageMapping mapping : productImageMappings) {
            ProductImage productImage = productImageRepository.findById(mapping.getImage().getId())
                    .orElseThrow(() -> new NotFoundException("Image not found"));

            try {
                imageService.deleteImage(productImage.getFileDownloadUri(), "product");
                productImageMappingRepository.delete(mapping);
                productImageRepository.delete(productImage);
            } catch (IOException e) {
                System.err.println("Error deleting image file: " + e.getMessage());
                throw new RuntimeException("Có lỗi khi xóa file", e);
            }
        }
    }

    // Phương thức lưu hình ảnh mới
    private void saveNewImages(Product existingProduct, List<MultipartFile> files) throws IOException {
        System.out.println("đang bắt đầu lưu hình");
        for (MultipartFile uploadedFile : files) {
            // Lưu hình ảnh vào hệ thống và cơ sở dữ liệu
            String imagePath = imageService.saveImage(uploadedFile, "product");
            System.out.println("1dasd");
            System.out.println(imagePath);
            // Tạo đối tượng ProductImage mới và lưu vào cơ sở dữ liệu
            ProductImage newProductImage = new ProductImage();
            newProductImage.setFileDownloadUri(imagePath);
            productImageRepository.save(newProductImage);

            // Tạo liên kết giữa sản phẩm và ảnh mới
            ProductImageMapping newMapping = new ProductImageMapping();
            newMapping.setProduct(existingProduct);
            newMapping.setImage(newProductImage);
            productImageMappingRepository.save(newMapping);
            System.out.println("đã lưu hình");
        }
    }

    private void updateProductThumbnail(Product existingProduct) {
        List<ProductImageMapping> updatedImageMappings = productImageMappingRepository.findByProductId(existingProduct.getId());
        if (!updatedImageMappings.isEmpty()) {
            ProductImageMapping firstImageMapping = updatedImageMappings.get(0);
            existingProduct.setThumbnail(firstImageMapping.getImage().getFileDownloadUri());
            productRepository.save(existingProduct);
        }
    }



    public ProductResponse createProduct(CreateProductRequest createProductRequest, long authorId) {
        Product product = new Product();

        Optional<Account> author = accountRepository.findById(authorId);
        Optional<Categories> categories = categoriesRepository.findById(createProductRequest.getProductCategoryId());
        Optional<ProductBrand> productBrand = productBrandRepository.findById(createProductRequest.getProductBrandId());
        Optional<StatusProduct> statusProduct = statusProductRepository.findById(createProductRequest.getProductStatusResponseId());

        author.ifPresent(product::setAuthor);
        categories.ifPresent(product::setCategory);
        productBrand.ifPresent(product::setBrand);
        statusProduct.ifPresent(product::setStatus);

        product.setName(createProductRequest.getProductName());
        product.setArea(createProductRequest.getProductArea()); //diện tích
        product.setHeight(createProductRequest.getProductHeight()); //chiều coa
        product.setLength(createProductRequest.getProductLength()); //dài
        product.setWidth(createProductRequest.getWidth());
        product.setDiscountProduct(createProductRequest.getDiscountProduct());
        product.setWeight(createProductRequest.getProductWeight()); //cân năngj
        product.setVolume(createProductRequest.getProductVolume()); //thể tích
        product.setIsSupplier(false);
        product.setPrice(createProductRequest.getProductPrice());
        product.setDiscountProduct(createProductRequest.getDiscountProduct());
        product.setLongDescription(createProductRequest.getProductLongDescription());
        product.setShortDescription(createProductRequest.getProductShortDescription());
        product.setCreateAt(createProductRequest.getProductCreationDate() != null ? createProductRequest.getProductCreationDate() : LocalDateTime.now());
        product.setUpdateAt(createProductRequest.getProductUpdateDate() != null ? createProductRequest.getProductUpdateDate() : LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        return  findProductById(savedProduct.getId());

    }


    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .discountProduct(product.getDiscountProduct())
                .productImageMappingResponse(getProductImageMappings(product.getId()))
                .productInventoryResponse(getProductInventoryResponse(product))
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .thumbnail(product.getThumbnail())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(convertEntityToResponse.mapToStatusResponse(product.getStatus()))
                .productCreationDate(product.getCreateAt())
                .isSupplier(product.getIsSupplier())
                .productUpdateDate(product.getUpdateAt())
                .author(convertEntityToResponse.author(product.getAuthor()))
                .isSupplier(product.getIsSupplier())
                .build();
    }

    public List<ProductImageMappingResponse> getProductImageMappings(Long productId) {
        List<ProductImageMapping> mappings = productImageMappingRepository.findByProductId(productId);

        if(mappings == null){
            return null;
        }

        return mappings.stream()
                .map(mapping -> ProductImageMappingResponse.builder()
                        .id(mapping.getId())
                        .idProduct(mapping.getProduct().getId()) // Lấy ID của Product
                        .idProductImage(mapping.getImage().getId()) // Lấy ID của ProductImage
                        .fileDownloadUri(mapping.getImage().getFileDownloadUri()) // Lấy đường dẫn hình ảnh
                        .build())
                .collect(Collectors.toList());
    }


    private ProductInventoryResponse getProductInventoryResponse(Product product) {
        ProductInventory productInventory = product.getProductInventory(); // Lấy ProductInventory từ Product

        if (productInventory == null) {
            return null;
        }

        return ProductInventoryResponse.builder()
                .id(productInventory.getId())
                .inventory(InventoryResponse.builder()
                        .id(productInventory.getInventory().getId())
                        .inventoryName(productInventory.getInventory().getInventoryName())
                        .active(productInventory.getInventory().getActive())
                        .build())
                .quantity(productInventory.getQuantity())
                .build();
    }

}
