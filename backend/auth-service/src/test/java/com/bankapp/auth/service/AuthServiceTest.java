package com.bankapp.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import com.auth.service.AuthService;
import com.auth.service.TokenService;
import com.auth.factory.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth.client.AccountServiceClient;
import com.auth.dto.LoginDto;
import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.auth.model.AuthView;
import com.auth.repository.UserRepository;
import com.auth.security.ReferenceMonitor;
import com.common.dto.TokenValidationDTO;
import com.common.model.TokenValidationView;
import com.common.enums.UserRole;
import com.common.exception.BadRequestException;
import com.common.exception.UnauthorizedException;

import java.util.Optional;

import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service Test class")
public class AuthServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private TokenService tokenService;
    @Mock private AccountServiceClient accountServiceClient;
    @Mock private PasswordEncoder encoder;
    @Mock private ReferenceMonitor referenceMonitor;
    @Mock private UserFactory userFactory;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private String mockToken;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.it");
        testUser.setFirstName("Test");
        testUser.setLastName("Test");
        testUser.setPhone("123456789");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(LocalDateTime.now());

        mockToken = "mockToken";
    }


    @Nested
    @DisplayName("Registration tests")
    class RegistrationTest {

        private RegisterDto request;

        @BeforeEach
        void setup
        
        () {
            request = new RegisterDto();
            request.setEmail("test@test.it");
            request.setFirstName("Test");
            request.setLastName("Test");
            request.setPhone("123456789");
            request.setPassword("password");
        }

        @Test
        @DisplayName("should register a new user correctly")
        void shouldRegisterANewUserCorrectly() {
            when(userRepo.existsByEmail(request.getEmail())).thenReturn(false);
            when(userFactory.create(request)).thenReturn(testUser);
            when(userRepo.save(any(User.class))).thenReturn(testUser);
            when(tokenService.generateToken(testUser.getId(), request.getEmail(), testUser.getRole())).thenReturn(mockToken);

            AuthView response = authService.register(request);

            assertEquals(mockToken, response.getToken());
            assertEquals(testUser.getEmail(), response.getUser().getEmail());

            verify(userRepo, times(1)).existsByEmail(request.getEmail());
            verify(userFactory, times(1)).create(request);
            verify(userRepo, times(1)).save(any(User.class));
            verify(accountServiceClient, times(1)).createAccount(testUser.getId());
            verify(tokenService, times(1)).generateToken(testUser.getId(), request.getEmail(), testUser.getRole());
        }

        @Test
        @DisplayName("Should return BadRequestException if user already exists")
        void shouldReturnBadRequestExceptionIfUserAlreadyExists() {
            when(userRepo.existsByEmail(request.getEmail())).thenReturn(true);

            BadRequestException exception = assertThrows(BadRequestException.class,
                 () -> authService.register(request));
            assertEquals("Email already registered: " + request.getEmail(), exception.getMessage());

            verify(userRepo, times(1)).existsByEmail(request.getEmail());
            verify(accountServiceClient, never()).createAccount(anyLong());
            verify(tokenService, never()).generateToken(anyLong(), any(String.class), any(UserRole.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when register request is null")
        void shouldThrowIllegalArgumentExceptionWhenRegisterRequestIsNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(null));
            assertEquals("Registration request cannot be null", exception.getMessage());

            verify(userRepo, never()).existsByEmail(anyString());
        }
    }


    @Nested
    @DisplayName("Login tests")
    class LoginTest {

        private LoginDto loginRequest;

        @BeforeEach
        void setup() {
            loginRequest = new LoginDto();
            loginRequest.setEmail(testUser.getEmail());
            loginRequest.setPassword("password");
        }

        @Test
        @DisplayName("Should login successfully and return token")
        void shouldLoginSuccessfully() {
            when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
            when(encoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(true);
            when(tokenService.generateToken(testUser.getId(), testUser.getEmail(), testUser.getRole())).thenReturn(mockToken);

            AuthView response = authService.login(loginRequest);

            assertEquals(mockToken, response.getToken());
            assertEquals(testUser.getEmail(), response.getUser().getEmail());
        }

        @Test
        @DisplayName("Should throw UnauthorizedException if user not found")
        void shouldThrowUnauthorizedExceptionIfUserNotFound() {
            when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

            UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.login(loginRequest));
            assertEquals("No user found with this email", exception.getMessage());

            verify(encoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw UnauthorizedException for wrong password")
        void shouldThrowUnauthorizedExceptionForWrongPassword() {
            when(userRepo.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
            when(encoder.matches(loginRequest.getPassword(), testUser.getPasswordHash())).thenReturn(false);

            UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> authService.login(loginRequest));
            assertEquals("Wrong password", exception.getMessage());

            verify(tokenService, never()).generateToken(anyLong(), anyString(), any(UserRole.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when login request is null")
        void shouldThrowIllegalArgumentExceptionWhenLoginRequestIsNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(null));
            assertEquals("Login request cannot be null", exception.getMessage());

            verify(userRepo, never()).findByEmail(anyString());
        }
    }


    @Nested
    @DisplayName("Token validation tests")
    class TokenValidationTest {
        
        private TokenValidationDTO validationRequest;

        @BeforeEach
        void setup() {
            validationRequest = new TokenValidationDTO();
            validationRequest.setToken(mockToken);
            validationRequest.setRequestPath("/api/accounts");
            validationRequest.setHttpMethod("GET");
        }

        @Test
        @DisplayName("Should validate token successfully")
        void shouldValidateTokenSuccessfully() {
            when(tokenService.isTokenExpired(mockToken)).thenReturn(false);
            when(tokenService.getUserIdFromClaims(mockToken)).thenReturn(testUser.getId());
            when(tokenService.getEmailFromClaims(mockToken)).thenReturn(testUser.getEmail());
            when(tokenService.getRoleFromClaims(mockToken)).thenReturn(testUser.getRole());
            when(userRepo.existsById(testUser.getId())).thenReturn(true);
            when(referenceMonitor.isAuthorized(validationRequest)).thenReturn(true);

            TokenValidationView response = authService.validateToken(validationRequest);

            assertEquals(true, response.getValid());
            assertEquals(testUser.getId(), response.getUserId());
        }

        @Test
        @DisplayName("Should throw UnauthorizedException for expired token")
        void shouldThrowUnauthorizedExceptionForExpiredToken() {
            when(tokenService.isTokenExpired(mockToken)).thenReturn(true);

            UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("Token has expired", exception.getMessage());

            verify(userRepo, never()).existsById(anyLong());
        }

        @Test
        @DisplayName("Should throw UnauthorizedException if user does not exist")
        void shouldThrowUnauthorizedExceptionIfUserDoesNotExist() {
            when(tokenService.isTokenExpired(mockToken)).thenReturn(false);
            when(tokenService.getUserIdFromClaims(mockToken)).thenReturn(testUser.getId());
            when(tokenService.getEmailFromClaims(mockToken)).thenReturn(testUser.getEmail());
            when(tokenService.getRoleFromClaims(mockToken)).thenReturn(testUser.getRole());
            when(userRepo.existsById(testUser.getId())).thenReturn(false);

            UnauthorizedException excdeption = assertThrows(UnauthorizedException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("User no longer exists", excdeption.getMessage());

            verify(referenceMonitor, never()).isAuthorized(any(TokenValidationDTO.class));
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when reference monitor denies access")
        void shouldThrowUnauthorizedExceptionWhenReferenceMonitorDeniesAccess() {
            when(tokenService.isTokenExpired(mockToken)).thenReturn(false);
            when(tokenService.getUserIdFromClaims(mockToken)).thenReturn(testUser.getId());
            when(tokenService.getEmailFromClaims(mockToken)).thenReturn(testUser.getEmail());
            when(tokenService.getRoleFromClaims(mockToken)).thenReturn(testUser.getRole());
            when(userRepo.existsById(testUser.getId())).thenReturn(true);
            when(referenceMonitor.isAuthorized(validationRequest)).thenReturn(false);

            UnauthorizedException excdeption = assertThrows(UnauthorizedException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("Access denied: insufficient permissions", excdeption.getMessage());
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when JwtException is thrown during parsing")
        void shouldThrowUnauthorizedExceptionWhenJwtExceptionIsThrownDuringParsing() {
            when(tokenService.isTokenExpired(mockToken)).thenReturn(false);
            when(tokenService.getUserIdFromClaims(mockToken)).thenThrow(new JwtException("Invalid signature"));

            UnauthorizedException excdeption = assertThrows(UnauthorizedException.class,
                    () -> authService.validateToken(validationRequest));
            assertThat(excdeption.getMessage()).contains("Invalid token: Invalid signature");
        }

        @Test
        @DisplayName("Should throw BadRequestException when validation request is null")
        void shouldThrowBadRequestExceptionWhenValidationRequestIsNull() {
            BadRequestException excdeption = assertThrows(BadRequestException.class,
                    () -> authService.validateToken(null));
            assertEquals("Token validation request or token cannot be null", excdeption.getMessage());
        }

        @Test
        @DisplayName("Should throw BadRequestException when token in request is null")
        void shouldThrowBadRequestExceptionWhenTokenInRequestIsNull() {
            validationRequest.setToken(null);

            BadRequestException excdeption = assertThrows(BadRequestException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("Token validation request or token cannot be null", excdeption.getMessage());
        }

        @Test
        @DisplayName("Should throw BadRequestException when requestPath is null")
        void shouldThrowBadRequestExceptionWhenRequestPathIsNull() {
            validationRequest.setRequestPath(null);

            BadRequestException excdeption = assertThrows(BadRequestException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("Request path and HTTP method are required for token validation", excdeption.getMessage());
        }

        @Test
        @DisplayName("Should throw BadRequestException when httpMethod is null")
        void shouldThrowBadRequestExceptionWhenHttpMethodIsNull() {
            validationRequest.setHttpMethod(null);

            BadRequestException excdeption = assertThrows(BadRequestException.class,
                    () -> authService.validateToken(validationRequest));
            assertEquals("Request path and HTTP method are required for token validation", excdeption.getMessage());
        }
    }
}