package com.auth.assembler;

import com.auth.model.AuthModel;
import com.auth.model.AuthView;
import com.auth.model.mapper.AuthMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("com.auth") // To solve missing bean (AuthMapper) error
public class AuthModelAssembler {

    private final AuthMapper authMapper;
    private final UserModelAssembler userModelAssembler;

    public AuthModelAssembler(AuthMapper authMapper, UserModelAssembler userModelAssembler) {
        this.authMapper = authMapper;
        this.userModelAssembler = userModelAssembler;
    }
    
    public AuthModel toModel(AuthView view) {
        AuthModel model = authMapper.toModel(view);
        model.setUser(userModelAssembler.toModel(view.getUser()));
        return model;
    }

}
