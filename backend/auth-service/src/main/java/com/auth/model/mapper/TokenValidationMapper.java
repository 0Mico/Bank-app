package com.auth.model.mapper;

import com.common.model.TokenValidationModel;
import com.common.model.TokenValidationView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenValidationMapper {

    TokenValidationModel toModel(TokenValidationView view);
}
