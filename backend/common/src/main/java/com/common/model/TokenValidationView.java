package com.common.model;

import com.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenValidationView {
    private Boolean valid;
    private Long userId;
    private String email;
    private UserRole role;
    private String message;
}
