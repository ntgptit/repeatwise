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
 *   "expiresIn": 900,
 *   "user": {
 *     "id": "uuid",
 *     "email": "user@example.com",
 *     "username": "john_doe123",
 *     "name": "John Doe",
 *     "language": "VI",
 *     "theme": "SYSTEM",
 *     "timezone": "Asia/Ho_Chi_Minh"
 *   }
 * }
 *
 * Notes:
 * - accessToken: JWT token with HS256 signature
 * - expiresIn: Token lifetime in seconds (900 = 15 minutes)
 * - user: User profile information
 * - refreshToken: Refresh token string (set in HttpOnly cookie by controller, not in response body for security)
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
     * - Claims: sub (userId), email, username, iat, exp, iss, aud
     */
    private String accessToken;

    /**
     * Token expiration time in seconds
     * - Default: 900 seconds (15 minutes)
     */
    private long expiresIn;

    /**
     * User profile information
     * - UC-002: Response includes user object
     */
    private UserResponse user;

    /**
     * Refresh token string
     * - Used internally by controller to set HttpOnly cookie
     * - Not exposed in JSON response for security
     * - UC-002: Refresh token stored in HttpOnly cookie
     */
    private String refreshToken;
}
