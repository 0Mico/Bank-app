package com.bankapp.auth.controller;

import com.auth.controller.AuthController;
import com.auth.dto.LoginDto;
import com.auth.dto.RegisterDto;
import com.auth.exception.GlobalExceptionHandler;
import com.auth.entity.User;
import com.auth.model.AuthView;
import com.auth.service.AuthService;
import com.common.dto.TokenValidationDTO;
import com.common.model.TokenValidationView;
import com.common.enums.UserRole;
import com.common.exception.BadRequestException;
import com.common.exception.UnauthorizedException;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Auth Controller Test class")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private User testUser;
    private AuthView testAuthResponse;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.it");
        testUser.setFirstName("Test");
        testUser.setLastName("Test");
        testUser.setPhone("1234567890");
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(LocalDateTime.now());

        testAuthResponse = AuthView.builder().token("validToken").user(testUser).build();
    }

    
    @Nested
    @DisplayName("POST/register: tests")
    class RegistrationTest {

        private RegisterDto validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new RegisterDto();
            validRequest.setEmail("test@test.it");
            validRequest.setPassword("testPassword");
            validRequest.setFirstName("Test");
            validRequest.setLastName("Test");
            validRequest.setPhone("1234567890");     
        }

        @Test
        @DisplayName("200 OK — valid request returns token and user payload")
        void shouldRegisterCorrectlyNewUser() throws Exception {
            when(authService.register(any(RegisterDto.class))).thenReturn(testAuthResponse);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("validToken"))
                    .andExpect(jsonPath("$.user.id").value(1))
                    .andExpect(jsonPath("$.user.email").value("test@test.it"))
                    .andExpect(jsonPath("$.user.firstName").value("Test"))
                    .andExpect(jsonPath("$.user.lastName").value("Test"))
                    .andExpect(jsonPath("$.user.role").value("USER"));
        }

        @Test
        @DisplayName("200 OK — phone is optional, registration succeeds without it")
        void shouldRegisterCorrectlyNewUserWithoutPhone() throws Exception {
            validRequest.setPhone(null);
            when(authService.register(any(RegisterDto.class))).thenReturn(testAuthResponse);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("400 Bad Request — duplicate email throws BadRequestException")
        void shouldThrowBadRequestExceptionOnDuplicateEmail() throws Exception {
            when(authService.register(any(RegisterDto.class)))
                    .thenThrow(new BadRequestException("Email already registered: " + validRequest.getEmail()));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Email already registered: " + validRequest.getEmail()));
        }

        @Test
        @DisplayName("400 Validation Failed — blank email triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyEmail() throws Exception {
            validRequest.setEmail("");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — invalid email format triggers @Email")
        void shouldTriggerValidatoinErrorOnInvalidEmailFormat() throws Exception {
            validRequest.setEmail("invalidEmail");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — blank password triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyPassword() throws Exception {
            validRequest.setPassword("");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.password").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — short password (< 6 chars) triggers @Size")
        void shouldTriggerValidationErrorOnShortPassword() throws Exception {
            validRequest.setPassword("abc");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.password").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — blank firstName triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyFirstName() throws Exception {
            validRequest.setFirstName("");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.firstName").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — blank lastName triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyLastName() throws Exception {
            validRequest.setLastName("");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.lastName").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — multiple invalid fields, all errors reported at once")
        void shouldTriggerMultipleValidationErrors() throws Exception {
            validRequest.setEmail("bad");
            validRequest.setPassword("");
            validRequest.setFirstName("");

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists())
                    .andExpect(jsonPath("$.validationErrors.password").exists())
                    .andExpect(jsonPath("$.validationErrors.firstName").exists());
        }

        @Test
        @DisplayName("400 Bad Request — missing request body (no JSON at all)")
        void shouldReturnBadRequestOnMissingBody() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException from service layer")
        void shouldReturn500IfServerIsDown() throws Exception {
            when(authService.register(any(RegisterDto.class)))
                    .thenThrow(new RuntimeException("Database connection lost"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("Database connection lost"));
        }
    }


    @Nested
    @DisplayName("POST: /login tests")
    class LoginTest {
        
        private LoginDto validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new LoginDto();
            validRequest.setEmail("test@test.it");
            validRequest.setPassword("testPassword");    
        }

        @Test
        @DisplayName("200 OK — valid request returns token and user payload")
        void shouldLoginCorrectly() throws Exception {
            when(authService.login(any(LoginDto.class))).thenReturn(testAuthResponse);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("validToken"))
                    .andExpect(jsonPath("$.user.id").value(1))
                    .andExpect(jsonPath("$.user.email").value("test@test.it"))
                    .andExpect(jsonPath("$.user.firstName").value("Test"))
                    .andExpect(jsonPath("$.user.lastName").value("Test"))
                    .andExpect(jsonPath("$.user.role").value("USER"));
        }

        @Test
        @DisplayName("400 Bad Request — wrong email throws UnauthorizedException")
        void shouldThrowBadRequestExceptionOnDuplicateEmail() throws Exception {
            when(authService.login(any(LoginDto.class)))
                    .thenThrow(new UnauthorizedException("No user found with this email"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.error").value("Unauthorized"))
                    .andExpect(jsonPath("$.message").value("No user found with this email"));
        }

        @Test
        @DisplayName("400 Validation Failed — blank email triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyEmail() throws Exception {
            validRequest.setEmail("");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — invalid email format triggers @Email")
        void shouldTriggerValidatoinErrorOnInvalidEmailFormat() throws Exception {
            validRequest.setEmail("invalidEmail");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }

        @Test
        @DisplayName("400 Bad Request — wrong password throws UnauthorizedException")
        void shouldThrowBadRequestExceptionOnWrongPassword() throws Exception {
            when(authService.login(any(LoginDto.class)))
                    .thenThrow(new UnauthorizedException("Wrong password"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.error").value("Unauthorized"))
                    .andExpect(jsonPath("$.message").value("Wrong password"));
        }

        @Test
        @DisplayName("400 Validation Failed — blank password triggers @NotBlank")
        void shouldTriggerValidationErrorOnEmptyPassword() throws Exception {
            validRequest.setPassword("");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.password").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — short password (< 6 chars) triggers @Size")
        void shouldTriggerValidationErrorOnShortPassword() throws Exception {
            validRequest.setPassword("abc");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.password").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — multiple invalid fields, all errors reported at once")
        void shouldTriggerMultipleValidationErrors() throws Exception {
            validRequest.setEmail("bad");
            validRequest.setPassword("dab");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists())
                    .andExpect(jsonPath("$.validationErrors.password").exists());
        }

        @Test
        @DisplayName("400 Bad Request — missing request body (no JSON at all)")
        void shouldReturnBadRequestOnMissingBody() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException from service layer")
        void shouldReturn500IfServerIsDown() throws Exception {
            when(authService.login(any()))
                    .thenThrow(new RuntimeException("Database connection lost"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"))
                    .andExpect(jsonPath("$.message").value("Database connection lost"));
        }
    }


    @Nested
    @DisplayName("POST: /validate tests")
    class TokenValidationTest {

        private TokenValidationDTO validRequest;
        private TokenValidationView successResponse;

        @BeforeEach
        void setUp() {
            validRequest = new TokenValidationDTO("validToken", "/api/accounts", "GET");
            successResponse = TokenValidationView.builder().valid(true).userId(1L).email("test@test.it").role(UserRole.USER).message(null).build();
        }
        
        @Test
        @DisplayName("200 OK — valid token, ReferenceMonitor grants access")
        void shouldValidateTokenSuccessfully() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class))).thenReturn(successResponse);

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(true));
        }

        @Test
        @DisplayName("400 Bad Request — null requestPath throws BadRequestException")
        void shouldReturn400WhenRequestPathIsMissing() throws Exception {
            validRequest.setRequestPath(null);

            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new BadRequestException("Request path and HTTP method are required for token validation"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Request path and HTTP method are required for token validation"));
        }

        @Test
        @DisplayName("400 Bad Request — null token throws BadRequestException")
        void shouldReturn400OnNullToken() throws Exception {
            validRequest.setToken(null);

            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new BadRequestException("Token validation request or token cannot be null"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Token validation request or token cannot be null"));
        }

        @Test
        @DisplayName("400 Bad Request — missing request body entirely")
        void shouldReturn400OnMissingBody() throws Exception {
            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("401 Unauthorized — expired token throws UnauthorizedException")
        void shouldReturn401OnExpiredToken() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new UnauthorizedException("Token has expired"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.error").value("Unauthorized"))
                    .andExpect(jsonPath("$.message").value("Token has expired"));
        }

        @Test
        @DisplayName("401 Unauthorized — user deleted after token was issued")
        void shouldReturn401WhenUserNoLongerExists() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new UnauthorizedException("User no longer exists"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("User no longer exists"));
        }

        @Test
        @DisplayName("401 Unauthorized — ReferenceMonitor denies access (insufficient role)")
        void shouldReturn401WhenReferenceMonitorDeniesAccess() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new UnauthorizedException("Access denied: insufficient permissions"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Access denied: insufficient permissions"));
        }

        @Test
        @DisplayName("401 Unauthorized — malformed/tampered JWT throws UnauthorizedException")
        void shouldReturn401OnMalformedToken() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new UnauthorizedException("Invalid token: JWT signature does not match"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid token: JWT signature does not match"));
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException from service layer")
        void shouldReturn500OnUnexpectedException() throws Exception {
            when(authService.validateToken(any(TokenValidationDTO.class)))
                    .thenThrow(new RuntimeException("DB connection refused"));

            mockMvc.perform(post("/api/auth/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500))
                    .andExpect(jsonPath("$.error").value("Internal Server Error"));
        }
    }

}

