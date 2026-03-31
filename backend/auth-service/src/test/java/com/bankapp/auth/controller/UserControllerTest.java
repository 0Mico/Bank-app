package com.bankapp.auth.controller;

import com.auth.controller.UserController;
import com.auth.dto.ChangePasswordDto;
import com.auth.exception.GlobalExceptionHandler;
import com.auth.service.UserService;
import com.common.enums.UserRole;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.auth.entity.User;
import com.auth.model.UserModel;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("User Controller Test class")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    private User testUser;

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
    }


    @Nested
    @DisplayName("GET /api/auth/users/{id}")
    class GetUserByIdTest {

        @Test
        @DisplayName("200 OK — existing user id returns UserDTO")
        void shouldReturnUserById() throws Exception {
            when(userService.getUserById(1L)).thenReturn(testUser);

            mockMvc.perform(get("/api/auth/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("test@test.it"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("404 Not Found — non-existent id throws ResourceNotFoundException")
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(userService.getUserById(99L))
                    .thenThrow(new ResourceNotFoundException("User", 99L));

            mockMvc.perform(get("/api/auth/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("User not found with id: 99"));
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException from service layer")
        void shouldReturn500OnUnexpectedException() throws Exception {
            when(userService.getUserById(anyLong()))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/auth/users/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/users/email")
    class GetUserByEmailTest {

        @Test
        @DisplayName("200 OK — existing email returns UserDTO")
        void shouldReturnUserByEmail() throws Exception {
            when(userService.getUserByEmail("test@test.it")).thenReturn(testUser);

            mockMvc.perform(get("/api/auth/users/email")
                            .param("email", "test@test.it"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("test@test.it"));
        }

        @Test
        @DisplayName("404 Not Found — non-existent email throws ResourceNotFoundException")
        void shouldReturn404WhenEmailNotFound() throws Exception {
            when(userService.getUserByEmail("unknown@test.it"))
                    .thenThrow(new ResourceNotFoundException("User not found with email: unknown@test.it"));

            mockMvc.perform(get("/api/auth/users/email")
                            .param("email", "unknown@test.it"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("User not found with email: unknown@test.it"));
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException from service layer")
        void shouldReturn500OnUnexpectedException() throws Exception {
            when(userService.getUserByEmail(any()))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(get("/api/auth/users/email")
                            .param("email", "test@test.it"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/auth/users/iban")
    class GetUserByIbanTest {

        @Test
        @DisplayName("200 OK — existing IBAN returns the owning UserDTO")
        void shouldReturnUserByIban() throws Exception {
            when(userService.getUserByIban("IT60X0542811101000000123456")).thenReturn(testUser);

            mockMvc.perform(get("/api/auth/users/iban")
                            .param("iban", "IT60X0542811101000000123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("404 Not Found — account with IBAN not found")
        void shouldReturn404WhenIbanNotFound() throws Exception {
            when(userService.getUserByIban("UNKNOWN_IBAN"))
                    .thenThrow(new ResourceNotFoundException("Account not found with iban: UNKNOWN_IBAN"));

            mockMvc.perform(get("/api/auth/users/iban")
                            .param("iban", "UNKNOWN_IBAN"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("500 Internal Server Error — account-service unavailable")
        void shouldReturn500WhenAccountServiceIsDown() throws Exception {
            when(userService.getUserByIban(any()))
                    .thenThrow(new RuntimeException("account-service unreachable"));

            mockMvc.perform(get("/api/auth/users/iban")
                            .param("iban", "IT60X0542811101000000123456"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ===========================================================================
    // PUT /api/auth/users/{id}
    // ===========================================================================

    @Nested
    @DisplayName("PUT /api/auth/users/{id}")
    class UpdateUserTest {

        private UserModel updatePayload;

        @BeforeEach
        void setUp() {
            updatePayload = new UserModel();
            updatePayload.setFirstName("Updated");
            updatePayload.setLastName("Name");
        }

        @Test
        @DisplayName("200 OK — valid id and body returns updated UserDTO")
        void shouldUpdateUser() throws Exception {
            User updated = new User();
            updated.setId(1L);
            updated.setEmail("test@test.it");
            updated.setFirstName("Updated");
            updated.setLastName("Name");
            updated.setRole(UserRole.USER);
            when(userService.updateUser(eq(1L), any(UserModel.class))).thenReturn(updated);

            mockMvc.perform(put("/api/auth/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePayload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Name"));
        }

        @Test
        @DisplayName("404 Not Found — non-existent id throws ResourceNotFoundException")
        void shouldReturn404WhenUserNotFound() throws Exception {
            when(userService.updateUser(eq(99L), any(UserModel.class)))
                    .thenThrow(new ResourceNotFoundException("User", 99L));

            mockMvc.perform(put("/api/auth/users/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePayload)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id: 99"));
        }

        @Test
        @DisplayName("400 Bad Request — missing request body")
        void shouldReturn400OnMissingBody() throws Exception {
            mockMvc.perform(put("/api/auth/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException")
        void shouldReturn500OnUnexpectedException() throws Exception {
            when(userService.updateUser(anyLong(), any(UserModel.class)))
                    .thenThrow(new RuntimeException("Database error"));

            mockMvc.perform(put("/api/auth/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePayload)))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ===========================================================================
    // DELETE /api/auth/users/{id}
    // ===========================================================================

    @Nested
    @DisplayName("DELETE /api/auth/users/{id}")
    class DeleteUserTest {

        @Test
        @DisplayName("204 No Content — existing user is deleted")
        void shouldDeleteUser() throws Exception {
            doNothing().when(userService).deleteUser(1L);

            mockMvc.perform(delete("/api/auth/users/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("404 Not Found — non-existent id throws ResourceNotFoundException")
        void shouldReturn404WhenUserNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("User", 99L))
                    .when(userService).deleteUser(99L);

            mockMvc.perform(delete("/api/auth/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id: 99"));
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException")
        void shouldReturn500OnUnexpectedException() throws Exception {
            doThrow(new RuntimeException("Database error"))
                    .when(userService).deleteUser(anyLong());

            mockMvc.perform(delete("/api/auth/users/1"))
                    .andExpect(status().isInternalServerError());
        }
    }

    // ===========================================================================
    // POST /api/auth/users/{id}/password
    // ===========================================================================

    @Nested
    @DisplayName("POST /api/auth/users/{id}/password")
    class ChangePasswordTest {

        private ChangePasswordDto validRequest;

        @BeforeEach
        void setUp() {
            validRequest = new ChangePasswordDto("currentPass1", "newPassword1");
        }

        @Test
        @DisplayName("200 OK — correct current password, valid new password")
        void shouldChangePasswordSuccessfully() throws Exception {
            doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordDto.class));

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("404 Not Found — non-existent user id")
        void shouldReturn404WhenUserNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("User", 99L))
                    .when(userService).changePassword(eq(99L), any(ChangePasswordDto.class));

            mockMvc.perform(post("/api/auth/users/99/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found with id: 99"));
        }

        @Test
        @DisplayName("400 Bad Request — current password is wrong")
        void shouldReturn400WhenCurrentPasswordIsWrong() throws Exception {
            doThrow(new BadRequestException("Invalid current password"))
                    .when(userService).changePassword(eq(1L), any(ChangePasswordDto.class));

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Invalid current password"));
        }

        @Test
        @DisplayName("400 Validation Failed — blank currentPassword triggers @NotBlank")
        void shouldReturn400WhenCurrentPasswordIsBlank() throws Exception {
            validRequest.setCurrentPassword("");

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.currentPassword").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — blank newPassword triggers @NotBlank")
        void shouldReturn400WhenNewPasswordIsBlank() throws Exception {
            validRequest.setNewPassword("");

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.newPassword").exists());
        }

        @Test
        @DisplayName("400 Validation Failed — short newPassword (< 6 chars) triggers @Size")
        void shouldReturn400WhenNewPasswordIsTooShort() throws Exception {
            validRequest.setNewPassword("abc");

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation Failed"))
                    .andExpect(jsonPath("$.validationErrors.newPassword").exists());
        }

        @Test
        @DisplayName("400 Bad Request — missing request body")
        void shouldReturn400OnMissingBody() throws Exception {
            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("500 Internal Server Error — unexpected RuntimeException")
        void shouldReturn500OnUnexpectedException() throws Exception {
            doThrow(new RuntimeException("Database error"))
                    .when(userService).changePassword(anyLong(), any(ChangePasswordDto.class));

            mockMvc.perform(post("/api/auth/users/1/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest)))
                    .andExpect(status().isInternalServerError());
        }
    }
}
