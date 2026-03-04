package com.bankapp.auth.service;

import com.bankapp.auth.client.AccountServiceClient;
import com.bankapp.auth.entity.User;
import com.bankapp.auth.repository.UserRepository;
import com.bankapp.common.dto.ChangePasswordRequest;
import com.bankapp.common.dto.UserDTO;
import com.bankapp.common.dto.AccountDTO;
import com.bankapp.common.exception.BadRequestException;
import com.bankapp.common.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountServiceClient accountServiceClient;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AccountServiceClient accountServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountServiceClient = accountServiceClient;
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return AuthService.toDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return AuthService.toDTO(user);
    }

    public UserDTO getUserByIban(String iban) {
        AccountDTO account = accountServiceClient.getAccountByIban(iban);
        return getUserById(account.getUserId());
    }

    public UserDTO getUserByAccountId(Long accountId) {
        AccountDTO account = accountServiceClient.getAccountById(accountId);
        return getUserById(account.getUserId());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AuthService::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (userDTO.getFirstName() != null)
            user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null)
            user.setLastName(userDTO.getLastName());
        if (userDTO.getPhone() != null)
            user.setPhone(userDTO.getPhone());

        user = userRepository.save(user);
        return AuthService.toDTO(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(user);
    }

    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
