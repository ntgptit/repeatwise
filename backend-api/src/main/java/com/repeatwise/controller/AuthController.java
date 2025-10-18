package com.repeatwise.controller;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
     * Response: 200 OK with LoginResponse (accessToken, expiresIn)
     *
     * Business Flow:
     * 1. Validate request (@Valid annotation triggers bean validation)
     * 2. Auto-detect if input is email (contains @) or username
     * 3. Find user by username or email (case-insensitive)
     * 4. Verify password with bcrypt
     * 5. Generate JWT access token (15-minute expiry)
     * 6. Return login response with access token
     *
     * Error Responses:
     * - 400 BAD_REQUEST: Validation error
     * - 401 UNAUTHORIZED: Invalid credentials
     *
     * @param request LoginRequest
     * @return ResponseEntity with LoginResponse
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody final LoginRequest request) {

        log.info("Received login request: usernameOrEmail={}",
            request.getUsernameOrEmail());

        final LoginResponse response = authService.login(request);

        log.info("Login successful: accessToken generated, expiresIn={} seconds",
            response.getExpiresIn());

        return ResponseEntity.ok(response);
    }

    /**
     * Logout user from current device
     *
     * Endpoint: POST /api/auth/logout
     * Cookie: refresh_token (HttpOnly)
     * Response: 204 No Content
     *
     * Business Flow:
     * 1. Extract refresh token from cookie
     * 2. Get current authenticated user ID
     * 3. Validate token belongs to user
     * 4. Revoke refresh token (soft delete)
     * 5. Return 204 No Content
     *
     * Error Responses:
     * - 401 UNAUTHORIZED: Token invalid/expired
     * - 403 FORBIDDEN: Token doesn't belong to user
     *
     * @param refreshToken Refresh token from HttpOnly cookie
     * @return ResponseEntity with no content
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refresh_token", required = false) final String refreshToken) {

        log.info("Received logout request");

        final UUID userId = SecurityUtils.getCurrentUserId();
        authService.logout(refreshToken, userId);

        log.info("Logout successful: userId={}", userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Logout user from all devices
     *
     * Endpoint: POST /api/auth/logout-all
     * Authorization: Bearer <access_token>
     * Response: 204 No Content
     *
     * Business Flow:
     * 1. Get current authenticated user ID from JWT
     * 2. Find all valid refresh tokens for user
     * 3. Revoke all tokens (bulk update)
     * 4. Return 204 No Content
     *
     * Use Case:
     * - User suspects account compromise
     * - User wants to logout from all devices
     *
     * Error Responses:
     * - 401 UNAUTHORIZED: Invalid or missing access token
     *
     * @return ResponseEntity with no content
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll() {

        log.info("Received logout-all request");

        final UUID userId = SecurityUtils.getCurrentUserId();
        authService.logoutAll(userId);

        log.info("Logout-all successful: userId={}", userId);

        return ResponseEntity.noContent().build();
    }
}
