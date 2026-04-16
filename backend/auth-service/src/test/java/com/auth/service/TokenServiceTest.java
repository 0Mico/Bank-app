package com.auth.service;

import com.auth.service.TokenService;
import com.common.enums.UserRole;

import io.jsonwebtoken.ExpiredJwtException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Token Service Test class")
public class TokenServiceTest {

    private TokenService tokenService;
    private final String secret = "TestingSecretKeyAtLeast32Characters";
    private final long expiration = 3600000;
    private Long userId;
    private String email;
    private UserRole role;

    @BeforeEach
    void setup() {
        tokenService = new TokenService(secret, expiration);
        userId = 1L;
        email = "test@test.it";
        role = UserRole.USER;
    }

    @Test
    @DisplayName("Should generate a non-null token")
    void shouldGenerateToken() {
        String token = tokenService.generateToken(userId, email, role);
        
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException on null parameters")
    void shouldThrowIllegalArgumentExceptionOnNullParameters() {
        assertThrows(IllegalArgumentException.class, () -> tokenService.generateToken(null, email, role));
        assertThrows(IllegalArgumentException.class, () -> tokenService.generateToken(userId, null, role));
        assertThrows(IllegalArgumentException.class, () -> tokenService.generateToken(userId, email, null));
    }

    @Test
    @DisplayName("Should extract userId from token claims")
    void shouldExtractUserIdFromClaims() {
        String token = tokenService.generateToken(userId, email, role);
        Long extractedUserId = tokenService.getUserIdFromClaims(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract email from token claims")
    void shouldExtractEmailFromClaims() {
        String token = tokenService.generateToken(userId, email, role);
        String extractedEmail = tokenService.getEmailFromClaims(token);

        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("Should extract user role from token claims")
    void shouldExtractRoleFromClaims() {
        String token = tokenService.generateToken(userId, email, role);
        UserRole extractedRole = tokenService.getRoleFromClaims(token);

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Should return false when token is not expired")
    void shouldReturnFalseWhenTokenIsNotExpired() {
        String token = tokenService.generateToken(userId, email, role);
        boolean isExpired = tokenService.isTokenExpired(token);

        assertThat(isExpired).isFalse();
    }

    @Test
    @DisplayName("Should return true when token is expired")
    void shouldReturnTrueWhenTokenIsExpired() {
        TokenService expiredTokenService = new TokenService(secret, -10000);
        String token = expiredTokenService.generateToken(userId, email, role);

        assertThrows(ExpiredJwtException.class, () -> expiredTokenService.isTokenExpired(token));
    }
}
