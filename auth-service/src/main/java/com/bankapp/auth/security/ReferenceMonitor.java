package com.bankapp.auth.security;

import com.bankapp.auth.entity.RolePermission;
import com.bankapp.auth.repository.RolePermissionRepository;
import com.bankapp.common.enums.UserRole;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Reference Monitor — central authorization enforcement point.
 * Evaluates whether a user with a given role is allowed to access
 * a specific resource (path) with a specific HTTP method.
 */
@Component
public class ReferenceMonitor {

    private final RolePermissionRepository permissionRepository;

    public ReferenceMonitor(RolePermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @PostConstruct
    public void initDefaultPermissions() {
        // USER role permissions
        upsertPermission("USER", "/api/auth/users/**", "GET,POST,PUT,DELETE");
        upsertPermission("USER", "/api/transactions/**", "GET,POST,PUT,DELETE");
        upsertPermission("USER", "/api/payments/**", "GET,POST,PUT,PATCH,DELETE");

        // ADMIN role permissions (full access)
        upsertPermission("ADMIN", "/api/**", "GET,POST,PUT,PATCH,DELETE");
    }

    /**
     * Check if a user with the given role is authorized to perform
     * the specified HTTP method on the given request path.
     */
    public boolean isAuthorized(UserRole role, String requestPath, String httpMethod) {
        List<RolePermission> permissions = permissionRepository.findByRole(role.name());

        return permissions.stream().anyMatch(permission -> {
            boolean pathMatches = matchesPattern(requestPath, permission.getResourcePattern());
            boolean methodAllowed = Arrays.asList(permission.getAllowedActions().split(","))
                    .contains(httpMethod.toUpperCase());
            return pathMatches && methodAllowed;
        });
    }

    /**
     * Simple path pattern matching supporting ** and * wildcards.
     */
    private boolean matchesPattern(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        } else if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            String remaining = path.substring(prefix.length());
            return path.startsWith(prefix) && !remaining.substring(1).contains("/");
        }
        return path.equals(pattern);
    }

    private void upsertPermission(String role, String resourcePattern, String allowedActions) {
        RolePermission perm = permissionRepository
                .findByRoleAndResourcePattern(role, resourcePattern)
                .orElse(new RolePermission());
        perm.setRole(role);
        perm.setResourcePattern(resourcePattern);
        perm.setAllowedActions(allowedActions);
        permissionRepository.save(perm);
    }
}
