package com.repeatwise.service;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.RefreshTokenResponse;
import com.repeatwise.exception.RepeatWiseException;

import java.util.UUID;

/**
 * Service interface for authentication operations including registration and login.
 */
public interface AuthService {

    /**
     * Register a new user with email, optional username, and password.
     * Creates user account with default settings (SrsSettings, UserStats).
     *
     * @param registerRequest Registration data (email, username, password, confirmPassword, name)
     * @return UUID of the newly created user
     * @throws RepeatWiseException if email/username already exists or passwords do not match
     */
    UUID register(RegisterRequest registerRequest);

    /**
     * Authenticate user with username/email and password.
     * Returns JWT access token and refresh token.
     *
     * @param loginRequest Login credentials (identifier, password)
     * @return LoginResponse with access token, user data, and refresh token
     * @throws RepeatWiseException if user not found or password is incorrect
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * Logout user by revoking all refresh tokens.
     *
     * @param userId User ID to logout
     */
    void logout(UUID userId);

    /**
     * Refresh access token using refresh token (with token rotation).
     *
     * @param refreshToken Refresh token string
     * @param deviceId Optional device ID for tracking
     * @param deviceInfo Optional device information
     * @param ipAddress Optional IP address
     * @return RefreshTokenResponse with new access token and new refresh token
     * @throws RepeatWiseException if refresh token is missing, invalid, expired, revoked, or reused
     */
    RefreshTokenResponse refreshAccessToken(String refreshToken, String deviceId,
            String deviceInfo, String ipAddress);
}
