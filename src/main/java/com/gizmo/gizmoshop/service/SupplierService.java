package com.gizmo.gizmoshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.SupplierRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.InvalidTokenException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import com.gizmo.gizmoshop.utils.ConvertEntityToResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    @Autowired
    private ProductImageMappingRepository productImageMappingRepository;

    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private WithdrawalHistoryService withdrawalHistoryService;

    ConvertEntityToResponse convertEntityToResponse = new ConvertEntityToResponse();


    public Page<SupplierDto> findSupplierByDeleted(int page, int limit, Optional<String> sort, Boolean deleted, String keyword) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.ASC;

        if (deleted == null) {
            throw new InvalidInputException("Trường 'deleted' không được để trống.");
        }

        String keywordTrimmed = (keyword != null && !keyword.trim().isEmpty()) ? "%" + keyword.trim() + "%" : "%";

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));


        Page<SupplierInfo> listAccountResponses = suppilerInfoRepository.findByDeleted(deleted, keywordTrimmed, pageable);

        List<SupplierDto> supplierDtos = listAccountResponses.getContent().stream()
                .map(supplierInfo -> SupplierDto.builder()
                        .Id(supplierInfo.getId())
                        .nameSupplier(supplierInfo.getBusiness_name())
                        .tax_code(supplierInfo.getTaxCode())
                        .frozen_balance(supplierInfo.getFrozen_balance())
                        .description(supplierInfo.getDescription())
                        .deleted(supplierInfo.getDeleted())
                        .accountResponse(AccountResponse.builder()
                                .id(supplierInfo.getAccount().getId())
                                .fullname(supplierInfo.getAccount().getFullname())
                                .email(supplierInfo.getAccount().getEmail())
                                .birthday(supplierInfo.getAccount().getBirthday())
                                .createAt(supplierInfo.getAccount().getCreate_at())
                                .image(supplierInfo.getAccount().getImage())
                                .build())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(supplierDtos, pageable, listAccountResponses.getTotalElements());


    }

    public void SupplierRegisterBusinessNotApi(long accountId, long walletId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
        String supplierInfoJson = account.getNoteregistersupplier();//build lai
        if (supplierInfoJson == null || supplierInfoJson.isEmpty()) {
            throw new NotFoundException("Thông tin nhà cung cấp không tồn tại");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SupplierRequest supplierRequest = objectMapper.readValue(supplierInfoJson, SupplierRequest.class);
            SupplierRegister(supplierRequest, accountId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chuyển đổi thông tin nhà cung cấp", e);
        }

    }

    //đăng ký hủy tư cách nhà cung cấp //role nhà cung cấp
    public void registerCancelSupplier(long accountId, long idwallet, long idAddress) {
        Optional<SupplierInfo> supplierInfoOptional = suppilerInfoRepository.findByAccount_Id(accountId);
        if (!supplierInfoOptional.isPresent()) {
            throw new InvalidInputException("Tài khoản chưa trở thành đối tác");
        }
        SupplierInfo supplierInfo = supplierInfoOptional.get();
        Date registeredDate = supplierInfo.getCreated();
        if (registeredDate == null) {
            throw new InvalidInputException("Không tìm thấy thông tin ngày đăng ký");
        }
        LocalDateTime registeredDateTime = registeredDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(registeredDateTime, now);
        if (daysBetween < 30) {
            throw new InvalidInputException("Không thể đăng ký hủy hợp tác vì thời gian hợp tác chưa đủ 30 ngày");
        }
        WalletAccount walletAccount = walletAccountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidInputException("IDWallet không tồn tại: " + idwallet));
        AddressAccount addressAccount = addressAccountRepository.findById(accountId)
                .orElseThrow(() -> new InvalidInputException("IDAddress không tồn tại: " + idAddress));
        supplierInfo.setDescription("CANCEL_SUPPLIER_CONTRACT|" + idwallet + "|" + idAddress);
        suppilerInfoRepository.save(supplierInfo);
    }
    // API(Page) lấy ra các đơn cần xét duyệt hủy bỏ tư cách (key :  supplierInfo.setDescription like CANCEL_SUPPLIER_CONTRACT)

    // API : xét duyệt đơn hủy (role staff) nhận vào id nhà cung cấp và 2 trạng thái
    // nhân viên : từ chốt , note lý do lại trong (supplierInfo.setDescription) không xử lý nx
    // nhân viên xác nhận :  kiểm tra nhà cung cấp có đang trong quá trình giao dịch đơn hàng nào k , nếu có chuyển hết về bị hủy
    // lọc qua các sp của nhà cung cấp xem còn sl không - > nếu có  : thì tạo đơn gửi về (đơn như khách hàng) giá tính theo phí và hợp đồng ,
    // -> nếu không : không xử lý
    // kiểm tra xem có còn số dư & số dư khóa không , nếu có tạo giao dịch và xóa hết số dư hiện tại
    // cuối cùng loại bỏ role , đánh cờ deleted= true


    @Transactional
    public void SupplierRegister(SupplierRequest supplierRequest, Long AccountId) {
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(AccountId);
        if (supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản đã đăng ký trở thành đối tác");
        }

        Account account = accountRepository.findById(AccountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

        Optional<SupplierInfo> checkTaxcode = suppilerInfoRepository.findByTaxCode(supplierRequest.getTax_code());
        if (checkTaxcode.isPresent()) {
            throw new UserAlreadyExistsException("Mã số thuế của bạn đã được đăng kí");
        }

        SupplierInfo supplierInfo1 = new SupplierInfo();
        supplierInfo1.setCreated(new Date());
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

    public SupplierDto getInfo(long idAccount) {
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
    public void withdraw(long accountId, SupplierDto supplier) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        WalletAccount wallet = walletAccountRepository.findById(supplier.getWallet()).orElseThrow(
                () -> new InvalidInputException("Ví không tồn tại trong tài khoản")
        );
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId);
        if (!supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản không phải đối tác");
        }
        supplierInfo.get().setBalance(supplierInfo.get().getBalance() - supplier.getBalance());
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
    public void DepositNoApi(long accountId, long amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        List<WalletAccount> walletAccounts = walletAccountRepository.findByAccountIdAndDeletedFalse(accountId);

        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId);
        if (!supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản không phải đối tác");
        }
        System.out.println("tiền hiện tại : " + supplierInfo.get().getBalance());
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


    public SupplierDto OrderCountBySupplier(long accountID, List<String> statusId) {
        Account account = accountRepository.findById(accountID).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        List<Order> ordersBySupplier = orderRepository.findOrdersByAccountIdAndStatusRoleOne(account.getId());
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


    public SupplierDto OrderTotalPriceBySupplier(long accountID, List<String> statusId,
                                                 Date startDate, Date endDate) {
        Account account = accountRepository.findById(accountID).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime startLocalDateTime = startLocalDate.atStartOfDay(); // Set to 00:00 AM

        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime endLocalDateTime = endLocalDate.atTime(23, 59, 59, 999999999); // Set to 23:59:59.999

        // Convert LocalDateTime to Date
        Date startOfDay = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());


        List<Long> statusIdsLong = statusId.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Order> ordersBySupplier = orderRepository.findOrdersByAccountIdAndStatusRoleFalse(startOfDay, endOfDay);
        List<Order> ordersListByStatus = ordersBySupplier.stream()
                .filter(order -> statusIdsLong.contains(order.getOrderStatus().getId()))
                .collect(Collectors.toList());

        long TotalNoVoucher = 0;

        for (Order order : ordersListByStatus) {
            List<OrderDetail> orderDetailList = orderDetailRepository.findByIdOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getIdProduct().getAuthor().getId() == accountID) {
                    double price = orderDetail.getIdProduct().getPrice();
                    long quantity = orderDetail.getQuantity();
                    double discount = orderDetail.getIdProduct().getDiscountProduct() / 100.0;
                    TotalNoVoucher += price * quantity * (1 - discount);
                }
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
        for (Product product : products.getContent()) {
            List<OrderDetail> orderDetail = orderDetailRepository.findByIdProduct(product);
            for (OrderDetail orderDetailv : orderDetail) {
                if (orderDetailv.getIdOrder().getOrderStatus().getRoleStatus()) {
                    product.setView(orderDetailv.getQuantity());
                } else {
                    product.setView(0L);
                }
            }
        }
        return products.map(this::buildProductResponse);
    }

    private ProductResponse buildProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .soldProduct(product.getView() - product.getProductInventory().getQuantity())
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
                .orElseThrow(() -> new NotFoundException("không tìm thấy tài khoản"));

        AddressAccount addressAccount = addressAccountRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        WalletAccount walletAccount = walletAccountRepository.findById(orderRequest.getWalletId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        OrderStatus orderStatus = orderStatusRepository.findById(26L)
                .orElseThrow(() -> new NotFoundException("không thề tìm thấy trạng thái của order"));

        System.err.println("Tổng cân nặng đơn hàng" + orderRequest.getTotalWeight());
        Order order = new Order();
        order.setIdAccount(account);
        order.setPaymentMethods(orderRequest.getPaymentMethod());
        order.setAddressAccount(addressAccount);
        order.setIdWallet(walletAccount);
        order.setNote(orderRequest.getNote());
        order.setOrderCode(generateOrderCode(accountId));
        order.setTotalPrice(0L);
        order.setOrderStatus(orderStatus);

        order.setCreateOderTime(new Date());
        order.setImage(orderRequest.getImgOrder());
        order.setTotalWeight(orderRequest.getTotalWeight());
        order.setOderAcreage(orderRequest.getOderAcreage());
        order = orderRepository.save(order);

        Contract contract = new Contract();
        contract.setOrder(order);
        contract.setStartDate(LocalDateTime.now());
        contract.setExpireDate(LocalDateTime.now().plusDays(orderRequest.getContractDate()));
        contract.setContractMaintenanceFee(orderRequest.getContractMaintenanceFee());
        System.err.println("tg/gui" + orderRequest.getContractDate());
        System.err.println("phí duy tri" + orderRequest.getContractMaintenanceFee());
        contractRepository.save(contract);

//        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId)
//                .orElseThrow(()-> new NotFoundException("Không tìm thấy tài khoản của người dùng"));
//
//        System.out.println(orderRequest.getContractMaintenanceFee());
//
//        long contractMaintenanceFee = supplierInfo.getBalance() - orderRequest.getContractMaintenanceFee();
//
//        System.out.println(contractMaintenanceFee);
//        if (contractMaintenanceFee < 0) {
//            throw new InvalidInputException("Tài khoản của quý khách không đủ số dư để thực hiện giao dịch");
//        }
//
//        supplierInfo.setBalance(contractMaintenanceFee);
//        suppilerInfoRepository.save(supplierInfo);

//        WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
//        withdrawalHistory.setAccount(account);
//        withdrawalHistory.setWalletAccount(walletAccount);
//        withdrawalHistory.setWithdrawalDate(new Date());
//        withdrawalHistory.setNote("SUPPLIER|Chuyển tiền duy trì của đơn hàng trong "+ orderRequest.getContractDate()+"của đơn hàng" +order.getOrderCode()+"|COMPETED");
//        withdrawalHistory.setAmount(orderRequest.getContractMaintenanceFee);
//        withdrawalHistoryRepository.save(withdrawalHistory);

        return maptoOrderResponse(order);
    }

    public void saveImageForOrder(long Orderid, MultipartFile image) {
        Optional<Order> existOrder = orderRepository.findById(Orderid);
        if (existOrder.isEmpty()) {
            throw new NotFoundException("Không tìm thấy Order với ID" + Orderid);
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
        productInventory.setQuantity(createProductRequest.getQuantity());
        productInventoryRepository.save(productInventory);


        Order order = orderRepository.findAndLockOrderById(idOrder);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setIdProduct(savedProduct);
        orderDetail.setIdOrder(order);

        double discountAmount = Math.round(savedProduct.getPrice() * (savedProduct.getDiscountProduct() / 100.0));
        System.out.println(discountAmount);
        long finalPrice = Math.round(savedProduct.getPrice() - discountAmount);
        System.out.println(finalPrice);
        orderDetail.setPrice(finalPrice);
        orderDetail.setQuantity((long) createProductRequest.getQuantity());

        orderDetail.setTotal(finalPrice * createProductRequest.getQuantity());

        order.setTotalPrice(order.getTotalPrice() + orderDetail.getTotal());
        orderRepository.save(order);
        orderDetailRepository.save(orderDetail);

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
        return "ORD_" + datePart + "_" + randomNumber + "_" + accountId;
    }

    @Transactional
    public void UpdateOrderBySupplier(OrderRequest orderRequest, long orderId, long accountId) {
        if (orderRequest.getAddressId() == null) {
            throw new InvalidInputException("Địa chỉ bị rỗng");
        }
        if (orderRequest.getWalletId() == null) {
            throw new InvalidInputException("ví bị rỗng");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("không tìm thấy tài khoản"));

        AddressAccount addressAccount = addressAccountRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        WalletAccount walletAccount = walletAccountRepository.findById(orderRequest.getWalletId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        OrderStatus orderStatus = orderStatusRepository.findById(26L)
                .orElseThrow(() -> new NotFoundException("không thề tìm thấy trạng thái của order"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("không tìm thấy đơn hàng"));

        if (!order.getIdAccount().getId().equals(accountId)) {
            throw new InvalidTokenException("Bạn không có quyền chỉnh sửa đơn hàng này");
        }

        order.setIdAccount(account);
        order.setPaymentMethods(orderRequest.getPaymentMethod());
        order.setAddressAccount(addressAccount);
        order.setIdWallet(walletAccount);
        order.setNote(orderRequest.getNote());
        order.setOrderCode(generateOrderCode(accountId));
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setOrderStatus(orderStatus);
        order.setImage(orderRequest.getImgOrder());
        order.setTotalWeight(orderRequest.getTotalWeight());
        order.setOderAcreage(orderRequest.getOderAcreage());
        order.setCreateOderTime(new Date());
        order = orderRepository.save(order);

        Contract contract = new Contract();
        contract.setOrder(order);
        contract.setStartDate(LocalDateTime.now());
        contract.setExpireDate(LocalDateTime.now().plusDays(orderRequest.getContractDate()));
        contract.setContractMaintenanceFee(orderRequest.getContractMaintenanceFee());
        contractRepository.save(contract);
    }

    public Page<OrderResponse> findAllOrderForSupplier(int page, int limit, Optional<String> sort, String keyword, Long idStatus, long accountId) {
        String sortField = "id"; // Mặc định là 'id'
        Sort.Direction sortDirection = Sort.Direction.DESC;
        String keywordTrimmed = (keyword != null) ? keyword.trim() : null;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }

        Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortField));
        Page<Order> orders = orderRepository.findAllOrderForSupplier(idStatus, keywordTrimmed, accountId, pageable);
        return orders.map(this::convertToOrderResponse);
    }

    public Page<OrderResponse> findAllOrderOfSupplierForAdmin(int page, int limit, Optional<String> sort, String keyword, Long idStatus) {
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
        Page<Order> orders = orderRepository.findAllOrderOfSupplierForAdmin(idStatus, keywordTrimmed, pageable);
        return orders.map(this::convertToOrderResponse);
    }

    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderDetail> orderDetailsList = orderDetailRepository.findByIdOrder(order);

        ContractResponse contractResponse = null;
        if (order.getContract() != null) {
            contractResponse = ContractResponse.builder()
                    .contractId(order.getContract().getId())
                    .notes(order.getContract().getNotes())
                    .contractMaintenanceFee(order.getContract().getContractMaintenanceFee())
                    .start_date(order.getContract().getStartDate())
                    .expirationDate(order.getContract().getExpireDate())
                    .build();
        }

        return OrderResponse.builder()
                .id(order.getId())
                .paymentMethods(order.getPaymentMethods())
                .account(AccountResponse.builder()
                        .id(order.getIdAccount().getId())
                        .fullname(order.getIdAccount().getFullname())
                        .build())
                .addressAccount(AddressAccountResponse.builder()
                        .fullname(order.getAddressAccount().getFullname())
                        .city(order.getAddressAccount().getCity())
                        .commune(order.getAddressAccount().getCommune())
                        .district(order.getAddressAccount().getDistrict())
                        .specificAddress(order.getAddressAccount().getSpecific_address())
                        .sdt(order.getAddressAccount().getSdt())
                        .build())
                .orderStatus(OrderStatusResponse.builder()
                        .id(order.getOrderStatus().getId())
                        .status(order.getOrderStatus().getStatus())
                        .roleStatus(order.getOrderStatus().getRoleStatus())
                        .build())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .totalWeight(order.getTotalWeight())
                .orderCode(order.getOrderCode())
                .createOderTime(order.getCreateOderTime())
                .orderDetails(orderDetailsList.stream().map(orderDetail -> OrderDetailsResponse.builder()
                        .id(orderDetail.getId())
                        .price(orderDetail.getPrice())
                        .quantity(orderDetail.getQuantity())
                        .accept(orderDetail.getAccept())
                        .total(orderDetail.getPrice() * orderDetail.getQuantity())
                        .product(ProductResponse.builder()
                                .id(orderDetail.getIdProduct().getId())
                                .discountProduct(orderDetail.getIdProduct().getDiscountProduct())
                                .productName(orderDetail.getIdProduct().getName())
                                .productImageMappingResponse(orderDetail.getIdProduct().getProductImageMappings().stream()
                                        .map(imageMapping -> new ProductImageMappingResponse(imageMapping)) // Chuyển từ ProductImageMapping sang ProductImageMappingResponse
                                        .collect(Collectors.toList()))// Thu thập thành List
                                .productPrice(orderDetail.getIdProduct().getPrice())
                                .thumbnail(orderDetail.getIdProduct().getThumbnail())
                                .productLongDescription(orderDetail.getIdProduct().getLongDescription())
                                .productShortDescription(orderDetail.getIdProduct().getShortDescription())
                                .productWeight(orderDetail.getIdProduct().getWeight())
                                .productArea(orderDetail.getIdProduct().getArea())
                                .productVolume(orderDetail.getIdProduct().getVolume())
                                .productHeight(orderDetail.getIdProduct().getHeight())
                                .productLength(orderDetail.getIdProduct().getLength())
                                .productStatusResponse(ProductStatusResponse.builder()
                                        .name(orderDetail.getIdProduct().getStatus().getName())
                                        .id(orderDetail.getIdProduct().getStatus().getId())
                                        .build())
                                .productInventoryResponse(ProductInventoryResponse.builder()
                                        .quantity(orderDetail.getIdProduct().getProductInventory().getQuantity())
                                        .build())
                                .build())
                        .build()).collect(Collectors.toList()))
                .contractresponse(contractResponse)
                .build();
    }

    public void toggleDeletedStatus(Long supplierId) {
        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(supplierId)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy Supplier với ID: " + supplierId));
        supplierInfo.setDeleted(supplierInfo.getDeleted() == null || !supplierInfo.getDeleted());
        if (supplierInfo.getDeleted()) {
            if (supplierInfo.getFrozen_balance() > 50000L) {
                Long amountToTransfer = supplierInfo.getFrozen_balance() - 50000L;
                supplierInfo.setFrozen_balance(50000L);
                supplierInfo.setBalance(supplierInfo.getBalance() + amountToTransfer);
            }
        }
        suppilerInfoRepository.save(supplierInfo);
    }


    @Transactional
    public void ApproveOrderByAdmin(Long orderId, Boolean accept, List<Long> idProducts) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng"));

        List<OrderDetail> orderDetailList = orderDetailRepository.findByIdOrder(order);

        StatusProduct statusProductReject = statusProductRepository.findById(3L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái sản phẩm 3"));

        if (!accept) {
            // Chuyển trạng thái đơn hàng sang 28 (từ chối)
            OrderStatus orderStatusReject = orderStatusRepository.findById(28L)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 28"));

            order.setOrderStatus(orderStatusReject);

            for (OrderDetail orderDetailItem : orderDetailList) {
                Product product = orderDetailItem.getIdProduct();
                product.setStatus(statusProductReject);
                productRepository.save(product);
            }

            orderRepository.save(order);
            return;
        }

        if (idProducts == null || idProducts.isEmpty()) {
            // Nếu không có sản phẩm nào trong danh sách idProducts, chỉ cập nhật trạng thái của đơn hàng
            OrderStatus orderStatusApprove = orderStatusRepository.findById(9L)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 9"));
            order.setOrderStatus(orderStatusApprove);
            orderRepository.save(order);
            return; // Kết thúc nếu đơn hàng được chấp nhận mà không thay đổi sản phẩm
        }
        // Tính tổng giá trị cần trừ đi, tổng diện tích và cân nặng
        long totalPriceToSubtract = 0L;
        float totalAcreageToSubtract = 0;
        float totalWeightToSubtract = 0;


        for (OrderDetail orderDetailItem : orderDetailList) {
            Product product = orderDetailItem.getIdProduct();

            // Nếu sản phẩm nằm trong danh sách idProducts
            if (idProducts.contains(product.getId())) {
                // Lấy trạng thái sản phẩm 3 (bị từ chối)

                product.setStatus(statusProductReject);

                // Tính tổng giá trị, diện tích và cân nặng cần trừ
                totalPriceToSubtract += orderDetailItem.getTotal();
                totalAcreageToSubtract += product.getArea() * orderDetailItem.getQuantity();
                totalWeightToSubtract += product.getWeight() * orderDetailItem.getQuantity();

                productRepository.save(product);
            }
        }
        System.err.println("tổng diện tích trước khi trừ" + order.getOderAcreage());
        System.err.println("Tổng giá trị đơn hàng trước khi lưu" + order.getTotalPrice());
        // Cập nhật tổng giá trị, diện tích và cân nặng mới cho đơn hàng
        order.setTotalPrice(order.getTotalPrice() - totalPriceToSubtract);

        order.setOderAcreage(order.getOderAcreage() - totalAcreageToSubtract);
        order.setTotalWeight(order.getTotalWeight() - totalWeightToSubtract);

        System.err.println("Tổng ");
        // Cập nhật trạng thái đơn hàng là 9 (chấp nhận)
        OrderStatus orderStatusApprove = orderStatusRepository.findById(9L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 9"));
        order.setOrderStatus(orderStatusApprove);

        orderRepository.save(order);

        System.err.println("Tổng diện sau trước khi lưu" + order.getOderAcreage());

        // Lấy danh sách hợp đồng liên quan
        Contract contracts = contractRepository.findByOrderId(orderId);


        // Tính số ngày giữa startDate và expireDate
        long daysBetween = ChronoUnit.DAYS.between(contracts.getStartDate(), contracts.getExpireDate());

        System.err.println("Tổng diện tích trước khi lưu" + order.getOderAcreage());
        // Tính toán phí bảo trì
        float acreage = order.getOderAcreage() / 10000;
        System.err.println("acreage biến đổi thành m2" + acreage);
        System.err.println("Tổng diện tích sau khi lưu" + order.getOderAcreage());
        System.err.println("Tổng phí duy trì trước khi lưu" + contracts.getContractMaintenanceFee());
        long maintenanceFee = Math.round((acreage * 200000 * daysBetween) / 30);

        // Cập nhật phí bảo trì
        contracts.setContractMaintenanceFee(maintenanceFee);

        contractRepository.save(contracts);

        System.err.println("Tổng phí duy trì sau khi lưu" + contracts.getContractMaintenanceFee());
    }


    @Transactional
    public void ApproveOrderBySupplier(Long orderId, Boolean accept, Long accountId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy dơnd hàng"));

        if (!order.getOrderStatus().getId().equals(9L)) {
            throw new IllegalArgumentException("Đơn hàng phải có trạng thái là 9 để có thể duyệt.");
        }

        List<OrderDetail> orderDetailList = orderDetailRepository.findByIdOrder(order);


        StatusProduct statusProductReject = statusProductRepository.findById(3L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái sản phẩm 3"));

        OrderStatus orderStatusApprove = orderStatusRepository.findById(18L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 6"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("không tFìm thấy tài khoản"));

        WalletAccount walletAccount = walletAccountRepository.findById(order.getIdWallet().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        if (!accept) {
            OrderStatus orderStatusReject = orderStatusRepository.findById(19L)
                    .orElseThrow((() -> new NotFoundException("Không tìm thấy trạng thái hoat động số 19")));
            order.setOrderStatus(orderStatusReject);

            for (OrderDetail orderDetailItem : orderDetailList) {
                Product product = orderDetailItem.getIdProduct();
                product.setStatus(statusProductReject);
                productRepository.save(product);
            }

            orderRepository.save(order);
            return;
        }

        order.setOrderStatus(orderStatusApprove);
        orderRepository.save(order);

        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản của người dùng"));

        Contract contract = contractRepository.findByOrderId(orderId);


        if (contract == null) {
            throw new NotFoundException("không tìm thấy bản hợp đồng");
        }
        if (supplierInfo.getBalance() - contract.getContractMaintenanceFee() < 0L) {
            throw new InvalidInputException("Tài khoản của quý khách không đủ, vui lòng nạp thêm tìm");
        } else {
            supplierInfo.setBalance(supplierInfo.getBalance() - contract.getContractMaintenanceFee());
            suppilerInfoRepository.save(supplierInfo);
        }

        long daysBetween = ChronoUnit.DAYS.between(contract.getStartDate(), contract.getExpireDate());

        WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
        withdrawalHistory.setAccount(account);
        withdrawalHistory.setWalletAccount(walletAccount);
        withdrawalHistory.setWithdrawalDate(new Date());
        withdrawalHistory.setNote("SUPPLIER|Chuyển tiền duy trì của đơn hàng trong " + daysBetween + " của đơn hàng" + order.getOrderCode() + "|COMPETED");
        withdrawalHistory.setAmount(contract.getContractMaintenanceFee());
        withdrawalHistoryRepository.save(withdrawalHistory);

        System.out.println("Thay đổi toàn bộ trạng thái sản phẩm thành công");
    }

    @Transactional
    public void ApproveOrderByAdminFinal(Long orderId, Boolean accept) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy dơnd hàng"));

        if (!order.getOrderStatus().getId().equals(20L)) {
            throw new IllegalArgumentException("Đơn hàng phải có trạng thái là 20 để có thể duyệt.");
        }

        OrderStatus orderStatusReject = orderStatusRepository.findById(28L)
                .orElseThrow((() -> new NotFoundException("Không tìm thấy trạng thái hoat động số 28")));

        OrderStatus orderStatusApprove = orderStatusRepository.findById(10L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 6"));

        Account account = accountRepository.findById(order.getIdAccount().getId())
                .orElseThrow(() -> new NotFoundException("không tìm thấy tài khoản"));

        WalletAccount walletAccount = walletAccountRepository.findById(order.getIdWallet().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy địa chỉ"));

        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(order.getIdAccount().getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy tài khoản của người dùng"));

        List<Product> products = productRepository.findAllProductsByStatusAndOrder(orderId);

        StatusProduct statusProductApprove = statusProductRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy mã trạng thái 1"));

        Contract contract = contractRepository.findByOrderId(orderId);

        if (contract == null) {
            throw new NotFoundException("không tìm thấy bản hợp đồng");
        }

        if (!accept) {
            order.setOrderStatus(orderStatusReject);


            WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
            withdrawalHistory.setAccount(account);
            withdrawalHistory.setWalletAccount(walletAccount);
            withdrawalHistory.setWithdrawalDate(new Date());
            withdrawalHistory.setNote("SUPPLIER|Hoàn tiền của đơn hàng " + order.getOrderCode() + " cho nhà cung cấp" + "|COMPETED");
            withdrawalHistory.setAmount(contract.getContractMaintenanceFee());

            supplierInfo.setBalance(supplierInfo.getBalance() + contract.getContractMaintenanceFee());

            orderRepository.save(order);
            withdrawalHistoryRepository.save(withdrawalHistory);
            suppilerInfoRepository.save(supplierInfo);
            return;
        } else {
            System.err.println("line23");
            for (Product product : products) {
                if (product.getStatus() != null) {
                    product.setStatus(statusProductApprove);
                }
                productRepository.save(product);
                System.err.println("line24");
            }
            order.setOrderStatus(orderStatusApprove);
        }

        orderRepository.save(order);
    }

    @Transactional
    public void ApproveOrderByShipper(Long orderId, Boolean accept) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy dơnd hàng"));

        OrderStatus orderStatusReject = orderStatusRepository.findById(28L)
                .orElseThrow((() -> new NotFoundException("Không tìm thấy trạng thái hoat động số 28")));

        OrderStatus orderStatusApprove = orderStatusRepository.findById(10L)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy trạng thái hoạt động số 6"));

        Account account = accountRepository.findById(order.getIdAccount().getId())
                .orElseThrow(() -> new NotFoundException("không tìm thấy tài khoản"));

        if (!order.getOrderStatus().getId().equals(18L)) {
            throw new IllegalArgumentException("Đơn hàng phải có trạng thái là 18 để có thể duyệt.");
        }

        order.setOrderStatus(orderStatusApprove);
        orderRepository.save(order);
    }


    public Page<SupplierDto> getCancelSupplierRequests(Pageable pageable) {
        Page<SupplierInfo> suppliers = suppilerInfoRepository.findByDescriptionContaining("CANCEL_SUPPLIER_CONTRACT", pageable);

        return suppliers.map(this::convertToSupplierDto);
    }

    private SupplierDto convertToSupplierDto(SupplierInfo supplierInfo) {
        return SupplierDto.builder()
                .Id(supplierInfo.getId())
                .nameSupplier(supplierInfo.getBusiness_name())
                .tax_code(supplierInfo.getTaxCode())
                .balance(supplierInfo.getBalance())
                .frozen_balance(supplierInfo.getFrozen_balance())
                .description(supplierInfo.getDescription())
                .deleted(supplierInfo.getDeleted())
                .accountResponse(
                        AccountResponse.builder()
                                .fullname(supplierInfo.getAccount().getFullname())
                                .id(supplierInfo.getAccount().getId())
                                .build()
                )
                .build();
    }

    @Transactional
    public void AcceptCancelSupplier(Long accountId) {
        // Bước 1: Tạo Order
        SupplierInfo supplierInfo = suppilerInfoRepository.findByAccount_Id(accountId)
                .orElseThrow(() -> new InvalidInputException("Supplier not found"));
        String description = supplierInfo.getDescription();
        if (description == null || !description.contains("|")) {
            throw new InvalidInputException("Không tìm thấy thông tin hợp lệ trong description của SupplierInfo");
        }

        String[] parts = description.split("\\|");
        if (parts.length < 3 || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new InvalidInputException("Description không hợp lệ hoặc thiếu ID Wallet/Address");
        }

        String idWallet = parts[1].trim();
        String idAddress = parts[2].trim();
        WalletAccount walletAccount = walletAccountRepository.findById(Long.valueOf(idWallet))
                .orElseThrow(() -> new InvalidInputException("ID Wallet không tồn tại: " + idWallet));

        AddressAccount addressAccount = addressAccountRepository.findById(Long.valueOf(idAddress))
                .orElseThrow(() -> new InvalidInputException("ID Address không tồn tại: " + idAddress));

        Order order = new Order();
        order.setIdAccount(supplierInfo.getAccount());
        order.setOrderStatus(orderStatusRepository.findById(1L)
                .orElseThrow(() -> new InvalidInputException("Order Status not found")));
        order.setTotalPrice(30000L);
        order.setNote("Lần cuối: Đơn hàng của các sản phẩm còn dư đang được chuyển lại cho nhà cung cấp");
        order.setCreateOderTime(new Date());
        order.setOrderCode(generateOrderCode(accountId));
        order.setIdWallet(walletAccount);
        order.setAddressAccount(addressAccount);
        order.setPaymentMethods(true);
        orderRepository.save(order); // Lưu Order

        // Bước 2: Tạo OrderDetail
        List<Product> productsInTransaction = productRepository.findByAuthorId(accountId);
        if (!productsInTransaction.isEmpty()) {
            for (Product product : productsInTransaction) {
                ProductInventory inventoryProduct = productInventoryRepository.findByProductId(product.getId())
                        .orElseThrow(() -> new InvalidInputException("InventoryProduct not found for Product ID: " + product.getId()));

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setIdProduct(product);
                orderDetail.setIdOrder(order);
                orderDetail.setPrice(product.getPrice());
                orderDetail.setQuantity((long) inventoryProduct.getQuantity());
                orderDetail.setTotal(product.getPrice() * inventoryProduct.getQuantity());
                orderDetailRepository.save(orderDetail);

                // Cập nhật trạng thái sản phẩm và tồn kho
                StatusProduct status = statusProductRepository.findById(2L)
                        .orElseThrow(() -> new InvalidInputException("Status not found with ID: 2"));
                product.setStatus(status);
                inventoryProduct.setQuantity(0);
                productRepository.save(product);
                productInventoryRepository.save(inventoryProduct);
            }
        }

        // Bước 3: Tạo WithdrawalHistory
        Long balance = supplierInfo.getBalance();
        Long frozenBalance = supplierInfo.getFrozen_balance();
        if (balance > 0 || frozenBalance > 0) {
            WithdrawalHistory withdrawalHistory = new WithdrawalHistory();
            withdrawalHistory.setAccount(supplierInfo.getAccount());
            withdrawalHistory.setWalletAccount(walletAccount);
            withdrawalHistory.setAmount(balance + frozenBalance); // Tổng tiền từ balance + frozenBalance
            withdrawalHistory.setWithdrawalDate(new Date());
            withdrawalHistory.setNote("CUSTOMER|HUY LAM NHA CUNG CAP|PENDING");
            withdrawalHistoryRepository.save(withdrawalHistory);

            // Reset balance và frozenBalance về 0
            supplierInfo.setBalance(0L);
            supplierInfo.setFrozen_balance(0L);
        }

        // Bước 4: Xóa role và cập nhật thông tin nhà cung cấp
        List<RoleAccount> supplierRoles = roleAccountRepository.findByAccount_IdAndRole_Name(accountId, "ROLE_SUPPLIER");
        if (!supplierRoles.isEmpty()) {
            roleAccountRepository.deleteAll(supplierRoles);
        }
        supplierInfo.setDeleted(true);
        supplierInfo.setDescription("Đã hủy role");
        suppilerInfoRepository.save(supplierInfo); // Lưu thông tin nhà cung cấp
    }

    public Page<OrderSupplierSummaryDTO> getAllOrdersBySupplier(Pageable pageable) {
        List<Long> statusIds = Arrays.asList(1L, 20L, 26L);
        Page<Order> ordersPage = orderRepository.findByOrderStatusIdIn(statusIds, pageable);
        Page<OrderSupplierSummaryDTO> orderSummaryResponses = ordersPage.map(order -> {
            boolean isRole5 = order.getIdAccount().getRoleAccounts().stream()
                    .anyMatch(roleAccount -> roleAccount.getRole().getId() == 5);
            return new OrderSupplierSummaryDTO(
                    order.getOrderCode(),
                    order.getIdAccount().getFullname(),
                    isRole5,
//                  order.getOrderStatus().getId(),
                    order.getCreateOderTime()
            );
        });
        return orderSummaryResponses;
    }

    private String getSupplierName(Account account) {
        if (account != null && account.getSupplierInfos() != null && !account.getSupplierInfos().isEmpty()) {
            // Trả về tên nhà cung cấp đầu tiên nếu có, hoặc một tên mặc định nếu không có
            return account.getSupplierInfos().iterator().next().getBusinessName();  // Lấy tên của nhà cung cấp từ SupplierInfo
        }
        return "Unknown Supplier";  // Nếu không có thông tin nhà cung cấp, trả về "Unknown Supplier"
    }

    public Page<SupplierDto> findAllSupplierActive(String keyword, Pageable pageable) {
        Page<Account> accounts = accountRepository.findAllBySupplier(keyword, pageable);
        return accounts.map(account -> {
            SupplierInfo s = suppilerInfoRepository.findByAccount_Id(account.getId()).orElseThrow(() -> new InvalidInputException("Supplier info notfound"));  // Assuming supplierService.findByAccount() fetches the Supplier entity
            return SupplierDto.builder()
                    .Id(s.getId())
                    .nameSupplier(s.getBusiness_name())
                    .tax_code(s.getTaxCode())
                    .balance(s.getBalance())
                    .frozen_balance(s.getFrozen_balance())
                    .description(s.getDescription())
                    .deleted(s.getDeleted())
                    .accountResponse(AccountResponse.builder()
                            .email(account.getEmail())
                            .fullname(account.getFullname())
                            .image(account.getImage())
                            .id(account.getId())
                            .build())
                    .build();
        });
    }


    public Page<ProductResponse> getAllProductBySupplier(String keyword, long accountId, Date startDate, Date endDate, Pageable pageable) {
        Page<Product> productPage = productRepository.findProductBySupplierAccount(accountId, keyword, startDate, endDate, pageable);
        return productPage.map(this::mapToProductResponse);
    }

    public SupplierDto getStatisByDate(long accountID, Date startDate, Date endDate, List<String> statusId) {
        accountRepository.findById(accountID).orElseThrow(
                () -> new InvalidInputException("Tài khoản không tồn tại")
        );
        LocalDate startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime startLocalDateTime = startLocalDate.atStartOfDay(); // Set to 00:00 AM

        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime endLocalDateTime = endLocalDate.atTime(23, 59, 59, 999999999); // Set to 23:59:59.999

        // Convert LocalDateTime to Date
        Date startOfDay = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());


        List<Long> statusIdsLong = statusId.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        List<Order> ordersBySupplier = orderRepository.findOrdersByAccountIdAndStatusRoleFalse(startOfDay, endOfDay);
        List<Order> ordersListByStatus = ordersBySupplier.stream()
                .filter(order -> statusIdsLong.contains(order.getOrderStatus().getId()))
                .collect(Collectors.toList());
        long TotalNoVoucher = 0;
        for (Order order : ordersListByStatus) {
            List<OrderDetail> orderDetailList = orderDetailRepository.findByIdOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                if (orderDetail.getIdProduct().getAuthor().getId() == accountID) {
                    double price = orderDetail.getIdProduct().getPrice();
                    long quantity = orderDetail.getQuantity();
                    double discount = orderDetail.getIdProduct().getDiscountProduct() / 100.0;
                    TotalNoVoucher += price * quantity * (1 - discount);
                }
            }
        }

        return SupplierDto.builder()
                .totalPriceOrder(TotalNoVoucher)
                .build();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getName())
                .quantityBr(product.getProductInventory().getQuantity())//hien tai
                .productPrice(product.getPrice())
                .discountProduct(product.getDiscountProduct())
                .productImageMappingResponse(getProductImageMappings(product.getId()))
                .productInventoryResponse(getProductInventoryResponse(product))
                .productLongDescription(product.getLongDescription())
                .productShortDescription(product.getShortDescription())
                .productWeight(product.getWeight())
                .productHeight(product.getHeight())
                .productLength(product.getLength())
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

    public List<ProductImageMappingResponse> getProductImageMappings(long productId) {
        List<ProductImageMapping> mappings = productImageMappingRepository.findByProductId(productId);
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyList();
        }
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

    private ProductInventoryResponse getProductInventoryResponse(Product product) {
        ProductInventory productInventory = product.getProductInventory();
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

    public StaticsSupplierResponse calculateProductStatistics(Long productId, Date startDate, Date endDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm"));

        if (!Boolean.TRUE.equals(product.getIsSupplier())) {
            throw new InvalidInputException("Sản phẩm này không thuộc nhà cung cấp.");
        }
        List<OrderDetail> orderDetailsList = orderDetailRepository.findByIdProductAndIdOrderRoleStatusTrue(productId);

        if (orderDetailsList.isEmpty()) {
            System.out.println("Không có đơn hàng nào trong khoảng thời gian này.");
        }

        long supplyQuantity = orderDetailsList.stream().mapToLong(OrderDetail::getQuantity).sum();

        System.out.println("Số lượng cung cấp (supplyQuantity): " + supplyQuantity);

        ProductInventory productInventory = productInventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sản phẩm trong kho"));

        long inventoryQuantity = productInventory != null ? productInventory.getQuantity() : 0;

        // Kiểm tra số lượng tồn kho
        System.out.println("Số lượng tồn kho (inventoryQuantity): " + inventoryQuantity);

        // Tính toán số lượng đã bán (soldQuantity) dựa trên sự chênh lệch giữa số lượng cung cấp và tồn kho
        long soldQuantity = Math.max(supplyQuantity - inventoryQuantity, 0);

        // Kiểm tra số lượng đã bán
        System.out.println("Số lượng đã bán (soldQuantity): " + soldQuantity);

        // Tạo đối tượng kết quả để trả về
        StaticsSupplierResponse res = new StaticsSupplierResponse();
        res.setQuantityBr(soldQuantity);
        res.setQuantityCC(supplyQuantity);
        res.setQuantityTK(inventoryQuantity);
        res.setStartDate(startDate);
        res.setEndDate(endDate);
        res.setProduct(mapToProductResponse(product));

        return res;
    }

}
