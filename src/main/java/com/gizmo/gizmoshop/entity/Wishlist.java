    package com.gizmo.gizmoshop.entity;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.List;

    @Entity
    @Table(name = "wishlist")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor

    public class Wishlist {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "account_id", nullable = false)
        private Account accountId; // Hoặc có thể sử dụng Account nếu có quan hệ với lớp Account

        @Column(name = "create_date")
        private LocalDateTime createDate;

        @Column(name = "update_date")
        private LocalDateTime updateDate;

        @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<WishlistItems> wishlistItems;
    }
