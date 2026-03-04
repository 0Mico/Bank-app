package com.bankapp.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getResourcePattern() { return resourcePattern; }
    public void setResourcePattern(String resourcePattern) { this.resourcePattern = resourcePattern; }
    public String getAllowedActions() { return allowedActions; }
    public void setAllowedActions(String allowedActions) { this.allowedActions = allowedActions; }
}
