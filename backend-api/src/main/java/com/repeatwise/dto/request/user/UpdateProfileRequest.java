package com.repeatwise.dto.request.user;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating user profile
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - API: PUT /api/users/me
 *
 * Validation Rules:
 * - name: Required, 1-100 characters
 * - timezone: Required, valid IANA timezone string, max 50 characters
 * - language: Required, must be VI or EN
 * - theme: Required, must be LIGHT, DARK, or SYSTEM
 *
 * Note: username and email are read-only in MVP
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "{error.user.name.required}")
    @Size(min = 1, max = 100, message = "{error.user.name.length}")
    private String name;

    @NotBlank(message = "{error.user.timezone.required}")
    @Size(max = 50, message = "{error.user.timezone.length}")
    private String timezone;

    @NotNull(message = "{error.user.language.required}")
    private Language language;

    @NotNull(message = "{error.user.theme.required}")
    private Theme theme;
}
