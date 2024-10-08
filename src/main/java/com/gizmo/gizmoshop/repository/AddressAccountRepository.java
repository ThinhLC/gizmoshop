package com.gizmo.gizmoshop.repository;

import com.gizmo.gizmoshop.entity.AddressAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressAccountRepository extends JpaRepository<AddressAccount, Long> {

}
