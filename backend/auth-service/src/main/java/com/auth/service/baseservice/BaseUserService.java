package com.auth.service.baseservice;

import com.auth.dto.ChangePasswordDto;
import com.auth.entity.User;
import com.auth.model.UserModel;
import com.auth.repository.UserRepository;
import com.common.interfaces.BaseService;

public interface BaseUserService extends BaseService<UserRepository, User, Long> {
    boolean checkIfUserExists(Long userId);
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByIban(String iban);
    User updateUser(Long id, UserModel userDTO);
    void deleteUser(Long id);
    void changePassword(Long id, ChangePasswordDto request);
}
