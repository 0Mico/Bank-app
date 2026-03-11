package com.bankapp.auth.dtos;

import com.bankapp.common.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private UserDTO user;

}
