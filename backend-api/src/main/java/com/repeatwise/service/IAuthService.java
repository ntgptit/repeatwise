package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.exception.DuplicateEmailException;
import com.repeatwise.exception.DuplicateUsernameException;
import com.repeatwise.exception.ForbiddenException;
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
     * Business Logic:
     * 1. Detect if input is email (contains @) or username
     * 2. Find user by username or email (case-insensitive)
     * 3. Verify password with bcrypt
     * 4. Generate JWT access token (15-minute expiry)
     * 5. Generate refresh token (7-day expiry)
     * 6. Return login response with access token
     *
     * @param request LoginRequest containing usernameOrEmail and password
     * @return LoginResponse with accessToken and expiresIn
     * @throws InvalidCredentialsException if username/email or password is invalid
     */
    LoginResponse login(LoginRequest request);

    /**
     * Logout user from current device (revoke single refresh token)
     *
     * Business Logic:
     * 1. Validate refresh token is not blank
     * 2. Find refresh token in database
     * 3. Verify token belongs to current user (authorization)
     * 4. Revoke refresh token (soft delete - set isRevoked=true)
     * 5. Log logout event
     *
     * Note: Access token remains valid until expiry (client-side cleanup)
     *
     * @param refreshToken Refresh token string from HttpOnly cookie
     * @param userId       Current authenticated user ID
     * @throws InvalidTokenException if token not found or invalid
     * @throws ForbiddenException    if token doesn't belong to user
     */
    void logout(String refreshToken, UUID userId);

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
     * Business Logic:
     * 1. Validate username uniqueness (case-insensitive)
     * 2. Validate email uniqueness (case-insensitive)
     * 3. Hash password with bcrypt (cost 12)
     * 4. Create user with default settings
     * 5. Create default SRS settings
     * 6. Create default user stats
     * 7. Return user response
     *
     * @param request RegisterRequest containing username, email, password, name
     * @return UserResponse with user details
     * @throws DuplicateUsernameException if username already exists
     * @throws DuplicateEmailException    if email already exists
     */
    UserResponse register(RegisterRequest request);
}
