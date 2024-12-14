package com.gizmo.gizmoshop.controller.admin;

import com.gizmo.gizmoshop.dto.reponseDto.*;
import com.gizmo.gizmoshop.dto.requestDto.UpdateAccountByAdminRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.entity.SupplierInfo;
import com.gizmo.gizmoshop.service.AccountService;
import com.gizmo.gizmoshop.service.Auth.AuthService;
import com.gizmo.gizmoshop.service.OrderService;
import com.gizmo.gizmoshop.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/admin")
@CrossOrigin("*")
public class AdminAPI {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/list/account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accountResponses = authService.getAllAccountResponses(); // Gọi phương thức trong AuthService
        ResponseWrapper<List<AccountResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", accountResponses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/supplier")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<SupplierDto>>> getListSupplier(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Boolean deleted,
            @RequestParam (required = false) String keyword,
            @RequestParam(required = false) Optional<String> sort) {
        Page<SupplierDto> listSupplier = supplierService.findSupplierByDeleted(page,limit,sort,deleted,keyword); // Gọi phương thức trong AuthService
        ResponseWrapper<Page<SupplierDto>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", listSupplier);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<AccountResponse>>> findUsersByCriteria(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "deleted", required = false) Boolean available,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
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
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));

        Page<AccountResponse> accountResponses = authService.findAccountByCriteria(keyword, available, roleName, pageable);
        ResponseWrapper<Page<AccountResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Accounts fetched successfully", accountResponses);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/account/{accountId}/availability")
    @Operation(summary = "Cập nhật trạng thái deleted của tài khoản",
            description = "API này được dùng để thay đổi trạng thái deleted của tài khoản. Chỉ có ADMIN mới được phép thực hiện.",
            tags = {"Account"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Account>> updateAccountAvailability(@PathVariable Long accountId) {
        Account updatedAccount = authService.updateAccountDeleted(accountId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật trạng thái thành công", updatedAccount));
    }
    @PatchMapping("account/{accountId}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@PathVariable Long accountId) {
        authService.resetPassword(accountId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Đặt lại mật khẩu thành công", null));
    }

    @PutMapping("account/{accountId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> updateAccountByAdmin(@PathVariable Long accountId,
                                                                                 @RequestBody UpdateAccountByAdminRequest updateAccountByAdminRequest) {
        AccountResponse accountResponse = accountService.updateAccountByAdmin(accountId, updateAccountByAdminRequest);
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Account update successful", accountResponse);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/roles/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> addAccountRoles(
            @PathVariable Long accountId,
            @RequestBody List<String> roleNames) {
        System.out.println(roleNames);
        authService.addAccountRoles(accountId, roleNames);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Cập nhật quyền thành công", null));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<AccountResponse>> getAccountId(
            @PathVariable Long accountId) {
        AccountResponse accountResponse = accountService.findById(accountId);
        ResponseWrapper<AccountResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy thông tin accountId:" + accountId, accountResponse);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve-supplier/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> ApproveSupplier(
            @PathVariable("id") Long supplierId,
            @RequestParam("deleted") boolean deleted) {
        supplierService.updateSupplierDeletedStatus(supplierId, deleted);
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                HttpStatus.OK, "Đã thay đổi trạng thái hoạt động của đối tác", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order-supplier")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<OrderResponse>>> findAllOrderForSupplier(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam Optional<String> sort,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long idStatus
    ){
        Page<OrderResponse> orderResponses = supplierService.findAllOrderOfSupplierForAdmin(page, limit, sort, keyword, idStatus);
        ResponseWrapper<Page<OrderResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Tìm toàn bộ order thành công", orderResponses);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/toggle-deleted/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> ApproveSupplier(
            @PathVariable("id") Long supplierId) {
        supplierService.toggleDeletedStatus(supplierId);
        ResponseWrapper<Void> response = new ResponseWrapper<>(
                HttpStatus.OK, "Đã thay đổi trạng thái hoạt động của đối tác", null);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve-order/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> approveOrderByAdmin(
            @PathVariable("orderId") Long orderId,
            @RequestParam Boolean accept,
            @RequestParam(required = false) List<Long> idProducts) {

        try {
            // Gọi service để xử lý chấp nhận hoặc từ chối đơn hàng
            supplierService.ApproveOrderByAdmin(orderId, accept, idProducts);

            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.OK, accept ? "Đơn hàng đã được chấp nhận." : "Đơn hàng đã bị từ chối.", null);
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng hoặc trạng thái.", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Xảy ra lỗi khi xử lý đơn hàng.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/approve-order-final/{orderId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Void>> approveOrderFinal(
            @PathVariable("orderId") Long orderId,
            @RequestParam Boolean accept) {
        try {
            supplierService.ApproveOrderByAdminFinal(orderId, accept);
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.OK,
                    accept ? "Đơn hàng đã được phê duyệt." : "Đơn hàng đã bị từ chối.",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (NotFoundException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.NOT_FOUND, ex.getMessage(), null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (IllegalArgumentException ex) {
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ResponseWrapper<Void> response = new ResponseWrapper<>(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Xảy ra lỗi không xác định.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/supplier/orders")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ResponseWrapper<Page<OrderSupplierSummaryDTO>>> getAllOrdersForSupplier(
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
            @RequestParam(defaultValue = "5") int limit, // Số lượng đơn hàng mỗi trang (mặc định là 5)
            @RequestParam(required = false) Optional<String> sort
            ) {

        Page<OrderSupplierSummaryDTO> listSupplier = supplierService.getAllOrdersBySupplier(page, limit, sort);

        // Đóng gói kết quả vào ResponseWrapper
        ResponseWrapper<Page<OrderSupplierSummaryDTO>> response = new ResponseWrapper<>(HttpStatus.OK, "Orders fetched successfully", listSupplier);

        return ResponseEntity.ok(response);
    }

    //lấy all đối tác
    @GetMapping("/supplier-all-ac")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<SupplierDto>>> getAllSupplierActive(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Optional<String> sort) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        Page<SupplierDto> supplierDto = supplierService.findAllSupplierActive(keyword, pageable);
        ResponseWrapper<Page<SupplierDto>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách đối tác thành công", supplierDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier-products-all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<Page<ProductResponse>>> getAllProductBySupplier(
            @RequestParam(value = "accountId", required = true) long accountId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) Optional<String> sort) {
        String sortField = "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (sort.isPresent()) {
            String[] sortParams = sort.get().split(",");
            sortField = sortParams[0];
            if (sortParams.length > 1) {
                sortDirection = Sort.Direction.fromString(sortParams[1]);
            }
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by(new Sort.Order(sortDirection, sortField)));
        Page<ProductResponse> products = supplierService.getAllProductBySupplier(keyword, accountId,startDate,endDate,pageable);
        ResponseWrapper<Page<ProductResponse>> response = new ResponseWrapper<>(HttpStatus.OK, "Lấy danh sách sản phẩm của đối tác thành công !", products);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/revenue-statistics-suppler-by-id-account")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<SupplierDto>> OrderTotalPriceBySupplier(
            @RequestParam(value = "statusId", required = false) List<String> statusId,
            @RequestParam(value = "accountId", required = true) long accountId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        if (statusId == null || statusId.isEmpty()) {
            statusId = List.of("13");
        }
        SupplierDto count = supplierService.getStatisByDate(accountId, startDate, endDate, statusId);
        return ResponseEntity.ok(new ResponseWrapper<>(HttpStatus.OK, "Lấy số doanh thu của đối tác thành công", count));
    }

    @GetMapping("/product-statics-supplier")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseWrapper<StaticsSupplierResponse>> OrderTotalPriceBySupplier(
            @RequestParam long productId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        StaticsSupplierResponse staticsSupplierResponse = supplierService.calculateProductStatistics(productId,startDate,endDate);
        ResponseWrapper<StaticsSupplierResponse> response = new ResponseWrapper<>(HttpStatus.OK, "Thành công", staticsSupplierResponse);
        return ResponseEntity.ok(response);
    }

    //phân tán đơn hàng


}
