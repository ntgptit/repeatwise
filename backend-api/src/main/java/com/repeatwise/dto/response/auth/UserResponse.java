package com.repeatwise.dto.response.auth;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * User Response DTO
 *
 * Requirements:
 * - UC-001: User Registration
 * - UC-002: User Login
 * - API Response Specs
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String name;
    private Language language;
    private Theme theme;
    private Instant createdAt;
}
