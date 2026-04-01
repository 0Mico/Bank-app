package com.auth.assembler;

import org.springframework.stereotype.Component;

import com.auth.entity.User;
import com.auth.model.mapper.UserMapper;
import com.auth.model.UserModel;

@Component
public class UserModelAssembler {

    private final UserMapper userMapper;

    public UserModelAssembler(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public UserModel toModel(User user) {
        return userMapper.toModel(user);
    }
    
}
