package com.bankapp.common.dto;

import com.bankapp.common.enums.UserRole;

public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private UserRole role;
    private String message;

    public TokenValidationResponse() {}
    public TokenValidationResponse(boolean valid, Long userId, String email, UserRole role) {
        this.valid = valid;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public static TokenValidationResponse invalid(String message) {
        TokenValidationResponse response = new TokenValidationResponse();
        response.setValid(false);
        response.setMessage(message);
        return response;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
