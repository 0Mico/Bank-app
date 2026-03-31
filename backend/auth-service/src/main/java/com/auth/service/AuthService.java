package com.auth.service;

import com.auth.client.AccountServiceClient;
import com.auth.dto.LoginDto;
import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.auth.model.AuthView;
import com.auth.repository.UserRepository;
import com.auth.security.ReferenceMonitor;
import com.common.dto.TokenValidationDTO;
import com.common.enums.UserRole;
import com.common.exception.BadRequestException;
import com.common.exception.UnauthorizedException;

import com.common.model.TokenValidationView;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class AuthService {

    private final Logger logger = Logger.getLogger(AuthService.class.getName());

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AccountServiceClient accountServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final ReferenceMonitor referenceMonitor;

    public AuthService(UserRepository userRepository, TokenService tokenService,
            PasswordEncoder passwordEncoder, ReferenceMonitor referenceMonitor,
            AccountServiceClient accountServiceClient) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.referenceMonitor = referenceMonitor;
        this.accountServiceClient = accountServiceClient;
    }

    public AuthView register(RegisterDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        //factory
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.USER);
        user = userRepository.save(user);

        try {
            accountServiceClient.createAccount(user.getId());
        } catch (Exception e) {
            logger.info("Warning: Could not create account for new user " + user.getId() + " - " + e.getMessage());
        }

        return AuthView.builder()
            .user(user)
            .token(tokenService.generateToken(user.getId(), user.getEmail(), user.getRole()))
            .build();
    }

    public AuthView login(LoginDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("No user found with this email"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Wrong password");
        }
        return AuthView.builder()
                .user(user)
                .token(tokenService.generateToken(user.getId(), user.getEmail(), user.getRole()))
                .build();
    }

    
    public TokenValidationView validateToken(TokenValidationDTO request) {
        if (request == null || request.getToken() == null) {
            throw new BadRequestException("Token validation request or token cannot be null");
        }
        if (request.getRequestPath() == null || request.getHttpMethod() == null) {
            throw new BadRequestException("Request path and HTTP method are required for token validation");
        }
        try {
            String token = request.getToken();
            if (tokenService.isTokenExpired(token)) {
                throw new UnauthorizedException("Token has expired");
            }

            Long userId = tokenService.getUserIdFromClaims(token);
            String email = tokenService.getEmailFromClaims(token);
            UserRole role = tokenService.getRoleFromClaims(token);

            if (!userRepository.existsById(userId)) {
                throw new UnauthorizedException("User no longer exists");
            }

            if (!referenceMonitor.isAuthorized(request)) {
                throw new UnauthorizedException("Access denied: insufficient permissions");
            }

            return TokenValidationView.builder()
                    .valid(true)
                    .userId(userId)
                    .email(email)
                    .role(role)
                    .message(null)
                    .build();

        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid token: " + e.getMessage());
        }
    }
}
