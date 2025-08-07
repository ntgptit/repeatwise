package com.repeatwise.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.AuthRequestDto;
import com.repeatwise.dto.AuthResponseDto;
import com.repeatwise.dto.UserDto;
import com.repeatwise.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves information about the current authenticated user")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("Authorization") String token) {
        final var user = this.authService.getCurrentUser(token);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user with email and password")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto loginRequest) {
        final var response = this.authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logs out the current user")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        this.authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Registers a new user")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRequestDto registerRequest) {
        final var response = this.authService.register(
                registerRequest.getName(),
                registerRequest.getEmail(),
                registerRequest.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
