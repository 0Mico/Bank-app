package com.bankapp.auth.security;

import com.auth.entity.RolePermission;
import com.auth.repository.RolePermissionRepository;
import com.auth.security.ReferenceMonitor;
import com.auth.service.TokenService;
import com.common.dto.TokenValidationDTO;
import com.common.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReferenceMonitor")
class ReferenceMonitorTest {

    private static final String TOKEN = "validToken";

    @Mock
    private RolePermissionRepository permissionRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private ReferenceMonitor referenceMonitor;

    private RolePermission permission(String role, String pattern, String actions) {
        RolePermission perm = new RolePermission();
        perm.setRole(role);
        perm.setResourcePattern(pattern);
        perm.setAllowedActions(actions);
        return perm;
    }

    private TokenValidationDTO request(String path, String method) {
        TokenValidationDTO req = new TokenValidationDTO();
        req.setToken(TOKEN);
        req.setRequestPath(path);
        req.setHttpMethod(method);
        return req;
    }


    @Nested
    @DisplayName("isAuthorized()")
    class IsAuthorizedTests {

        @Nested
        @DisplayName("Path pattern matching")
        class PathPatternTest {

            @Test
            @DisplayName("Should grant access when path is nested under the ** prefix")
            void wildcardMatchesDeepPath() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/payments/**", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/payments/123/status/details", "GET"))).isTrue();
            }

            @Test
            @DisplayName("Should grant access when path exactly matches the pattern")
            void exactPathMatches() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/accounts/profile", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/accounts/profile", "GET"))).isTrue();
            }

            @Test
            @DisplayName("Should deny access when path does not match rule prefix")
            void wildcardDoesNotMatchUnrelatedPath() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/auth/users/**", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/admin/users/1", "GET"))).isFalse();
            }

            @Test
            @DisplayName("Should deny access when path only partially matches an exact pattern")
            void exactPathDoesNotMatchSubPath() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/accounts/profile", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/accounts/profile/extra", "GET"))).isFalse();
            }
        }

        @Nested
        @DisplayName("HTTP method matching")
        class HttpMethodMatchingTests {

            @ParameterizedTest(name = "Should grant access for {0} method")
            @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
            void grantsAccessForAllAllowedMethods(String method) {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/transactions/**", "GET,POST,PUT,PATCH,DELETE")));

                assertThat(referenceMonitor.isAuthorized(request("/api/transactions/1", method))).isTrue();
            }

            @Test
            @DisplayName("Should deny access when HTTP method is not in the allowed actions list")
            void deniesAccessForDisallowedMethod() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/accounts/**", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/accounts/123", "POST"))).isFalse();
            }

            @Test
            @DisplayName("Should match HTTP method case-insensitively")
            void matchesMethodCaseInsensitively() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/accounts/**", "GET")));

                assertThat(referenceMonitor.isAuthorized(request("/api/accounts/123", "get"))).isTrue();
            }
        }

        @Nested
        @DisplayName("Edge cases")
        class EdgeCaseTests {

            @Test
            @DisplayName("Should deny access when the permission list is empty")
            void deniesAccessWhenNoPermissionsExist() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER")).thenReturn(Collections.emptyList());

                assertThat(referenceMonitor.isAuthorized(request("/api/transactions/1", "GET"))).isFalse();
            }

            @Test
            @DisplayName("Should grant access when any one of multiple permissions matches")
            void grantsAccessWhenOneOfMultiplePermissionsMatches() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(Arrays.asList(
                                permission("USER", "/api/auth/users/**", "GET"),
                                permission("USER", "/api/transactions/**", "GET")
                        ));

                assertThat(referenceMonitor.isAuthorized(request("/api/transactions/42", "GET"))).isTrue();
            }

            @Test
            @DisplayName("Should deny access when path matches but allowed actions list is empty")
            void deniesAccessWhenAllowedActionsEmpty() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER"))
                        .thenReturn(List.of(permission("USER", "/api/accounts/**", "")));

                assertThat(referenceMonitor.isAuthorized(request("/api/accounts/1", "GET"))).isFalse();
            }

            @Test
            @DisplayName("Should verify that getRoleFromClaims is called with the token from the request")
            void usesTokenFromRequest() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER")).thenReturn(Collections.emptyList());

                referenceMonitor.isAuthorized(request("/api/transactions/1", "GET"));

                verify(tokenService).getRoleFromClaims(TOKEN);
            }

            @Test
            @DisplayName("Should query the repository with the role resolved from the token")
            void queriesRepositoryWithResolvedRole() {
                when(tokenService.getRoleFromClaims(TOKEN)).thenReturn(UserRole.USER);
                when(permissionRepository.findByRole("USER")).thenReturn(Collections.emptyList());

                referenceMonitor.isAuthorized(request("/api/transactions/1", "GET"));

                verify(permissionRepository).findByRole("USER");
            }
        }
    }


    @Nested
    @DisplayName("initDefaultPermissions()")
    class InitDefaultPermissionsTests {

        @BeforeEach
        void setup() {
            when(permissionRepository.findByRoleAndResourcePattern(anyString(), anyString()))
                    .thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("Should save exactly 5 permissions (4 USER + 1 ADMIN)")
        void savesExactlyFivePermissions() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository, times(5)).save(any(RolePermission.class));
        }

        @Test
        @DisplayName("Should create USER permission for /api/auth/users/**")
        void createsUserAuthUsersPermission() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    "USER".equals(perm.getRole()) &&
                    "/api/auth/users/**".equals(perm.getResourcePattern()) &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should create USER permission for /api/transactions/**")
        void createsUserTransactionsPermission() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    "USER".equals(perm.getRole()) &&
                    "/api/transactions/**".equals(perm.getResourcePattern()) &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should create USER permission for /api/payments/**")
        void createsUserPaymentsPermission() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    "USER".equals(perm.getRole()) &&
                    "/api/payments/**".equals(perm.getResourcePattern()) &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should create USER permission for /api/accounts/**")
        void createsUserAccountsPermission() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    "USER".equals(perm.getRole()) &&
                    "/api/accounts/**".equals(perm.getResourcePattern()) &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should create ADMIN permission for /api/**")
        void createsAdminWildcardPermission() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    "ADMIN".equals(perm.getRole()) &&
                    "/api/**".equals(perm.getResourcePattern()) &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should update an existing permission record rather than creating a new one (upsert)")
        void updatesExistingPermissionOnUpsert() {
            RolePermission existing = permission("USER", "/api/auth/users/**", "GET");
            when(permissionRepository.findByRoleAndResourcePattern("USER", "/api/auth/users/**"))
                    .thenReturn(Optional.of(existing));

            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).save(argThat(perm ->
                    perm == existing &&
                    "GET,POST,PUT,PATCH,DELETE".equals(perm.getAllowedActions())
            ));
        }

        @Test
        @DisplayName("Should look up each resource pattern before saving (upsert semantics)")
        void loadsEachPatternBeforeSaving() {
            referenceMonitor.initDefaultPermissions();

            verify(permissionRepository).findByRoleAndResourcePattern("USER", "/api/auth/users/**");
            verify(permissionRepository).findByRoleAndResourcePattern("USER", "/api/transactions/**");
            verify(permissionRepository).findByRoleAndResourcePattern("USER", "/api/payments/**");
            verify(permissionRepository).findByRoleAndResourcePattern("USER", "/api/accounts/**");
            verify(permissionRepository).findByRoleAndResourcePattern("ADMIN", "/api/**");
        }
    }
}
