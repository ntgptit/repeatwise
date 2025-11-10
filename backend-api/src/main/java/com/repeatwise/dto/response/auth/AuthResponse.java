package com.repeatwise.dto.response.auth;

import com.repeatwise.dto.response.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response (login/refresh)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private Integer expiresIn; // Token expiry in seconds (900 for 15 minutes)
    private UserResponse user;
}
