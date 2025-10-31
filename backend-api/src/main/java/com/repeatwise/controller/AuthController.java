package com.repeatwise.controller;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.LogoutResponse;
import com.repeatwise.dto.response.auth.RegisterResponse;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
     * Request Body: RegisterRequest (username optional, email, password, confirmPassword, name optional)
     * Response: 201 Created with RegisterResponse (message, userId)
     *
     * UC-001: User Registration
     *
     * Business Flow:
     * 1. Validate request (@Valid annotation triggers bean validation)
     * 2. Validate confirmPassword matches password
     * 3. Check username uniqueness if provided (case-sensitive)
     * 4. Check email uniqueness (case-insensitive)
     * 5. Hash password with bcrypt
     * 6. Create user with default settings (language: VI, theme: SYSTEM)
     * 7. Return register response with message and userId
     *
     * Error Responses:
     * - 400 BAD_REQUEST: Validation error (password mismatch, invalid format, email/username already exists)
     *
     * @param request RegisterRequest
     * @return ResponseEntity with RegisterResponse
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody final RegisterRequest request) {

        log.info("event={} Received registration request: username={}, email={}",
            LogEvent.AUTH_REGISTER_START, request.getUsername(), request.getEmail());

        final RegisterResponse response = authService.register(request);

        log.info("event={} User registered successfully: userId={}, username={}, email={}",
            LogEvent.AUTH_REGISTER_SUCCESS, response.getUserId(), request.getUsername(), request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user with username or email
     *
     * Endpoint: POST /api/auth/login
     * Request Body: LoginRequest (usernameOrEmail, password)
     * Response: 200 OK with LoginResponse (accessToken, expiresIn, user)
     * Cookie: refresh_token (HttpOnly, Secure, SameSite=Strict)
     *
     * UC-002: User Login
     *
     * Business Flow:
     * 1. Validate request (@Valid annotation triggers bean validation)
     * 2. Auto-detect if input is email (contains @) or username
     * 3. Find user by username (case-sensitive) or email (case-insensitive)
     * 4. Verify password with bcrypt
     * 5. Generate JWT access token (15-minute expiry)
     * 6. Generate refresh token (7-day expiry)
     * 7. Save refresh token to database
     * 8. Set refresh token in HttpOnly cookie
     * 9. Return login response with access token and user info
     *
     * Error Responses:
     * - 400 BAD_REQUEST: Validation error
     * - 401 UNAUTHORIZED: Invalid credentials
     *
     * @param request LoginRequest
     * @param response HttpServletResponse for setting cookie
     * @return ResponseEntity with LoginResponse
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody final LoginRequest request,
            final HttpServletResponse response) {

        log.info("event={} Received login request: usernameOrEmail={}",
            LogEvent.AUTH_LOGIN_START, request.getUsernameOrEmail());

        final LoginResponse loginResponse = authService.login(request);

        // Set refresh token in HttpOnly cookie
        // UC-002: Cookie configuration (HttpOnly, Secure, SameSite=Strict, Max-Age=604800, Path=/api/auth)
        final ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS only in production
                .sameSite("Strict")
                .maxAge(604800) // 7 days in seconds
                .path("/api/auth")
                .build();

        log.info("event={} Login successful: accessToken generated, expiresIn={} seconds, userId={}",
            LogEvent.AUTH_LOGIN_SUCCESS, loginResponse.getExpiresIn(), 
            loginResponse.getUser() != null ? loginResponse.getUser().getId() : null);

        // Remove refreshToken from response body (security - only in cookie)
        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponse);
    }

    /**
     * Refresh access token using refresh token (token rotation)
     *
     * Endpoint: POST /api/auth/refresh
     * Cookie: refresh_token (HttpOnly)
     * Response: 200 OK with LoginResponse (accessToken, expiresIn)
     *
     * Business Flow:
     * 1. Extract refresh token from cookie
     * 2. Find refresh token in database
     * 3. Validate token (not expired, not revoked)
     * 4. Generate new access token (15-minute expiry)
     * 5. Generate new refresh token (token rotation)
     * 6. Revoke old refresh token
     * 7. Save new refresh token
     * 8. Set new refresh token in HttpOnly cookie
     * 9. Return new access token and expires_in
     *
     * Use Case: UC-003 - Refresh Access Token
     *
     * Error Responses:
     * - 401 UNAUTHORIZED: Token invalid/expired/revoked
     *
     * @param refreshToken Refresh token from HttpOnly cookie
     * @return ResponseEntity with LoginResponse
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) final String refreshToken) {

        log.info("event={} Received refresh token request", LogEvent.AUTH_TOKEN_REFRESH);

        final LoginResponse loginResponse = authService.refreshToken(refreshToken);

        // Set new refresh token in HttpOnly cookie (token rotation)
        // UC-002: Cookie configuration (HttpOnly, Secure, SameSite=Strict, Max-Age=604800, Path=/api/auth)
        final ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS only in production
                .sameSite("Strict")
                .maxAge(604800) // 7 days in seconds
                .path("/api/auth")
                .build();

        log.info("event={} Token refreshed successfully: expiresIn={} seconds",
                LogEvent.AUTH_TOKEN_REFRESH, loginResponse.getExpiresIn());

        // Remove refreshToken from response body (security - only in cookie)
        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponse);
    }

    /**
     * Logout user from current device
     * UC-004: User Logout
     *
     * Endpoint: POST /api/auth/logout
     * Cookie: refresh_token (HttpOnly)
     * Response: 200 OK with LogoutResponse (message)
     *
     * Business Flow:
     * 1. Extract refresh token from cookie (optional)
     * 2. Get current authenticated user ID
     * 3. Revoke refresh token if present and valid (idempotent)
     * 4. Clear refresh token cookie
     * 5. Return 200 OK with success message
     *
     * UC-004: Logout is idempotent - always succeeds even if token invalid/missing
     *
     * Error Responses:
     * - None (logout always succeeds for better UX)
     *
     * @param refreshToken Refresh token from HttpOnly cookie (optional)
     * @return ResponseEntity with LogoutResponse
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @CookieValue(name = "refresh_token", required = false) final String refreshToken) {

        log.info("event={} Received logout request", LogEvent.AUTH_TOKEN_REVOKE);

        final UUID userId = SecurityUtils.getCurrentUserId();
        final LogoutResponse logoutResponse = authService.logout(refreshToken, userId);

        // UC-004: Clear refresh token cookie (Max-Age=0)
        final ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0) // Immediately expire
                .path("/api/auth")
                .build();

        log.info("event={} Logout successful: userId={}", LogEvent.AUTH_TOKEN_REVOKE, userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(logoutResponse);
    }

}
