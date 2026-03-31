package com.auth.model;

import com.auth.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthView {
    private String token;
    private User user;
}
