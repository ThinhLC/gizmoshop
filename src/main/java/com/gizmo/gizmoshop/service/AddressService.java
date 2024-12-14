package com.gizmo.gizmoshop.service;

import com.gizmo.gizmoshop.dto.reponseDto.AddressAccountResponse;
import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.entity.AddressAccount;
import com.gizmo.gizmoshop.exception.InvalidInputException;
import com.gizmo.gizmoshop.repository.AccountRepository;
import com.gizmo.gizmoshop.repository.AddressAccountRepository;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressAccountRepository addressAccountRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<AddressAccountResponse> getAllAddressesByAccountId(UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        List<AddressAccount> addresses = addressAccountRepository.findByAccountIdAndDeletedFalse(accountId);
        return addresses.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AddressAccountResponse createAddress(AddressAccountResponse newAddress, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account không tồn tại"));

        AddressAccount address = new AddressAccount();
        address.setFullname(newAddress.getFullname());
        address.setSpecific_address(newAddress.getSpecificAddress());
        address.setSdt(newAddress.getSdt());
        address.setCity(newAddress.getCity());
        if(!newAddress.getCity().equals("Đà Nẵng")){
            newAddress.setDistrict(newAddress.getDistrict()+"(khác)");
        }
        address.setDistrict(newAddress.getDistrict());
        address.setCommune(newAddress.getCommune());
        address.setLongitude(newAddress.getLongitude());
        address.setLatitude(newAddress.getLatitude());
        address.setDeleted(false);
        address.setAccount(account);  // Gán đúng tài khoản đang đăng nhập

        addressAccountRepository.save(address);

        return convertToDto(address);
    }

    // Cập nhật địa chỉ của tài khoản
    public AddressAccountResponse updateAddress(Long addressId, AddressAccountResponse updatedAddress, Long accountId) {
        AddressAccount address = addressAccountRepository.findById(addressId)
                .orElseThrow(() -> new InvalidInputException("Địa chỉ không tồn tại"));

        if (!address.getAccount().getId().equals(accountId)) {
            throw new InvalidInputException("Không có quyền sửa địa chỉ này");
        }

        // Cập nhật thông tin địa chỉ
        address.setFullname(updatedAddress.getFullname());
        address.setSpecific_address(updatedAddress.getSpecificAddress());
        address.setSdt(updatedAddress.getSdt());
        address.setCity(updatedAddress.getCity());
        if(!updatedAddress.getCity().equals("Đà Nẵng")){
            updatedAddress.setDistrict(updatedAddress.getDistrict()+"(khác)");
        }
        address.setDistrict(updatedAddress.getDistrict());
        address.setCommune(updatedAddress.getCommune());
        address.setLongitude(updatedAddress.getLongitude());
        address.setLatitude(updatedAddress.getLatitude());
        addressAccountRepository.save(address);
        return convertToDto(address);
    }

    // Xóa địa chỉ của tài khoản
    public void deleteAddress(Long addressId, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();

        AddressAccount address = addressAccountRepository.findById(addressId)
                .orElseThrow(() -> new InvalidInputException("Địa chỉ không tồn tại"));

        if (!address.getAccount().getId().equals(accountId)) {
            throw new InvalidInputException("Không có quyền xóa địa chỉ này");
        }
        addressAccountRepository.deleteById(addressId);
    }

    private AddressAccountResponse convertToDto(AddressAccount address) {
        return new AddressAccountResponse(
                address.getId(),
                address.getFullname(),
                address.getSpecific_address(),
                address.getSdt(),
                address.getCity(),
                address.getDistrict(),
                address.getCommune(),
                address.getLongitude(),
                address.getLatitude(),
                address.getDeleted()
        );
    }
    public void setDeletedAddress (Long addressId, UserPrincipal userPrincipal) {
        Long accountId = userPrincipal.getUserId();
        AddressAccount addressAccount = addressAccountRepository.findById(addressId)
                .orElseThrow(() -> new InvalidInputException("Không tìm thấy địa chỉ"));
        if (!addressAccount.getAccount().getId().equals(accountId)) {
            throw new InvalidInputException("Bạn không đủ quyền với địa chỉ này");
        }
        addressAccount.setDeleted(true);
        addressAccountRepository.save(addressAccount);
    }
}
