package com.gizmo.gizmoshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.SupplierRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    @Autowired
    private SuppilerInfoRepository suppilerInfoRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleAccountRepository roleAccountRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private StatusProductRepository statusProductRepository;



    public void SupplierRegisterBusinessNotApi(long accountId ,long walletId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
        String supplierInfoJson = account.getNoteregistersupplier();//build lai
        if (supplierInfoJson == null || supplierInfoJson.isEmpty()) {
            throw new NotFoundException("Thông tin nhà cung cấp không tồn tại");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SupplierRequest supplierRequest = objectMapper.readValue(supplierInfoJson, SupplierRequest.class);
            SupplierRegister(supplierRequest,accountId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chuyển đổi thông tin nhà cung cấp", e);
        }

    }
    @Transactional
    public void SupplierRegister(SupplierRequest supplierRequest, Long AccountId) {
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(AccountId);
        if (supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản đã đăng ký trở thành đối tác");
        }

        Account account = accountRepository.findById(AccountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

        Optional<SupplierInfo> checkTaxcode=  suppilerInfoRepository.findByTaxCode(supplierRequest.getTax_code());
        if (checkTaxcode.isPresent()) {
            throw new UserAlreadyExistsException("Mã số thuế của bạn đã được đăng kí");
        }

        SupplierInfo supplierInfo1 = new SupplierInfo();
        supplierInfo1.setAccount(account);
        supplierInfo1.setDeleted(true);
        supplierInfo1.setBusiness_name(supplierRequest.getNameSupplier());
        supplierInfo1.setDescription(supplierRequest.getDescription());
        supplierInfo1.setTaxCode(supplierRequest.getTax_code());
        supplierInfo1.setBalance(0L);
        supplierInfo1.setFrozen_balance(200000L);
        suppilerInfoRepository.save(supplierInfo1);

        RoleAccount roleAccount = new RoleAccount();
        roleAccount.setAccount(account);

        Role supplierRole = new Role();
        supplierRole.setId(5L);
        roleAccount.setRole(supplierRole);

        roleAccountRepository.save(roleAccount);
    }

    public void updateSupplierDeletedStatus(Long supplierId, boolean deleted) {
        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(supplierId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Supplier với ID: " + supplierId));

        supplierInfo.setDeleted(deleted);
        suppilerInfoRepository.save(supplierInfo);
    }

    @Transactional
    public void CreateOrderBySupplier(List<CreateProductRequest> createProductRequests, Long accountId) {
        // Lấy tài khoản của người tạo đơn hàng
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản"));

        // Kiểm tra xem danh sách sản phẩm có trống hay không
        if (createProductRequests == null || createProductRequests.isEmpty()) {
            throw new InvalidInputException("Bạn chưa thêm bất kỳ sản phẩm nào");
        }

        // Lấy danh sách ID cần thiết
        List<Long> categoryIds = createProductRequests.stream()
                .map(CreateProductRequest::getProductCategoryId)
                .distinct()
                .toList();

        List<Long> brandIds = createProductRequests.stream()
                .map(CreateProductRequest::getProductBrandId)
                .distinct()
                .toList();

        // Lấy danh sách các danh mục và thương hiệu từ database
        Map<Long, Categories> categoryMap = categoriesRepository.findById(categoryIds).stream()
                .collect(Collectors.toMap(Categories::getId, category -> category));

        Map<Long, ProductBrand> brandMap = productBrandRepository.findById(brandIds).stream()
                .collect(Collectors.toMap(ProductBrand::getId, brand -> brand));

        // Xử lý từng sản phẩm
        for (CreateProductRequest request : createProductRequests) {
            // Lấy danh mục và thương hiệu của sản phẩm từ danh sách đã tải về
            Categories category = categoryMap.get(request.getProductCategoryId());
            if (category == null) {
                throw new NotFoundException("Không tìm thấy danh mục sản phẩm với ID: " + request.getProductCategoryId());
            }

            ProductBrand brand = brandMap.get(request.getProductBrandId());
            if (brand == null) {
                throw new NotFoundException("Không tìm thấy thương hiệu với ID: " + request.getProductBrandId());
            }

            // Tạo sản phẩm mới
            Product product = new Product();
            product.setAuthor(account); // Gán tác giả (account)
            product.setName(request.getProductName());
            product.setPrice(request.getProductPrice());
            product.setCategory(category);
            product.setBrand(brand);
            product.setShortDescription(request.getProductShortDescription());
            product.setLongDescription(request.getProductLongDescription());
            product.setDiscountProduct(request.getDiscountProduct());
            product.setThumbnail(request.getThumbnail());
            product.setWeight(request.getProductWeight());
            product.setArea(request.getProductArea());
            product.setVolume(request.getProductVolume());
            product.setWidth(request.getWidth());
            product.setHeight(request.getProductHeight());
            product.setLength(request.getProductLength());
            product.setCreateAt(request.getProductCreationDate() != null ? request.getProductCreationDate() : LocalDateTime.now());
            product.setUpdateAt(request.getProductUpdateDate() != null ? request.getProductUpdateDate() : LocalDateTime.now());

            StatusProduct statusProduct = statusProductRepository.findById(1L)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái sản phẩm với ID: 1"));

            product.setStatus(statusProduct);
            // Lưu sản phẩm
            Product savedProduct = productRepository.save(product);

            // Chú ý: Đã bỏ qua việc tạo và lưu sản phẩm tồn kho (productInventory)
        }
    }


}
