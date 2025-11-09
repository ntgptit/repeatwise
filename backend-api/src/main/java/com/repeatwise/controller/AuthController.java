package com.repeatwise.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.AuthResponse;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration, login, logout, and token refresh.
 */
@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     * Creates user with default settings (SRS settings, user stats).
     *
     * @param registerRequest Registration data (email, username, password, confirmPassword, name)
     * @return Response with success message and user ID
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new user account with email, optional username, and password. "
            +
            "Creates user profile with default settings (SRS settings, user stats).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Bad request - Email/username already exists or validation error", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for email: {}", registerRequest.getEmail());

        final var userId = this.authService.register(registerRequest);

        final Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful. Please login.");
        response.put("userId", userId.toString());

        log.info("User registered successfully with ID: {}", userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user with username/email and password.
     * Returns JWT access token and user information.
     *
     * @param loginRequest Login credentials (identifier, password)
     * @return AuthResponse with access token and user data
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user with username or email and password. " +
            "Returns JWT access token in response body and sets refresh token in HTTP-only cookie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        log.info("Login request received for identifier: {}", loginRequest.getIdentifier());

        // Perform login and get response with refresh token
        final var loginResponse = this.authService.login(loginRequest);

        // Set refresh token in HTTP-only cookie
        final var refreshTokenCookie = new Cookie("refresh_token", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true); // Not accessible by JavaScript (XSS protection)
        refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
        refreshTokenCookie.setPath("/api/auth"); // Restrict to auth endpoints only
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days in seconds
        refreshTokenCookie.setAttribute("SameSite", "Strict"); // CSRF protection
        response.addCookie(refreshTokenCookie);

        log.info("User logged in successfully, refresh token set in cookie");
        return ResponseEntity.ok(loginResponse.getAuthResponse());
    }

    /**
     * Logout user by revoking all refresh tokens.
     * Gets authenticated user from SecurityContext (JWT token).
     *
     * @param user Authenticated user from SecurityContext
     * @return Success message
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout authenticated user by revoking all refresh tokens. " +
            "User is identified from JWT token in Authorization header.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not logged in", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> logout(
            @AuthenticationPrincipal User user,
            HttpServletResponse response) {
        log.info("Logout request received for user: {}", user.getId());

        this.authService.logout(user.getId());

        // Clear refresh token cookie
        final var refreshTokenCookie = new Cookie("refresh_token", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
        refreshTokenCookie.setPath("/api/auth");
        refreshTokenCookie.setMaxAge(0); // Expire immediately
        refreshTokenCookie.setAttribute("SameSite", "Strict");
        response.addCookie(refreshTokenCookie);

        final Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout successful");

        log.info("User logged out successfully, refresh token cookie cleared: {}", user.getId());
        return ResponseEntity.ok(responseBody);
    }

    /**
     * Refresh access token using refresh token from HTTP-only cookie.
     * Implements token rotation: old refresh token is revoked, new one is generated.
     *
     * @param request  HTTP request to extract refresh token from cookie
     * @param response HTTP response to set new refresh token cookie
     * @return Response with new access token and expiry time
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = """
            Refresh access token using refresh token from HTTP-only cookie. \
            Implements token rotation for security: old refresh token is revoked, \
            and a new refresh token is generated and set in HTTP-only cookie.""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid, expired, revoked, or missing refresh token", content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.info("Token refresh request received");

        // Extract refresh token from HTTP-only cookie
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (final Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // Throw exception if refresh token not found
        if (refreshToken == null) {
            log.warn("Refresh token missing from cookie");
            throw new RepeatWiseException(RepeatWiseError.REFRESH_TOKEN_MISSING);
        }

        // Extract device info and IP address from request
        final var deviceInfo = request.getHeader("User-Agent");
        final var ipAddress = request.getRemoteAddr();

        // Refresh access token (with token rotation)
        final var refreshTokenResponse = this.authService.refreshAccessToken(
                refreshToken, null, deviceInfo, ipAddress);

        // Set new refresh token in HTTP-only cookie
        final var newRefreshTokenCookie = new Cookie("refresh_token", refreshTokenResponse.getRefreshToken());
        newRefreshTokenCookie.setHttpOnly(true); // Not accessible by JavaScript (XSS protection)
        newRefreshTokenCookie.setSecure(false); // Set to true in production with HTTPS
        newRefreshTokenCookie.setPath("/api/auth"); // Restrict to auth endpoints only
        newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days in seconds
        newRefreshTokenCookie.setAttribute("SameSite", "Strict"); // CSRF protection
        response.addCookie(newRefreshTokenCookie);

        // Build response (do not include refresh token in body - it's in cookie)
        final Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("access_token", refreshTokenResponse.getAccessToken());
        responseBody.put("expires_in", refreshTokenResponse.getExpiresIn());

        log.info("Access token refreshed successfully, new refresh token set in cookie");
        return ResponseEntity.ok(responseBody);
    }
}
