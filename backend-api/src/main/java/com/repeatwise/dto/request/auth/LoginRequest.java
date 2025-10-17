package com.repeatwise.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Login Request DTO
 *
 * Requirements:
 * - UC-002: User Login
 * - API Request/Response Specs: POST /api/auth/login
 *
 * Validation Rules:
 * - usernameOrEmail: Required, can be username or email
 * - password: Required, min 8 chars, max 128 chars
 *
 * Business Rules:
 * - System auto-detects if input is email (contains @) or username
 * - Username/Email comparison is case-insensitive
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "{error.user.username.or.email.required}")
    @Size(max = 255, message = "{error.user.username.or.email.too.long}")
    private String usernameOrEmail;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String password;
}
