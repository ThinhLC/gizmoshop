package com.gizmo.gizmoshop.test;


import com.gizmo.gizmoshop.repository.RoleAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional // Đảm bảo rằng mỗi test sẽ rollback sau khi thực hiện
public class RoleAccountRepositoryTest {

    @Autowired
    private RoleAccountRepository roleAccountRepository;



    @BeforeEach
    public void setUp() {
        // Khởi tạo dữ liệu cần thiết trước mỗi test

    }

    @Test
    public void testFindByAccountAndRole() {
        // Kiểm tra xem phương thức có trả về true hay không
        Boolean result = roleAccountRepository.findByAccountAndRole(19L, "ROLE_SUPPLIER");
        assertTrue(result, "Expected true as the account has the role.");
    }



}
