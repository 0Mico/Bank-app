package com.auth.model.mapper;
import org.mapstruct.Mapper;

import com.auth.entity.User;
import com.auth.model.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel toModel(User user);
}