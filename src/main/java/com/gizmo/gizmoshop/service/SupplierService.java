package com.gizmo.gizmoshop.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gizmo.gizmoshop.dto.requestDto.CreateProductRequest;
import com.gizmo.gizmoshop.dto.requestDto.OrderRequest;
import com.gizmo.gizmoshop.dto.requestDto.SupplierRequest;
import com.gizmo.gizmoshop.entity.*;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.*;
import com.gizmo.gizmoshop.service.Image.ImageService;
import com.gizmo.gizmoshop.service.product.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService extends ProductService {
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

    @Autowired
    private AddressAccountRepository addressAccountRepository;

    @Autowired
    private WalletAccountRepository walletAccountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

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


    @Transactional
    public void CreateOrder(OrderRequest orderRequest, long accountId) {

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

        orderRepository.save(order);
    }

    public void saveImageForOrder(long id,MultipartFile image) {
        Optional<Order> existOrder = orderRepository.findById(id);
        if (existOrder.isEmpty()) {
            throw new NotFoundException("Không tìm thấy Order với ID"+ id);
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

}
