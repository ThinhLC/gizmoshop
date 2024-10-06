package com.gizmo.gizmoshop.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "role_account")
public class RoleAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "id_account", nullable = false)
    private Account account;
}
