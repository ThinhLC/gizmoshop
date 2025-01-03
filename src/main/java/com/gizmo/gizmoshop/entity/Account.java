package com.gizmo.gizmoshop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String email;

    @Column(length = 255)
    private String fullname;

    @Column(length = 13)
    private String sdt;

    @Column(length = 255, nullable = false)
    private String password;

    private LocalDate birthday;

    @Lob
    private String image;

    @Lob
    private String extra_info;

    @Temporal(TemporalType.TIMESTAMP)
    private Date create_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date update_at;

    private Boolean deleted;

    @Column(length = 255, nullable = true)
    private String vnp_TxnRef;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RoleAccount> roleAccounts;

    @Lob
    private String noteregistersupplier;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SupplierInfo> supplierInfos;
    public String getSupplierName() {
        if (supplierInfos != null && !supplierInfos.isEmpty()) {
            return supplierInfos.iterator().next().getBusiness_name();  // Lấy tên nhà cung cấp đầu tiên
        }
        return null;  // Nếu không có nhà cung cấp, trả về null
    }
}
