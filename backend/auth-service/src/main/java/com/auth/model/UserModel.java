package com.auth.model;

import com.common.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;
}
