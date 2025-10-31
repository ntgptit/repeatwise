package com.repeatwise.dto.response.auth;

import lombok.*;

import java.util.UUID;

/**
 * Register Response DTO
 *
 * Requirements:
 * - UC-001: User Registration
 * - API Response Spec: POST /api/auth/register
 *
 * Response Format:
 * {
 *   "message": "Registration successful. Please login.",
 *   "userId": "uuid-here"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    /**
     * Success message
     * UC-001: "Registration successful. Please login."
     */
    private String message;

    /**
     * User ID (UUID)
     * UC-001: UUID of newly created user
     */
    private UUID userId;
}

