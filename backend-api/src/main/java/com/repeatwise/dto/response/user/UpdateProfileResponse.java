package com.repeatwise.dto.response.user;

import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Update Profile Response DTO
 *
 * Requirements:
 * - UC-005: Update User Profile
 * - API Response Spec: PUT /api/users/me
 *
 * Response Format:
 * {
 *   "message": "Profile updated successfully",
 *   "user": {
 *     "id": "uuid",
 *     "email": "user@example.com",
 *     "name": "John Smith",
 *     "timezone": "Asia/Ho_Chi_Minh",
 *     "language": "VI",
 *     "theme": "DARK",
 *     "created_at": "2025-01-15T10:30:00Z",
 *     "updated_at": "2025-01-28T14:45:00Z"
 *   }
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileResponse {

    /**
     * Success message
     * UC-005: "Profile updated successfully"
     */
    private String message;

    /**
     * Updated user profile
     */
    private UserProfileResponse user;
}

