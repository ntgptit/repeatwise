package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.LogoutResponse;
import com.repeatwise.dto.response.auth.RegisterResponse;
import com.repeatwise.exception.DuplicateEmailException;
import com.repeatwise.exception.DuplicateUsernameException;
import com.repeatwise.exception.InvalidCredentialsException;
import com.repeatwise.exception.InvalidTokenException;

/**
 * Authentication Service Interface
 *
 * Requirements:
 * - UC-001: User Registration
 * - UC-002: User Login
 * - UC-003: User Logout
 *
 * @author RepeatWise Team
 */
public interface IAuthService {

    /**
     * Login user with username or email
     *
     * UC-002: User Login
     *
     * Business Logic:
     * 1. Detect if input is email (contains @) or username
     * 2. Find user by username (case-sensitive) or email (case-insensitive)
     * 3. Verify password with bcrypt
     * 4. Generate JWT access token (15-minute expiry) with userId, email, username
     * 5. Generate refresh token (7-day expiry)
     * 6. Save refresh token to database
     * 7. Return login response with access token and user info
     *
     * @param request LoginRequest containing usernameOrEmail and password
     * @return LoginResponse with accessToken, expiresIn, and user object
     * @throws InvalidCredentialsException if username/email or password is invalid
     */
    LoginResponse login(LoginRequest request);

    /**
     * Logout user from current device (revoke single refresh token)
     * UC-004: User Logout - Idempotent operation
     *
     * Business Logic:
     * 1. If refresh token is blank - return success (nothing to revoke)
     * 2. Try to find refresh token in database
     * 3. If token not found - return success (idempotent)
     * 4. If token already revoked - return success (idempotent)
     * 5. Verify token belongs to current user (authorization)
     * 6. Revoke refresh token (soft delete - set isRevoked=true)
     * 7. Log logout event
     *
     * UC-004: Logout should always succeed from UX perspective (graceful degradation)
     *
     * @param refreshToken Refresh token string from HttpOnly cookie (can be null/blank)
     * @param userId       Current authenticated user ID
     * @return LogoutResponse with success message
     */
    LogoutResponse logout(String refreshToken, UUID userId);

    /**
     * Refresh access token using refresh token (token rotation)
     *
     * Business Logic:
     * 1. Extract refresh token from cookie
     * 2. Find refresh token in database
     * 3. Validate token (not expired, not revoked, belongs to user)
     * 4. Generate new access token (15 minutes expiry)
     * 5. Generate new refresh token (token rotation)
     * 6. Revoke old refresh token
     * 7. Save new refresh token
     * 8. Return new access token and expires_in
     *
     * Use Case: UC-003 - Refresh Access Token
     *
     * @param refreshToken Refresh token string from HttpOnly cookie
     * @return LoginResponse with new accessToken and expiresIn
     * @throws InvalidTokenException if token not found, expired, or revoked
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * Register new user account
     *
     * UC-001: User Registration
     *
     * Business Logic:
     * 1. Validate confirmPassword matches password
     * 2. Validate username format if provided (3-30 chars, alphanumeric + underscore/hyphen)
     * 3. Validate username uniqueness if provided (case-sensitive)
     * 4. Validate email uniqueness (case-insensitive)
     * 5. Hash password with bcrypt (cost 12)
     * 6. Create user with default settings (language: VI, theme: SYSTEM)
     * 7. Create default SRS settings
     * 8. Create default user stats
     * 9. Return register response with message and userId
     *
     * @param request RegisterRequest containing username (optional), email, password, confirmPassword, name (optional)
     * @return RegisterResponse with message and userId
     * @throws DuplicateUsernameException if username already exists
     * @throws DuplicateEmailException    if email already exists
     */
    RegisterResponse register(RegisterRequest request);
}
