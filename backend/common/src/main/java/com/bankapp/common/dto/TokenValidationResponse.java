package com.bankapp.common.dto;

import com.bankapp.common.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private UserRole role;
    private String message;

    public static TokenValidationResponse invalid(String message) {
        TokenValidationResponse response = new TokenValidationResponse();
        response.setValid(false);
        response.setMessage(message);
        return response;
    }
}
