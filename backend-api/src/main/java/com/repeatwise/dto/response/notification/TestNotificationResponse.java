package com.repeatwise.dto.response.notification;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for test notification request
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 4: Test Notification)
 * - API Endpoints: POST /api/notifications/test
 * - API Spec lines 767-776
 *
 * Response Format (UC-024):
 * {
 *   "message": "Test notification sent to your-email@example.com",
 *   "sentAt": "2025-01-10T15:30:00Z"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestNotificationResponse {

    /**
     * Human-readable confirmation message
     * Example: "Test notification sent to your-email@example.com"
     */
    private String message;

    /**
     * Timestamp when test notification was sent (UTC)
     * Example: "2025-01-10T15:30:00Z"
     */
    private Instant sentAt;
}
