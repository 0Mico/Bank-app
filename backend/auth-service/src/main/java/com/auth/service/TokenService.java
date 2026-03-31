package com.auth.service;

import com.common.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    private final SecretKey key;
    private final long expiration;

    public TokenService( @Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String email, UserRole role) {
        if(userId == null || email == null || role == null) {
            throw new IllegalArgumentException("User ID, email, and role must not be null");
        }
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().before(new Date());
    }

    public Long getUserIdFromClaims(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getEmailFromClaims(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    public UserRole getRoleFromClaims(String token) {
        Claims claims = parseToken(token);
        return UserRole.valueOf(claims.get("role", String.class));
    }
}
