package com.repeatwise.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal DTO for login response with refresh token.
 * Used to pass refresh token from service to controller.
 * Refresh token should be set as HTTP-only cookie, not returned in response body.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private AuthResponse authResponse;
    private String refreshToken; // Plain text token to be set in HTTP-only cookie
}
