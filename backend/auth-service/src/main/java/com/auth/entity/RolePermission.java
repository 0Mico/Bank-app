package com.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_permissions")
@Getter
@Setter
@NoArgsConstructor
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String role;

    @Column(name = "resource_pattern", nullable = false)
    private String resourcePattern;

    @Column(name = "allowed_actions", nullable = false)
    private String allowedActions; // Comma-separated HTTP methods: GET,POST,PUT,DELETE
}
