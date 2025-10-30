package com.repeatwise.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Change Password Request DTO
 *
 * Requirements:
 * - UC-006: Change Password
 * - API Request/Response Specs: POST /api/users/change-password
 *
 * Validation Rules:
 * - currentPassword: Required
 * - newPassword: Required, min 8 chars, max 128 chars
 * - confirmNewPassword: Required, must match newPassword
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "{error.user.password.required}")
    private String currentPassword;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String newPassword;

    @NotBlank(message = "{error.user.password.required}")
    @Size(min = 8, max = 128, message = "{error.user.password.length}")
    private String confirmNewPassword;
}

