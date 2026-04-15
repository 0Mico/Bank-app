package com.auth.controller;

import com.auth.assembler.UserModelAssembler;
import com.auth.dto.ChangePasswordDto;
import com.auth.entity.User;
import com.auth.service.UserService;
import com.auth.model.UserModel;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;

    public UserController(UserService userService, UserModelAssembler userModelAssembler) {
        this.userService = userService;
        this.userModelAssembler = userModelAssembler;
    }

    @GetMapping("/email")
    public ResponseEntity<UserModel> getUserByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @GetMapping("/iban")
    // Used to show the name of the external account when clicking on a transaction
    public ResponseEntity<UserModel> getUserByIban(@RequestParam String iban) {
        User user = userService.getUserByIban(iban);
        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel userDTO) {
        User user = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(userModelAssembler.toModel(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDto request) {
        userService.changePassword(id, request);
        return ResponseEntity.ok().build();
    }
}
