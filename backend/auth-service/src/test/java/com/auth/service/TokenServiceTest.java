package com.auth.service;

import com.common.enums.UserRole;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

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

    @Test
    @DisplayName("Should throw MalformedJwtException on malformed token")
    void shouldThrowMalformedJwtExceptionOnMalformedToken() {
        assertThrows(MalformedJwtException.class, () -> tokenService.getUserIdFromClaims("not-a-token"));
    }

    @Test
    @DisplayName("Should throw SignatureException on invalid signature")
    void shouldThrowSignatureExceptionOnInvalidSignature() {
        TokenService otherTokenService = new TokenService("AnotherSecretKeyThatIsAtLeast32Chars", expiration);
        String token = otherTokenService.generateToken(userId, email, role);

        assertThrows(SignatureException.class, () -> tokenService.getUserIdFromClaims(token));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when token is null")
    void shouldThrowIllegalArgumentExceptionWhenTokenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> tokenService.getUserIdFromClaims(null));
        assertThrows(IllegalArgumentException.class, () -> tokenService.isTokenExpired(null));
    }

    @Test
    @DisplayName("Should throw SignatureException when token is tampered")
    void shouldThrowSignatureExceptionWhenTokenIsTampered() {
        String token = tokenService.generateToken(userId, email, role);
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";

        assertThrows(SignatureException.class, () -> tokenService.getUserIdFromClaims(tamperedToken));
    }
}
