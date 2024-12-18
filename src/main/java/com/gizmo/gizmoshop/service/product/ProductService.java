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
import java.util.Collections;
import java.util.HashSet;
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
    private ProductInventoryRepository productInventoryRepository;
    @Autowired
    private WishlistItemsRepository wishlistItemsRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ImageService imageService;

    ConvertEntityToResponse convertEntityToResponse = new ConvertEntityToResponse();
    @Autowired
    private InventoryRepository inventoryRepository;


    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse findProductById(Long id) {
        System.err.println(id);
        return productRepository.findById(id)
                .map(this::mapToProductResponse)
                .orElse(null);
    }

    public Page<ProductResponse> findProductsByAuthorId(Long idAuthor, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Product> products = productRepository.findByAuthId(idAuthor, pageable);
        return products.map(this::mapToProductResponse);
    }


    public Page<ProductResponse> findAllProductsForClient(int page, int limit, Optional<String> sort, Long price1, Long price2, String sortFieldCase, Long brand, Long category, String keyword) {
        String sortField = "id"; // Mặc định là 'id'
        Sort.Direction sortDirection = Sort.Direction.ASC;
        String keywordTrimmed = (keyword != null) ? keyword.trim() : null;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));

        Page<Product> products = productRepository.findAllProductsForClient(price1, price2, brand, category, keywordTrimmed, sortFieldCase, pageable);

        return products.map(this::mapToProductResponseForClient);
    }

    public Page<ProductResponse> findProductByIdBrand(Long BrandID, Pageable pageable) {
        Page<Product> products = productRepository.findByBrand(BrandID, pageable);
        return products.map(this::mapToProductResponseForClient);
    }


    public ProductResponse findProductDetailForClient(Long idProduct) {
        Optional<Product> product = productRepository.findProductDetailForClient(idProduct);
        if (product.isPresent()) {
            //+1 view
            product.get().setView(product.get().getView() == null ? 0 : product.get().getView() + 1);
            productRepository.save(product.get());
        }
        return product.map(this::mapToProductDetailResponseForClient).orElse(null);
    }

    public Page<ProductResponse> getAllProducts(String productName, Boolean active, int page, int limit, Optional<String> sort, Boolean isSupplier, Long idStatus) {

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

        Page<Product> productPage = productRepository.findAllByCriteria(productName, active, pageable, isSupplier, idStatus);

        return productPage.map(this::mapToProductResponse);
    }


    @Transactional
    public ProductResponse updateImage(long productId, List<MultipartFile> files) throws IOException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<ProductImageMapping> productImageMappings = productImageMappingRepository.findByProductId(existingProduct.getId());
        System.out.println("dongf 77");
        if(productImageMappings.size()!=0){
            deleteExistingImages(productImageMappings);
        }

        if (files != null && files.size() > 7) {
            throw new InvalidInputException("Chỉ có thể gửi tối đa 7 hình ảnh.");
        }

        if (files != null) {
            saveNewImages(existingProduct, files);
        }

        updateProductThumbnail(existingProduct);

        return mapToProductResponse(existingProduct);
    }

    @Transactional
    public void deleteExistingImages(List<ProductImageMapping> productImageMappings) {
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

    @Transactional
    public void saveNewImages(Product existingProduct, List<MultipartFile> files) throws IOException {
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


    @Transactional
    public ProductResponse createProduct(CreateProductRequest createProductRequest, long authorId) {
        System.err.println(createProductRequest.getWidth());
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
        product.setLength(createProductRequest.getProductLength());
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
        product.setAuthor(author.get());
        Product savedProduct = productRepository.save(product);

        //kiểm tra id kho sau đó thêm vào bảng productInventory
        Optional<Inventory> inventory = inventoryRepository.findById(createProductRequest.getInventoryId());
        if (inventory.isPresent()) {
            ProductInventory productInventory = new ProductInventory();
            productInventory.setProduct(savedProduct);
            productInventory.setInventory(inventory.get());
            System.err.println("đây là số lượng sản phẩm: " + createProductRequest.getQuantity());
            productInventory.setQuantity(createProductRequest.getQuantity());
            productInventoryRepository.save(productInventory);
        }
        System.err.println("lưu hoàn tất");
        return findProductById(savedProduct.getId());

    }

    public List<ProductImageMappingResponse> getProductImageMappings(long productId) {
        List<ProductImageMapping> mappings = productImageMappingRepository.findByProductId(productId);
        // Tránh trả về null, trả về danh sách rỗng khi không có dữ liệu
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();  // Trả về danh sách rỗng
        }

        // Chuyển đổi dữ liệu từ entity sang DTO
        return mappings.stream()
                .map(mapping -> {
                    ProductImage productImage = mapping.getImage();
                    return ProductImageMappingResponse.builder()
                            .id(mapping.getId())
                            .idProduct(mapping.getProduct().getId())
                            .image(Collections.singletonList(
                                    ProductImageResponse.builder()
                                            .id(productImage.getId())
                                            .fileDownloadUri(productImage.getFileDownloadUri())
                                            .build())
                            )
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductDetailResponseForClient(Product product) {
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
                .soldProduct(countSoldProduct(product.getId()))
                .thumbnail(product.getThumbnail())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(null)
                .productCreationDate(null)
                .isSupplier(null)
                .view(product.getView() != null ? product.getView() : 0L)
                .productUpdateDate(null)
                .author(null)
                .build();
    }

    private ProductResponse mapToProductResponseForClient(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .discountProduct(product.getDiscountProduct())
                .productImageMappingResponse(null)
                .productInventoryResponse(getProductInventoryResponse(product))
                .productLongDescription(null)
                .productShortDescription(product.getShortDescription())
                .productWeight(null)
                .soldProduct(countSoldProduct(product.getId()))
                .thumbnail(product.getThumbnail())
                .productArea(null)
                .productVolume(null)
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(null)
                .productCreationDate(null)
                .isSupplier(null)
                .view(product.getView() != null ? product.getView() : 0L)
                .productUpdateDate(null)
                .author(null)
                .build();
    }

    public Long countSoldProduct(Long productId) {
        return productRepository.countSoldProduct(productId);
    }

    private ProductResponse mapToProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .quantityBr(product.getProductInventory() != null && product.getProductInventory().getQuantity() != null ? product.getProductInventory().getQuantity() : 0)
                .productPrice(product.getPrice())
                .discountProduct(product.getDiscountProduct())
                .productImageMappingResponse(getProductImageMappings(product.getId()))

                .productInventoryResponse(
                        getProductInventoryResponse(product)
                )

                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productHeight(product.getHeight())
                .productLength(product.getLength())
                .productWidth(product.getWidth())
                .thumbnail(product.getThumbnail())
                .productArea(product.getArea())
                .productVolume(product.getVolume())
                .productBrand(convertEntityToResponse.mapToBrandResponse(product.getBrand()))
                .productCategories(convertEntityToResponse.mapToCategoryResponse(product.getCategory()))
                .productStatusResponse(convertEntityToResponse.mapToStatusResponse(product.getStatus()))
                .productCreationDate(product.getCreateAt())
                .isSupplier(product.getIsSupplier())
                .view(product.getView() != null ? product.getView() : 0L)
                .productUpdateDate(product.getUpdateAt())
                .author(convertEntityToResponse.author(product.getAuthor()))
                .build();
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

    public List<ProductDemoResponse> getProducts(int month, int year, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        Page<Product> productPage = productRepository.findAllProducts(pageable);

        return productPage.getContent().stream()
                .map(product -> {
                    int soldQuantity = getSoldQuantity(product.getId(), month, year);
                    int favoriteCount = getFavoriteCount(product.getId(), month, year);
                    int viewCount = getViewCount(product);

                    return ProductDemoResponse.builder()
                            .product(buildProductResponse(product))
                            .view(viewCount)
                            .quantity(soldQuantity)
                            .favorite(favoriteCount)
                            .build();
                })
                .collect(Collectors.toList());
    }


    private int getViewCount(Product product) {
        return product.getView() != null ? product.getView().intValue() : 0; // Trả về 0 nếu view là null
    }


    public int getSoldQuantity(Long productId, int month, int year) {
        Integer quantity = orderDetailRepository.countQuantityByProductAndMonth(productId, month, year);
        return quantity != null ? quantity : 0;
    }

    private int getFavoriteCount(Long productId, int month, int year) {
        return wishlistItemsRepository.countFavoritesByProductAndMonth(productId, month, year);
    }

    public ProductResponse updateProduct(Long productId, CreateProductRequest createProductRequest) {
        System.err.println(createProductRequest.getWidth());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setName(createProductRequest.getProductName() != null ? createProductRequest.getProductName() : product.getName());
        product.setLongDescription(createProductRequest.getProductLongDescription() != null ? createProductRequest.getProductLongDescription() : product.getLongDescription());
        product.setShortDescription(createProductRequest.getProductShortDescription() != null ? createProductRequest.getProductShortDescription() : product.getShortDescription());
        product.setPrice(createProductRequest.getProductPrice() != null ? createProductRequest.getProductPrice() : product.getPrice());
        product.setDiscountProduct(createProductRequest.getDiscountProduct());
        product.setWeight(createProductRequest.getProductWeight() != null ? createProductRequest.getProductWeight() : product.getWeight());
        product.setLength(createProductRequest.getProductLength() != null ? createProductRequest.getProductLength() : product.getLength());
        product.setHeight(createProductRequest.getProductHeight() != null ? createProductRequest.getProductHeight() : product.getHeight());
        product.setWidth(createProductRequest.getWidth() != null ? createProductRequest.getWidth() : product.getWidth());
        product.setArea(createProductRequest.getProductArea() != null ? createProductRequest.getProductArea() : product.getArea());
        product.setVolume(createProductRequest.getProductVolume() != null ? createProductRequest.getProductVolume() : product.getVolume());

        product.setUpdateAt(LocalDateTime.now());
        if (createProductRequest.getProductBrandId() != null) {
            ProductBrand brand = productBrandRepository.findById(createProductRequest.getProductBrandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found"));
            product.setBrand(brand);
        }
        if (createProductRequest.getProductCategoryId() != null) {
            Categories category = categoriesRepository.findById(createProductRequest.getProductCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (createProductRequest.getProductStatusResponseId() != null) {
            StatusProduct status = statusProductRepository.findById(createProductRequest.getProductStatusResponseId())
                    .orElseThrow(() -> new NotFoundException("StatusProduct not found"));
            product.setStatus(status);
        }
        if (createProductRequest.getAuthorId() != null) {
            Account author = accountRepository.findById(createProductRequest.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Author not found"));
            product.setAuthor(author);
        }

        Optional<Inventory> inventory = inventoryRepository.findById(createProductRequest.getInventoryId());
        System.out.println(inventory.get().getId());

        Optional<ProductInventory> optionalProductInventory = productInventoryRepository.findByProductId(product.getId());
        ProductInventory productInventory;

        if (optionalProductInventory.isPresent()) {
            // Lấy đối tượng từ Optional và cập nhật số lượng
            productInventory = optionalProductInventory.get();
            productInventory.setInventory(inventory.get()); // Gán kho
            System.err.println("Đây là số lượng sản phẩm:" + createProductRequest.getQuantity());
            productInventory.setQuantity(createProductRequest.getQuantity());
        } else {
            // Khởi tạo mới nếu không tồn tại
            productInventory = new ProductInventory();
            productInventory.setProduct(product); // Gán sản phẩm vào inventory
            productInventory.setInventory(inventory.get()); // Gán kho
            productInventory.setQuantity(createProductRequest.getQuantity()); // Gán số lượng
        }
        System.out.println("số lượng " + productInventory.getQuantity());
        System.out.println("kho nào" + productInventory.getInventory().getId());

        // Lưu đối tượng ProductInventory
        productInventoryRepository.save(productInventory);

        Product product1 = productRepository.save(product);
        return buildProductResponse(product1);

    }


    public ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productImageMappingResponse(getProductImageMappings(product.getId()))
                .productInventoryResponse(convertToProductInventoryResponse(product.getProductInventory())) // Lấy thông tin kho
                .productPrice(product.getPrice())
                .productHeight(product.getHeight())
                .productBrand(convertToProductBrandResponse(product.getBrand()))
                .productArea(product.getArea())
                .isSupplier(product.getIsSupplier())
                .productLength(product.getLength())
                .productWeight(product.getWeight())
                .productVolume(product.getVolume())
                .view(product.getView())
                .discountProduct(product.getDiscountProduct())
                .thumbnail(product.getThumbnail())
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productCategories(convertToProductCategoryResponses(product.getCategory()))
                .productStatusResponse(convertToProductStatusResponse(product.getStatus()))
                .author(convertToAccountResponse(product.getAuthor()))
                .productCreationDate(product.getCreateAt())
                .productUpdateDate(product.getUpdateAt())
                .build();
    }

    private ProductInventoryResponse convertToProductInventoryResponse(ProductInventory productInventory) {
        if (productInventory == null) {
            // Trả về một đối tượng ProductInventoryResponse mặc định khi productInventory là null
            return ProductInventoryResponse.builder()
                    .id(0L) // Có thể chọn giá trị mặc định
                    .quantity(0) // Số lượng mặc định
                    .build();
        }
        return ProductInventoryResponse.builder()
                .id(productInventory.getId())
                .quantity(productInventory.getQuantity())

                .build();
    }

    private BrandResponseDto convertToProductBrandResponse(ProductBrand productBrand) {
        if (productBrand == null) {
            // Trả về một đối tượng ProductInventoryResponse mặc định khi productInventory là null
            return null;
        }
        return BrandResponseDto.builder()
                .id(productBrand.getId())
                .deleted(productBrand.getDeleted())
                .description(productBrand.getDescription())
                .name(productBrand.getName())
                .build();
    }

    private CategoriesResponse convertToProductCategoryResponses(Categories categories) {
        if (categories == null) {
            return null; // Trả về danh sách rỗng nếu không có danh mục
        }
        return CategoriesResponse.builder()
                .id(categories.getId())
                .name(categories.getName())
                .image(categories.getImageId()).active(categories.getActive())
                .createAt(categories.getCreateAt())
                .updateAt(categories.getUpdateAt()).build();
    }

    private ProductImageResponse convertToProductImageResponses(ProductImage productImage) {
        if (productImage == null) {
            return null; // Trả về danh sách rỗng nếu không có danh mục
        }
        return ProductImageResponse.builder()
                .id(productImage.getId())
                .fileDownloadUri(productImage.getFileDownloadUri()).build();
    }

    private ProductStatusResponse convertToProductStatusResponse(StatusProduct statusProduct) {
        if (statusProduct == null) {
            return null;
        }
        return ProductStatusResponse.builder()
                .id(statusProduct.getId()).name(statusProduct.getName()).build();

    }

    public AccountResponse convertToAccountResponse(Account author) {
        return AccountResponse.builder()
                .id(author.getId())
                .email(author.getEmail())  // Thêm các thuộc tính cần thiết
                .fullname(author.getFullname())
                .sdt(author.getSdt())
                .birthday(author.getBirthday())
                .image(author.getImage() != null ? author.getImage() : "default-image.png") // Giá trị mặc định nếu không có hình ảnh
                .roles(author.getRoleAccounts() != null ? author.getRoleAccounts().stream().map(role -> role.getRole().getName()).collect(Collectors.toSet()) : new HashSet<>())
                .deleted(author.getDeleted())
                .createAt(author.getCreate_at())
                .updateAt(author.getUpdate_at())
                .extra_info(author.getExtra_info())
                .extraInfo(author.getExtra_info())
                .build();
    }


}
