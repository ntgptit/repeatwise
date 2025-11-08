package com.repeatwise.service;

import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseException;

/**
 * Service interface for refresh token management.
 * Handles creation, validation, and revocation of refresh tokens.
 */
public interface RefreshTokenService {

    /**
     * Generate and save a new refresh token for user.
     * Token is hashed with BCrypt before storing in database.
     * Expires in 7 days.
     *
     * @param user User to generate token for
     * @param deviceId Optional device ID for tracking
     * @param deviceInfo Optional device information
     * @param ipAddress Optional IP address
     * @return Generated refresh token (plain text, not hashed)
     */
    String createRefreshToken(User user, String deviceId, String deviceInfo, String ipAddress);

    /**
     * Validate refresh token and return associated RefreshToken entity.
     *
     * @param token Refresh token (plain text)
     * @return RefreshToken entity if valid
     * @throws RepeatWiseException if token is missing, expired, revoked, or invalid
     */
    RefreshToken validateRefreshToken(String token);

    /**
     * Validate and rotate refresh token (token rotation for security).
     * Validates old refresh token, revokes it, and generates new one.
     * If old token is already revoked, triggers security measure (revoke all tokens).
     *
     * @param token Old refresh token (plain text)
     * @param deviceId Optional device ID for tracking
     * @param deviceInfo Optional device information
     * @param ipAddress Optional IP address
     * @return New refresh token (plain text, not hashed)
     * @throws RepeatWiseException if token is missing, expired, revoked, or reused
     */
    String validateAndRotateRefreshToken(String token, String deviceId, String deviceInfo, String ipAddress);

    /**
     * Revoke a specific refresh token.
     *
     * @param token Refresh token (plain text)
     */
    void revokeRefreshToken(String token);

    /**
     * Revoke all refresh tokens for a user (logout from all devices).
     *
     * @param user User entity
     */
    void revokeAllUserTokens(User user);

    /**
     * Delete expired refresh tokens (cleanup job).
     * Should be called periodically.
     */
    void deleteExpiredTokens();
}
