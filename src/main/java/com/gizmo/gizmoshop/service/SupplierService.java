package com.gizmo.gizmoshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.SupplierRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import com.gizmo.gizmoshop.utils.ConvertEntityToResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    @Autowired
    private SuppilerInfoRepository suppilerInfoRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private WithdrawalHistoryRepository withdrawalHistoryRepository;
    @Autowired
    private WalletAccountRepository walletAccountRepository;
    @Autowired
    private RoleAccountRepository roleAccountRepository;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBrandRepository productBrandRepository;

    @Autowired
    private StatusProductRepository statusProductRepository;

    @Autowired
    private AddressAccountRepository addressAccountRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;


    ConvertEntityToResponse convertEntityToResponse = new ConvertEntityToResponse();

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

        if (!supplierInfo.getDeleted()) {
            supplierInfo.setFrozen_balance(supplierInfo.getFrozen_balance() - 150000);
            supplierInfo.setBalance(supplierInfo.getBalance() + 150000);
        }
        supplierInfo.setDeleted(deleted);
        suppilerInfoRepository.save(supplierInfo);
    }

    public SupplierDto getInfo(long idAccount){
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(idAccount);
        if (!supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản không phải đối tác");
        }
        return SupplierDto.builder()
                .Id(supplierInfo.get().getId())
                .balance(supplierInfo.get().getBalance())
                .nameSupplier(supplierInfo.get().getBusiness_name())
                .frozen_balance(supplierInfo.get().getFrozen_balance())
                .description(supplierInfo.get().getDescription())
                .tax_code(supplierInfo.get().getTaxCode())
                .deleted(supplierInfo.get().getDeleted())
                .build();
    }

    @Transactional
    public void withdraw(long accountId, SupplierDto supplier){
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        WalletAccount wallet  = walletAccountRepository.findById(supplier.getWallet()).orElseThrow(
          () -> new InvalidInputException("Ví không tồn tại trong tài khoản")
        );
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId);
        if (!supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản không phải đối tác");
        }
        supplierInfo.get().setBalance(supplierInfo.get().getBalance()-supplier.getBalance());
        suppilerInfoRepository.save(supplierInfo.get());
        //tạo đơn rút tiền
        WithdrawalHistory history = new WithdrawalHistory();
        history.setAccount(account);
        history.setAmount(supplier.getBalance());
        history.setWalletAccount(wallet);
        history.setWithdrawalDate(new Date());
        history.setNote(
                "SUPPLIER|Rút tiền lương |PENDING"
        );
        withdrawalHistoryRepository.save(history);

    }


    @Transactional
    public void DepositNoApi(long accountId , long amount){
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);

        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId);
        if (!supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản không phải đối tác");
        }
        System.out.println("tiền hiện tại : "+ supplierInfo.get().getBalance());
        supplierInfo.get().setBalance(supplierInfo.get().getBalance() + amount);
        suppilerInfoRepository.save(supplierInfo.get());

        //lưu lịch sử giao dịch
        WithdrawalHistory history = new WithdrawalHistory();
        history.setAccount(account);
        history.setAmount(amount);
        history.setWalletAccount(walletAccounts.get(0));
        history.setWithdrawalDate(new Date());
        history.setNote(
                "SUPPLIER|Nộp tiền thành công|COMPETED"
        );
        withdrawalHistoryRepository.save(history);
    }


    public SupplierDto OrderCountBySupplier(long accountID ,List<String> statusId){
        Account account = accountRepository.findById(accountID).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        List<Order> ordersBySupplier= orderRepository.findOrdersByAccountIdAndStatusRoleOne(account.getId());
        List<Long> statusIdsLong = statusId.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        long count = ordersBySupplier.stream()
                .filter(order -> statusIdsLong.contains(order.getOrderStatus().getId()))
                .count();
        System.out.println(count);
        return SupplierDto.builder()
                .successfulOrderCount(count)
                .build();
    }


    public SupplierDto OrderTotalPriceBySupplier(long accountID , List<String> statusId,
                                                 Date startDate , Date endDate ){
        Account account = accountRepository.findById(accountID).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        List<Long> statusIdsLong = statusId.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        List<Order> ordersBySupplier= orderRepository.findOrdersByAccountIdAndStatusRoleOne(account.getId(), startDate,endDate);
        List<Order> ordersListByStatus = ordersBySupplier.stream()
                .filter(order -> statusIdsLong.contains(order.getOrderStatus().getId()))
                .collect(Collectors.toList());
        long TotalNoVoucher =0;
        for (Order order : ordersListByStatus){
            List<OrderDetail> orderDetailList = orderDetailRepository.findByIdOrder(order);
            for (OrderDetail orderDetail : orderDetailList){
                TotalNoVoucher+=  orderDetail.getTotal();
            }
        }
        return SupplierDto.builder()
                .totalPriceOrder(TotalNoVoucher)
                .build();
    }



    public Page<ProductResponse> getProductsBySupplier(
            Long supplierId,
            String keyword,
            Date startDate,
            Date endDate,
            Pageable pageable) {

        Page<Product> products = productRepository.findProductsBySupplier(
                supplierId, keyword, startDate, endDate, pageable);
        for (Product product : products.getContent()){
            List<OrderDetail> orderDetail = orderDetailRepository.findByIdProduct(product);
            for (OrderDetail orderDetailv : orderDetail){
                if(orderDetailv.getIdOrder().getOrderStatus().getRoleStatus()){
                    product.setView(orderDetailv.getQuantity());
                }else{
                    product.setView(0L);
                }
            }
        }
        return products.map(this::buildProductResponse);
    }

    private ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .thumbnail(product.getThumbnail())
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productVolume(product.getVolume())
                .productHeight(product.getHeight())
                .productLength(product.getLength())
                .quantityBr(product.getView())
                .productImageMappingResponse(product.getProductImageMappings().stream()
                        .map(imageMapping -> ProductImageMappingResponse.builder()
                                .id(imageMapping.getId())
                                .idProduct(imageMapping.getProduct().getId())
                                .image(Collections.singletonList(ProductImageResponse.builder()
                                        .id(imageMapping.getImage().getId())
                                        .fileDownloadUri(imageMapping.getImage().getFileDownloadUri())
                                        .build()))
                                .build())
                        .collect(Collectors.toList()))
                .productInventoryResponse(buildProductInventoryResponse(product.getProductInventory()))
                .productBrand(BrandResponseDto.builder()
                        .id(product.getBrand().getId())
                        .name(product.getBrand().getName())
                        .description(product.getBrand().getDescription())
                        .build())
                .productCategories(CategoriesResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .build())
                .build();
    }

    private ProductInventoryResponse buildProductInventoryResponse(ProductInventory inventory) {
        if (inventory == null) return null;

        return ProductInventoryResponse.builder()
                .id(inventory.getId())
                .quantity(inventory.getQuantity())
                .build();
    }



    @Transactional
    public OrderResponse CreateOrder(OrderRequest orderRequest, long accountId) {

        if (orderRequest.getAddressId() == null) {
            throw new InvalidInputException("Địa chỉ bị rỗng");
        }
        if (orderRequest.getWalletId() == null) {
            throw new InvalidInputException("ví bị rỗng");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(()-> new NotFoundException("không tìm thấy tài khoản"));

        AddressAccount addressAccount = addressAccountRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        WalletAccount walletAccount = walletAccountRepository.findById(orderRequest.getWalletId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        OrderStatus orderStatus = orderStatusRepository.findById(26L)
                .orElseThrow(()-> new NotFoundException("không thề tìm thấy trạng thái của order"));
        Order order = new Order();
        order.setIdAccount(account);
        order.setPaymentMethods(orderRequest.getPaymentMethod());
        order.setAddressAccount(addressAccount);
        order.setIdWallet(walletAccount);
        order.setNote(orderRequest.getNote());
        order.setTotalPrice(0L);
        order.setOrderStatus(orderStatus);

        order =  orderRepository.save(order);

        return maptoOrderResponse(order);
    }

    public void saveImageForOrder(long Orderid, MultipartFile image) {
        Optional<Order> existOrder = orderRepository.findById(Orderid);
        if (existOrder.isEmpty()) {
            throw new NotFoundException("Không tìm thấy Order với ID"+ Orderid);
        }
        Order order = existOrder.get();
        try {
            String imagePath = imageService.saveImage(image, "order");
            order.setImage(imagePath);
            if (imagePath == null || imagePath.isEmpty()) {
                throw new InvalidInputException("Không thể lưu hình ảnh, đường dẫn trả về không hợp lệ");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputException("Lỗi xảy ra khi lưu hình ảnh: " + e.getMessage());
        }
        orderRepository.save(order);
    }

    private OrderResponse maptoOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        return OrderResponse.builder()
                .id(order.getId())
                .build();
    }

    @Transactional
    public ProductResponse createProductBySupplier(CreateProductRequest createProductRequest, OrderRequest orderRequest, long authorId, long idOrder) {
        Account author = accountRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tác giả với ID: " + authorId));

        Categories category = categoriesRepository.findById(createProductRequest.getProductCategoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục với ID: " + createProductRequest.getProductCategoryId()));

        ProductBrand productBrand = productBrandRepository.findById(createProductRequest.getProductBrandId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thương hiệu với ID: " + createProductRequest.getProductBrandId()));

        StatusProduct statusProduct = statusProductRepository.findById(6L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái sản phẩm mặc định."));

        Product product = new Product();
        product.setAuthor(author);
        product.setCategory(category);
        product.setBrand(productBrand);
        product.setStatus(statusProduct);
        product.setName(createProductRequest.getProductName());
        product.setArea(createProductRequest.getProductArea());
        product.setHeight(createProductRequest.getProductHeight());
        product.setLength(createProductRequest.getProductLength());
        product.setWidth(createProductRequest.getWidth());
        product.setDiscountProduct(createProductRequest.getDiscountProduct());
        product.setWeight(createProductRequest.getProductWeight());
        product.setVolume(createProductRequest.getProductVolume());
        product.setIsSupplier(true);
        product.setView(0L);
        product.setPrice(createProductRequest.getProductPrice());
        product.setLongDescription(createProductRequest.getProductLongDescription());
        product.setShortDescription(createProductRequest.getProductShortDescription());
        product.setCreateAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());

        Inventory inventory = inventoryRepository.findById(createProductRequest.getInventoryId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy kho với ID: " + createProductRequest.getInventoryId()));

        Product savedProduct = productRepository.save(product);

        ProductInventory productInventory = new ProductInventory();
        productInventory.setProduct(savedProduct);
        productInventory.setInventory(inventory);
        productInventory.setQuantity(orderRequest.getQuantity());
        productInventoryRepository.save(productInventory);



        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy order với ID: " + idOrder));
        order.setOderAcreage(orderRequest.getOderAcreage());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setNote(orderRequest.getNote());
        order.setTotalWeight(orderRequest.getTotalWeight());
        order.setOrderCode(generateOrderCode(authorId));

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setIdProduct(savedProduct);
        orderDetail.setIdOrder(order);
        orderDetail.setPrice(savedProduct.getPrice());
        orderDetail.setQuantity((long) orderRequest.getQuantity());
        orderDetail.setTotal(savedProduct.getPrice() * orderRequest.getQuantity());

        return ReturnOnlyIdOfProduct(savedProduct);
    }


    private ProductResponse ReturnOnlyIdOfProduct(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder().
                id(product.getId())
                .build();
    }


    private String generateOrderCode(Long accountId) {
        // Sinh mã đơn hàng ngẫu nhiên theo định dạng: ORD_ddMMyyyy_accountId
        LocalDate currentDate = LocalDate.now();
        String datePart = currentDate.format(DateTimeFormatter.ofPattern("ddMMyyyy")); // ddMMyyyy

        // Sinh 4 số ngẫu nhiên
        Random random = new Random();
        int randomNumber = 1000 + random.nextInt(9000); // Tạo số ngẫu nhiên trong khoảng 1000 đến 9999

        // Tạo mã đơn hàng theo định dạng yêu cầu
        return "ORD " + datePart + "_" + randomNumber + "_" + accountId;
    }

}
