package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.requestDto.SupplierRequest;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.Role;
import com.gizmo.gizmoshop.entity.RoleAccount;
import com.gizmo.gizmoshop.entity.SupplierInfo;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.exception.NotFoundException;
import com.gizmo.gizmoshop.exception.UserAlreadyExistsException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import com.gizmo.gizmoshop.repository.RoleRepository;
import com.gizmo.gizmoshop.repository.SuppilerInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {
    @Autowired
    private SuppilerInfoRepository suppilerInfoRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleAccountRepository roleAccountRepository;

    @Transactional
    public void SupplierRegister(SupplierRequest supplierRequest, Long AccountId) {
        Optional<SupplierInfo> supplierInfo = suppilerInfoRepository.findByAccount_Id(AccountId);
        if (supplierInfo.isPresent()) {
            throw new InvalidInputException("Tài khoản đã đăng ký trở thành đối tác");
        }

        Account account = accountRepository.findById(AccountId)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));

//        Optional<SupplierInfo> checkTaxcode=  suppilerInfoRepository.findByTax_code(supplierRequest.getTax_code());
//        if (checkTaxcode.isPresent()) {
//            throw new UserAlreadyExistsException("Mã số thuế của bạn đã được đăng kí");
//        }

        SupplierInfo supplierInfo1 = new SupplierInfo();
        supplierInfo1.setAccount(account);
        supplierInfo1.setDeleted(true);
        supplierInfo1.setBusiness_name(supplierRequest.getNameSupplier());
        supplierInfo1.setDescription(supplierInfo1.getDescription());
        supplierInfo1.setTax_code(supplierRequest.getTax_code());
        supplierInfo1.setBalance(0L);
        supplierInfo1.setFrozen_balance(0L);
        suppilerInfoRepository.save(supplierInfo1);

        RoleAccount roleAccount = new RoleAccount();
        roleAccount.setAccount(account);

        Role supplierRole = new Role();
        supplierRole.setId(5L);
        roleAccount.setRole(supplierRole);

        roleAccountRepository.save(roleAccount);
    }

    public void updateSupplierDeletedStatus(Long supplierId, boolean deleted) {
        SupplierInfo supplierInfo = suppilerInfoRepository.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Supplier với ID: " + supplierId));

        supplierInfo.setDeleted(deleted);
        suppilerInfoRepository.save(supplierInfo);
    }

}
