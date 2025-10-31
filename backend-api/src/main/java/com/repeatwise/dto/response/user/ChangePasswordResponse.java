package com.repeatwise.dto.response.user;

import lombok.*;

/**
 * Change Password Response DTO
 *
 * Requirements:
 * - UC-006: Change Password
 * - API Response Spec: POST /api/users/change-password
 *
 * Response Format:
 * {
 *   "message": "Password changed successfully. Please login with your new password."
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {

    /**
     * Success message
     * UC-006: "Password changed successfully. Please login with your new password."
     */
    private String message;
}

