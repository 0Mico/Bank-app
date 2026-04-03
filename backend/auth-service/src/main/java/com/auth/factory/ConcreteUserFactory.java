package com.auth.factory;

import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.common.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Default implementation of UserFactory.
 * Maps RegisterDto into a User entity.
 */

@Component
public class ConcreteUserFactory implements UserFactory {

    private final PasswordEncoder passwordEncoder;

    public ConcreteUserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(RegisterDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Registration DTO cannot be null");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(UserRole.USER);
        
        return user;
    }
}
