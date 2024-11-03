package com.gizmo.gizmoshop.service.product;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.utils.ConvertEntityToResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
                .orElse(null); // Trả về null nếu không tìm thấy sản phẩm, hoặc có thể thay thế bằng một phản hồi tùy chỉnh
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

    public ProductResponse createProduct(CreateProductRequest createProductRequest) {
        Product product = new Product();

        Optional<Account> author = accountRepository.findById(createProductRequest.getAuthorId());
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
        product.setWeight(createProductRequest.getProductWeight()); //cân năngj
        product.setVolume(createProductRequest.getProductVolume()); //thể tích
        product.setIsSupplier(false);
        product.setPrice(createProductRequest.getProductPrice());
        product.setLongDescription(createProductRequest.getProductLongDescription());
        product.setShortDescription(createProductRequest.getProductShortDescription());
        product.setThumbnail(createProductRequest.getThumbnail());
        product.setCreateAt(createProductRequest.getProductCreationDate() != null ? createProductRequest.getProductCreationDate() : LocalDateTime.now());
        product.setUpdateAt(createProductRequest.getProductUpdateDate() != null ? createProductRequest.getProductUpdateDate() : LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        return findProductById(savedProduct.getId());

    }


    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productImageMappingResponse(getProductImageMappings(product.getId()))
                .productInventoryResponse(getProductInventoryResponse(product))
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(convertEntityToResponse.mapToStatusResponse(product.getStatus()))
                .productCreationDate(product.getCreateAt())
                .isSupplier(product.getIsSupplier())
                .productUpdateDate(product.getUpdateAt())
                .author(convertEntityToResponse.author(product.getAuthor()))
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
