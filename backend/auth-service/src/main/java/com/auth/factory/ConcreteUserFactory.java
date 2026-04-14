package com.auth.factory;

import com.auth.dto.RegisterDto;
import com.auth.entity.User;
import com.auth.model.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Default implementation of UserFactory.
 * Maps RegisterDto into a User entity using MapStruct.
 */
@Component
@RequiredArgsConstructor
public class ConcreteUserFactory implements UserFactory {

    private final UserMapper userMapper;

    @Override
    public User create(RegisterDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Registration DTO cannot be null");
        }
        return userMapper.dtoToEntity(dto);
    }
}
