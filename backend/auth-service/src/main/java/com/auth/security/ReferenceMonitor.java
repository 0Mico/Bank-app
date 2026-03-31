package com.auth.security;

import com.auth.entity.RolePermission;
import com.auth.repository.RolePermissionRepository;
import com.auth.service.TokenService;
import com.common.dto.TokenValidationDTO;
import com.common.enums.UserRole;
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

    private final static String ALL_ACTIONS = "GET,POST,PUT,PATCH,DELETE";

    private final RolePermissionRepository permissionRepository;
    private final TokenService tokenService;

    public ReferenceMonitor(RolePermissionRepository permissionRepository, TokenService tokenService) {
        this.permissionRepository = permissionRepository;
        this.tokenService = tokenService;
    }

    @PostConstruct
    public void initDefaultPermissions() {
        // USER role permissions
        upsertPermission("USER", "/api/auth/users/**", ALL_ACTIONS);
        upsertPermission("USER", "/api/transactions/**", ALL_ACTIONS);
        upsertPermission("USER", "/api/payments/**", ALL_ACTIONS);
        upsertPermission("USER", "/api/accounts/**", ALL_ACTIONS);

        // ADMIN role permissions (full access)
        upsertPermission("ADMIN", "/api/**", ALL_ACTIONS);
    }

    /**
     * Check if a user with the given role is authorized to perform
     * the specified HTTP method on the given request path.
     */
    public boolean isAuthorized(TokenValidationDTO request) {
        String token = request.getToken();
        UserRole role = tokenService.getRoleFromClaims(token);
        String requestPath = request.getRequestPath();
        String httpMethod = request.getHttpMethod();
        List<RolePermission> permissions = permissionRepository.findByRole(role.name());

        return permissions.stream().anyMatch(permission -> {
            boolean pathMatches = matchesPattern(requestPath, permission.getResourcePattern());
            boolean methodAllowed = Arrays.asList(permission.getAllowedActions().split(","))
                    .contains(httpMethod.toUpperCase());
            return pathMatches && methodAllowed;
        });
    }

    /**
     * Path pattern matching supporting ** wildcard.
     * A pattern ending with /** matches any path that starts with the given prefix.
     */
    private boolean matchesPattern(String path, String pattern) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
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
