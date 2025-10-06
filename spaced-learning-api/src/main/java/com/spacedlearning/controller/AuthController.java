package com.spacedlearning.controller;

import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.service.AuditService;
import com.spacedlearning.service.AuthService;
import com.spacedlearning.service.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication API", description = "Endpoints for authentication")
public class AuthController {

    public static final String INVALID_TOKEN = "Invalid token";
    public static final String REGISTERS_A_NEW_USER = "Registers a new user";
    public static final String TOKEN_IS_VALID = "Token is valid";
    public static final String RATE_LIMIT_EXCEEDED = "Too many registration attempts. Please try again later.";
    
    private final AuthService authService;
    private final RateLimitService rateLimitService;
    private final AuditService auditService;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user with username or email and returns JWT tokens")
    public ResponseEntity<DataResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        log.debug("REST request to login user with username or email: {}", request.getUsernameOrEmail());
        final AuthResponse authResponse = authService.authenticate(request);
        return ResponseEntity.ok(DataResponse.of(authResponse));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh token", description = "Refreshes an authentication token")
    public ResponseEntity<DataResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("REST request to refresh token");
        final AuthResponse authResponse = authService.refreshToken(request);
        return ResponseEntity.ok(DataResponse.of(authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = REGISTERS_A_NEW_USER)
    public ResponseEntity<DataResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String realIp) {
        
        // Get client IP address
        String clientIp = getClientIpAddress(forwardedFor, realIp);
        
        // Check rate limiting
        if (!rateLimitService.isRegistrationAllowed(clientIp)) {
            log.warn("Registration rate limit exceeded for IP: {}", clientIp);
            throw SpacedLearningException.tooManyRequests(RATE_LIMIT_EXCEEDED);
        }
        
        log.debug("REST request to register user with email: {} from IP: {}", request.getEmail(), clientIp);
        
        try {
            final UserResponse registeredUser = authService.register(request);
            // Record successful registration attempt
            rateLimitService.recordRegistrationAttempt(clientIp);
            auditService.logRegistrationAttempt(request.getEmail(), clientIp, true, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(registeredUser));
        } catch (Exception e) {
            // Record failed registration attempt
            rateLimitService.recordRegistrationAttempt(clientIp);
            auditService.logRegistrationAttempt(request.getEmail(), clientIp, false, e.getMessage());
            throw e;
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Validates a JWT token")
    public ResponseEntity<SuccessResponse> validateToken(@RequestParam String token) {
        log.debug("REST request to validate token");
        final boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(SuccessResponse.of(TOKEN_IS_VALID));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(SuccessResponse.builder().message(INVALID_TOKEN).success(false).build());
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verifies user email using verification token")
    public ResponseEntity<SuccessResponse> verifyEmail(
            @RequestParam String token,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String realIp) {
        
        String clientIp = getClientIpAddress(forwardedFor, realIp);
        log.debug("REST request to verify email with token: {} from IP: {}", token, clientIp);
        
        final boolean isVerified = authService.verifyEmail(token);
        if (isVerified) {
            auditService.logEmailVerificationAttempt(token, clientIp, true, null);
            return ResponseEntity.ok(SuccessResponse.of("Email verified successfully"));
        }
        
        auditService.logEmailVerificationAttempt(token, clientIp, false, "Invalid or expired verification token");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(SuccessResponse.builder().message("Invalid or expired verification token").success(false).build());
    }

    /**
     * Get client IP address from request headers
     * @param forwardedFor X-Forwarded-For header value
     * @param realIp X-Real-IP header value
     * @return client IP address
     */
    private String getClientIpAddress(String forwardedFor, String realIp) {
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return forwardedFor.split(",")[0].trim();
        }
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return "unknown";
    }
}