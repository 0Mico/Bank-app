package com.auth.assembler;

import com.common.model.TokenValidationModel;
import com.common.model.TokenValidationView;
import com.auth.model.mapper.TokenValidationMapper;
import org.springframework.stereotype.Component;

@Component
public class TokenValidationModelAssembler {

    private final TokenValidationMapper tokenValidationMapper;

    public TokenValidationModelAssembler(TokenValidationMapper tokenValidationMapper) {
        this.tokenValidationMapper = tokenValidationMapper;
    }

    public TokenValidationModel toModel(TokenValidationView view) {
        return tokenValidationMapper.toModel(view);
    }
}
