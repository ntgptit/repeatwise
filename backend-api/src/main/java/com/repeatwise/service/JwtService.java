package com.repeatwise.service;

import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseException;

/**
 * Service interface for JWT token operations.
 * Handles generation and validation of access tokens.
 */
public interface JwtService {

    /**
     * Generate access token (JWT) for authenticated user.
     * Token payload includes: userId, email, username, iat, exp
     * Token expires in 15 minutes (900 seconds).
     *
     * @param user Authenticated user
     * @return JWT access token string
     */
    String generateAccessToken(User user);

    /**
     * Extract user ID from JWT token.
     *
     * @param token JWT token
     * @return User ID (UUID as string)
     * @throws RepeatWiseException if token is invalid or expired
     */
    String extractUserId(String token);

    /**
     * Extract email from JWT token.
     *
     * @param token JWT token
     * @return User email
     * @throws RepeatWiseException if token is invalid or expired
     */
    String extractEmail(String token);

    /**
     * Validate JWT token.
     * Checks signature, expiration, and format.
     *
     * @param token JWT token
     * @return true if token is valid
     */
    boolean validateToken(String token);

    /**
     * Check if JWT token is expired.
     *
     * @param token JWT token
     * @return true if token is expired
     */
    boolean isTokenExpired(String token);
}
