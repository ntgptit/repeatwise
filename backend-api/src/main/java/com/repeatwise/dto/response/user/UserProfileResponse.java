package com.repeatwise.dto.response.user;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for user profile information
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - API: GET /api/users/me, PUT /api/users/me
 *
 * Contains user profile data including:
 * - Basic info: id, username, email, name
 * - Preferences: timezone, language, theme
 * - Audit fields: createdAt, updatedAt
 *
 * Note: passwordHash and sensitive data are excluded
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private UUID id;

    private String username;

    private String email;

    private String name;

    private String timezone;

    private Language language;

    private Theme theme;

    private Instant createdAt;

    private Instant updatedAt;
}
