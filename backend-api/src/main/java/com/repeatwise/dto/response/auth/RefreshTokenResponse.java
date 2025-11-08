package com.repeatwise.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal DTO for refresh token response.
 * Used to pass both access token and new refresh token from service to controller.
 * New refresh token should be set as HTTP-only cookie.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenResponse {

    private String accessToken;
    private Integer expiresIn; // Token expiry in seconds (900 for 15 minutes)
    private String refreshToken; // New refresh token (plain text) for HTTP-only cookie
}
