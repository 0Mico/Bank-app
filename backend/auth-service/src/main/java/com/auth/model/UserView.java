package com.auth.model;

import com.common.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserView {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;
}
