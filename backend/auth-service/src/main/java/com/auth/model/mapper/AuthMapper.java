package com.auth.model.mapper;

import org.mapstruct.Mapper;

import com.auth.model.AuthModel;
import com.auth.model.AuthView;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthModel toModel(AuthView view);
}
