package com.repeatwise.dto.response.auth;

import lombok.*;

/**
 * Login Response DTO
 *
 * Requirements:
 * - UC-002: User Login
 * - API Response Spec: POST /api/auth/login
 *
 * Response Format:
 * {
 *   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "expiresIn": 900
 * }
 *
 * Notes:
 * - accessToken: JWT token with HS256 signature
 * - expiresIn: Token lifetime in seconds (900 = 15 minutes)
 * - Refresh token is sent in HttpOnly cookie (not in response body)
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT access token
     * - Algorithm: HS256
     * - Expiry: 15 minutes
     * - Claims: sub (userId), email, iat, exp, iss, aud
     */
    private String accessToken;

    /**
     * Token expiration time in seconds
     * - Default: 900 seconds (15 minutes)
     */
    private long expiresIn;
}
