package com.bankapp.auth.service;

import com.auth.client.AccountServiceClient;
import com.auth.dto.ChangePasswordDto;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.UserService;
import com.common.dto.AccountDTO;
import com.common.enums.UserRole;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.common.exception.ServiceUnavailableException;
import com.auth.model.UserModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Test class")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AccountServiceClient accountServiceClient;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Test");
        testUser.setPhone("123456789");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(UserRole.USER);
        testUser.setCreatedAt(LocalDateTime.now());
    }


    @Nested
    @DisplayName("Tests for getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return userDTO if user exists")
        void shouldReturnUserDTOIfUserExists() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            User result = userService.getUserById(testUser.getId());

            assertEquals(testUser, result);

            verify(userRepository, times(1)).findById(testUser.getId());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void shouldThrowExceptionIfUserDoesNotExist() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(testUser.getId()));
            assertEquals("User not found with id: " + testUser.getId(), exception.getMessage());

            verify(userRepository, times(1)).findById(testUser.getId());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when id is null")
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.getUserById(null));
        }
    }


    @Nested
    @DisplayName("Tests for getUserByEmail")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Should return userDTO if user exists")
        void shouldReturnUserDTOIfUserExists() {
            when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

            User result = userService.getUserByEmail(testUser.getEmail());

            assertEquals(testUser, result);

            verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void shouldThrowExceptionIfUserDoesNotExist() {
            when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail(testUser.getEmail()));
            assertEquals("User not found with email: " + testUser.getEmail(), exception.getMessage());

            verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when email is null")
        void shouldThrowIllegalArgumentExceptionWhenEmailIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.getUserByEmail(null));
        }
    }

        
    @Nested
    @DisplayName("Tests for getUserByIban")
    class GetUserByIbanTests {

        private AccountDTO testAccountDTO;

        @BeforeEach
        void setup() {
            testAccountDTO = new AccountDTO();
            testAccountDTO.setId(1L);
            testAccountDTO.setUserId(testUser.getId());
            testAccountDTO.setIban("IT12345678901234567890");
            testAccountDTO.setBalance(new BigDecimal("1000.00"));
            testAccountDTO.setCurrency("EUR");
        }
         
        @Test
        @DisplayName("should return UserDTO when account for IBAN exists and user exists")
        void shouldReturnUserDTOIfBothAccountForIbanAndUserExist() {
            when(accountServiceClient.getAccountByIban(testAccountDTO.getIban())).thenReturn(testAccountDTO);
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            User result = userService.getUserByIban(testAccountDTO.getIban());

            assertEquals(testUser, result);

            verify(accountServiceClient, times(1)).getAccountByIban(testAccountDTO.getIban());
            verify(userRepository, times(1)).findById(testUser.getId());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when IBAN does not exist")
        void shouldThrowResourceNotFoundExceptionWhenIbanDoesNotExist() {
            String iban = "NON_EXISTENT_IBAN";
            when(accountServiceClient.getAccountByIban(iban))
                .thenThrow(new ResourceNotFoundException("Account with IBAN " + iban + " not found"));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByIban(iban));
            assertEquals("Account with IBAN " + iban + " not found", exception.getMessage());

            verify(accountServiceClient, times(1)).getAccountByIban(iban);
            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("should throw Exception when account service client fails")
        void shouldThrowExceptionWhenAccountServiceClientFails() {
            String iban = "NON_EXISTENT_IBAN";
            when(accountServiceClient.getAccountByIban(iban))
                .thenThrow(new ServiceUnavailableException("account-service"));

            ServiceUnavailableException exception = assertThrows(ServiceUnavailableException.class,
                () -> userService.getUserByIban(iban));
            assertEquals("account-service", exception.getMessage());

            verify(accountServiceClient, times(1)).getAccountByIban(iban);
            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when iban is too long")
        void shouldThrowIllegalArgumentExceptionWhenIbanIsTooLong() {
            String iban = "IT1234567890123456789012345678901234567890";
            assertThrows(IllegalArgumentException.class, () -> userService.getUserByIban(iban));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when iban is null")
        void shouldThrowIllegalArgumentExceptionWhenIbanIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.getUserByIban(null));
        }
    }


    @Nested
    @DisplayName("Tests for updateUser")
    class UpdateUserTests {

        private UserModel updatedDto;

        @BeforeEach
        void setup() {
            updatedDto = new UserModel();
            updatedDto.setFirstName("Update");
            updatedDto.setLastName("Update");
            updatedDto.setPhone("9876543210");
        }

        @Test
        @DisplayName("should update user details and return updated UserDTO")
        void shouldReturnUpdatedUserDTO() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            User result = userService.updateUser(testUser.getId(), updatedDto);

            assertEquals(updatedDto.getFirstName(), result.getFirstName());
            assertEquals(updatedDto.getLastName(), result.getLastName());
            assertEquals(updatedDto.getPhone(), result.getPhone());

            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("should only update provided fields (partial update)")
        void shouldOnlyUpdateProvidedFields() {
            updatedDto.setPhone(null);
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            User result = userService.updateUser(testUser.getId(), updatedDto);

            assertEquals(updatedDto.getFirstName(), result.getFirstName());
            assertEquals(updatedDto.getLastName(), result.getLastName());
            assertEquals("123456789", result.getPhone()); // Should keep old value

            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("should not update nothing if all fields are empty")
        void shouldNotUpdateNothingIfAllFieldsAreEmpty() {
            updatedDto.setFirstName(null);
            updatedDto.setLastName(null);
            updatedDto.setPhone(null);

            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            User result = userService.updateUser(testUser.getId(), updatedDto);

            assertEquals(testUser.getFirstName(), result.getFirstName());
            assertEquals(testUser.getLastName(), result.getLastName());
            assertEquals(testUser.getPhone(), result.getPhone());

            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user to update does not exist")
        void shouldThrowResourceNotFoundExceptionWhenUserToUpdateDoesNotExist() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> userService.updateUser(testUser.getId(), updatedDto));
            assertEquals("User not found with id: " + testUser.getId(), exception.getMessage());
            
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when id is null")
        void shouldThrowIllegalArgumentExceptionWhenIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null, updatedDto));

            verify(userRepository, never()).save(any(User.class));
        }       
    }


    @Nested
    @DisplayName("Tests for changePassword")
    class ChangePasswordTests {

        private String newPassword = "new-password";
        private ChangePasswordDto request;

        @BeforeEach
        void setup() {
            request = new ChangePasswordDto();
            request.setCurrentPassword(testUser.getPasswordHash());
            request.setNewPassword(newPassword);
        }

        @Test
        @DisplayName("should change password when current password matches")
        void shouldChengePasswordIfCurrentPasswordMatch() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), eq(testUser.getPasswordHash()))).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn("newHashedPassword");

            userService.changePassword(testUser.getId(), request);

            verify(passwordEncoder, times(1)).encode(newPassword);
            verify(userRepository, times(1)).save(testUser);
        }

        @Test
        @DisplayName("should throw BadRequestException when current password does not matches")
        void shouldThrowBadRequestExceptionWhenCurrentPasswordDoesNotMatch() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(anyString(), eq(testUser.getPasswordHash()))).thenReturn(false);

            BadRequestException exception = assertThrows(BadRequestException.class, 
                () -> userService.changePassword(testUser.getId(), request));
            assertEquals("Invalid current password", exception.getMessage());

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user does not exist")
        void shouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> userService.changePassword(testUser.getId(), request));
            assertEquals("User not found with id: " + testUser.getId(), exception.getMessage());

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException if request is null")
        void shouldThrowIllegalArgumentExceptionIfRequestIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(testUser.getId(), null));

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("should throw IllegalArgumentException if id is null")
        void shouldThrowIllegalArgumentExceptionIfIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.changePassword(null, request));

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    
    @Nested
    @DisplayName("Tests for deleteUser")
    class DeleteUserTests {
        
        @Test
        @DisplayName("should delete user when user exists")
        void shouldDeleteUserIfHeExists() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

            userService.deleteUser(testUser.getId());

            verify(userRepository, times(1)).delete(testUser);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user to delete does not exists")
        void shouldThrowResourceNotFoundExceptionWhenUserToDeleteDoesNotExist() {
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> userService.deleteUser(testUser.getId()));
            assertEquals("User not found with id: " + testUser.getId(), exception.getMessage());

            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        @DisplayName("should throw IllegalArgumentException if id is null")
        void shouldThrowIllegalArgumentExceptionIfIdIsNull() {
            assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));

            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
