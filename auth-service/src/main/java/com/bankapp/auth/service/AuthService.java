package com.bankapp.auth.service;

import com.bankapp.auth.client.AccountServiceClient;
import com.bankapp.auth.entity.User;
import com.bankapp.auth.repository.UserRepository;
import com.bankapp.auth.security.ReferenceMonitor;
import com.bankapp.common.dto.*;
import com.bankapp.common.enums.UserRole;
import com.bankapp.common.exception.BadRequestException;
import com.bankapp.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final ReferenceMonitor referenceMonitor;
    private final AccountServiceClient accountServiceClient;

    public AuthService(UserRepository userRepository, TokenService tokenService,
            PasswordEncoder passwordEncoder, ReferenceMonitor referenceMonitor,
            AccountServiceClient accountServiceClient) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.referenceMonitor = referenceMonitor;
        this.accountServiceClient = accountServiceClient;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.USER);

        user = userRepository.save(user);

        // Auto-create an account for the new user over in Payment Service
        try {
            accountServiceClient.createAccount(user.getId());
        } catch (Exception e) {
            System.err
                    .println("Warning: Could not create account for new user " + user.getId() + " - " + e.getMessage());
        }

        String token = tokenService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, toDTO(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = tokenService.generateToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthResponse(token, toDTO(user));
    }

    /**
     * Validate token: check signature, expiration, and permissions via Reference
     * Monitor.
     */
    public TokenValidationResponse validateToken(TokenValidationRequest request) {
        try {
            Claims claims = tokenService.parseToken(request.getToken());

            if (tokenService.isTokenExpired(claims)) {
                return TokenValidationResponse.invalid("Token has expired");
            }

            Long userId = tokenService.getUserIdFromClaims(claims);
            String email = tokenService.getEmailFromClaims(claims);
            UserRole role = tokenService.getRoleFromClaims(claims);

            // Ensure the user still exists in the database
            if (!userRepository.existsById(userId)) {
                return TokenValidationResponse.invalid("User no longer exists");
            }

            // Reference Monitor — check authorization
            if (request.getRequestPath() != null && request.getHttpMethod() != null) {
                boolean authorized = referenceMonitor.isAuthorized(role, request.getRequestPath(),
                        request.getHttpMethod());
                if (!authorized) {
                    return TokenValidationResponse.invalid("Access denied: insufficient permissions");
                }
            }

            return new TokenValidationResponse(true, userId, email, role);

        } catch (JwtException e) {
            return TokenValidationResponse.invalid("Invalid token: " + e.getMessage());
        }
    }

    public static UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt());
    }
}
