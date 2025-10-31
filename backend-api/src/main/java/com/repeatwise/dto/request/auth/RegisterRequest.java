package com.repeatwise.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Register Request DTO
 *
 * Requirements:
 * - UC-001: User Registration
 * - API Request/Response Specs: POST /api/auth/register
 *
 * Validation Rules:
 * - username: Optional, 3-30 chars, alphanumeric + underscore/hyphen (if provided)
 * - email: Required, valid email format, max 255 chars
 * - password: Required, min 8 chars, max 128 chars
 * - confirmPassword: Required, must match password
 * - name: Optional, max 100 chars
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Username (optional)
     * UC-001: Username is optional during registration
     * Format: 3-30 characters, alphanumeric + underscore/hyphen only
     * Case-sensitive (not normalized)
     */
    @Size(min = 3, max = 30, message = "{error.user.username.length}")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "{error.user.username.format}")
    private String username;

    @NotBlank(message = "{error.user.email.required}")
    @Email(message = "{error.user.email.invalid}")
    @Size(max = 255, message = "{error.user.email.too.long}")
    private String email;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String password;

    /**
     * Confirm Password
     * UC-001: Must match password field
     */
    @NotBlank(message = "{error.user.confirm.password.required}")
    private String confirmPassword;

    @Size(max = 100, message = "{error.user.name.too.long}")
    private String name;
}
