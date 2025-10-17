package com.repeatwise.controller;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 *
 * Requirements:
 * - UC-001: User Registration - POST /api/auth/register
 * - UC-002: User Login - POST /api/auth/login
 * - API Endpoints Summary: Authentication endpoints
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;

    /**
     * Register new user
     *
     * Endpoint: POST /api/auth/register
     * Request Body: RegisterRequest (username, email, password, name)
     * Response: 201 Created with UserResponse
     *
     * Business Flow:
     * 1. Validate request (@Valid annotation triggers bean validation)
     * 2. Check username uniqueness
     * 3. Check email uniqueness
     * 4. Hash password with bcrypt
     * 5. Create user with default settings
     * 6. Return user response
     *
     * Error Responses:
     * - 400 BAD_REQUEST: Validation error
     * - 409 CONFLICT: Username or email already exists
     *
     * @param request RegisterRequest
     * @return ResponseEntity with UserResponse
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody final RegisterRequest request) {

        log.info("Received registration request: username={}, email={}",
            request.getUsername(), request.getEmail());

        final UserResponse response = authService.register(request);

        log.info("User registered successfully: userId={}, username={}, email={}",
            response.getId(), response.getUsername(), response.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user with username or email
     *
     * Endpoint: POST /api/auth/login
     * Request Body: LoginRequest (usernameOrEmail, password)
     * Response: 200 OK with UserResponse
     *
     * Business Flow:
     * 1. Validate request (@Valid annotation triggers bean validation)
     * 2. Auto-detect if input is email (contains @) or username
     * 3. Find user by username or email (case-insensitive)
     * 4. Verify password with bcrypt
     * 5. Return user response
     *
     * Error Responses:
     * - 400 BAD_REQUEST: Validation error
     * - 401 UNAUTHORIZED: Invalid credentials
     *
     * @param request LoginRequest
     * @return ResponseEntity with UserResponse
     */
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody final LoginRequest request) {

        log.info("Received login request: usernameOrEmail={}",
            request.getUsernameOrEmail());

        final UserResponse response = authService.login(request);

        log.info("User logged in successfully: userId={}, username={}",
            response.getId(), response.getUsername());

        return ResponseEntity.ok(response);
    }
}
