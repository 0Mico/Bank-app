package com.auth.service;

import com.auth.client.AccountServiceClient;
import com.auth.dto.LoginDto;
import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.auth.factory.ConcreteUserFactory;
import com.auth.factory.UserFactory;
import com.auth.model.AuthView;
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

    private final UserService userService;
    private final TokenService tokenService;
    private final AccountServiceClient accountServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final ReferenceMonitor referenceMonitor;
    private final UserFactory userFactory;

    public AuthService(UserService userService, TokenService tokenService,
                       PasswordEncoder passwordEncoder, ReferenceMonitor referenceMonitor,
                       AccountServiceClient accountServiceClient, ConcreteUserFactory userFactory) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.referenceMonitor = referenceMonitor;
        this.accountServiceClient = accountServiceClient;
        this.userFactory = userFactory;
    }

    public AuthView register(RegisterDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        if (userService.getUserByEmail(request.getEmail()) != null) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = userFactory.create(request);
        userService.save(user);
        
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
        User user = userService.getUserByEmail(request.getEmail());
        if (user == null) {
            throw new UnauthorizedException("No user found with this credentials");
        }
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

            if (!userService.checkIfUserExists(userId)) {
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
