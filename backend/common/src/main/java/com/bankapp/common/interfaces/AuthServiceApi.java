package com.bankapp.common.interfaces;

import com.bankapp.common.dto.TokenValidationRequest;
import com.bankapp.common.dto.TokenValidationResponse;
import com.bankapp.common.dto.UserDTO;

public interface AuthServiceApi {
    TokenValidationResponse validateToken(TokenValidationRequest request);
    UserDTO getUserById(Long id);
}
