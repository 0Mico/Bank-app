package com.auth.controller;

import com.auth.assembler.AuthModelAssembler;
import com.auth.dto.LoginDto;
import com.auth.dto.RegisterDto;
import com.auth.model.AuthModel;
import com.auth.model.AuthView;
import com.auth.service.AuthService;
import com.auth.assembler.TokenValidationModelAssembler;
import com.common.dto.TokenValidationDTO;
import com.common.model.TokenValidationModel;
import com.common.model.TokenValidationView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthModelAssembler authModelAssembler;
    private final TokenValidationModelAssembler tokenValidationModelAssembler;

    public AuthController(AuthService authService, AuthModelAssembler authModelAssembler, TokenValidationModelAssembler tokenValidationModelAssembler) {
        this.authService = authService;
        this.authModelAssembler = authModelAssembler;
        this.tokenValidationModelAssembler = tokenValidationModelAssembler;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthModel> register(@Valid @RequestBody RegisterDto request) {
        AuthView view = authService.register(request);
        return ResponseEntity.ok(authModelAssembler.toModel(view));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthModel> login(@Valid @RequestBody LoginDto request) {
        AuthView view = authService.login(request);
        return ResponseEntity.ok(authModelAssembler.toModel(view));
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationModel> validateToken(@RequestBody TokenValidationDTO request) {
        TokenValidationView view = authService.validateToken(request);
        return ResponseEntity.ok(tokenValidationModelAssembler.toModel(view));
    }
}
