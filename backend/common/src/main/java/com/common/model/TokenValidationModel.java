package com.common.model;

import com.common.enums.UserRole;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationModel {
    private boolean valid;
    private Long userId;
    private String email;
    private UserRole role;
    private String message;
}
