package com.repeatwise.dto.response.auth;

import lombok.*;

/**
 * Logout Response DTO
 *
 * Requirements:
 * - UC-004: User Logout
 * - API Response Spec: POST /api/auth/logout
 *
 * Response Format:
 * {
 *   "message": "Logged out successfully"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponse {

    /**
     * Success message
     * UC-004: "Logged out successfully"
     */
    private String message;
}

