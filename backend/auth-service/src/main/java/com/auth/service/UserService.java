package com.auth.service;

import com.auth.client.AccountServiceClient;
import com.auth.dto.ChangePasswordDto;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.baseservice.BaseUserService;
import com.common.model.AccountModel;
import com.common.exception.BadRequestException;
import com.common.exception.ResourceNotFoundException;
import com.auth.model.UserModel;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements BaseUserService {

    private final UserRepository userRepository;
    private final AccountServiceClient accountServiceClient;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }

    @Override
    public boolean checkIfUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    // Used to show the name of the external account when clicking on a transaction
    @Override
    public User getUserByIban(String iban) {
        if (iban == null || iban.length() > 34) {
            throw new IllegalArgumentException("Null or too long iban");
        }
        AccountModel account = accountServiceClient.getAccountByIban(iban);
        Long userId = account.getUserId();
        return getUserById(userId);
    }

    @Override
    public User updateUser(Long id, UserModel userDTO) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (userDTO.getFirstName() != null)
            user.setFirstName(userDTO.getFirstName());
        if (userDTO.getLastName() != null)
            user.setLastName(userDTO.getLastName());
        if (userDTO.getPhone() != null)
            user.setPhone(userDTO.getPhone());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userRepository.delete(user);
    }

    @Override
    public void changePassword(Long id, ChangePasswordDto request) {
        if (id == null || request == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid current password");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}