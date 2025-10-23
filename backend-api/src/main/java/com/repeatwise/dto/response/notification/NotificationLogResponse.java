package com.repeatwise.dto.response.notification;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for notification log (history)
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 6 logging)
 * - API Endpoints: GET /api/notifications/logs
 * - API Spec lines 783-811
 *
 * Response Format (UC-024):
 * {
 *   "id": "uuid",
 *   "type": "DAILY_REMINDER",
 *   "recipient": "user@example.com",
 *   "status": "DELIVERED",
 *   "sentAt": "2025-01-10T09:00:00Z",
 *   "metadata": {
 *     "dueCardsCount": 20,
 *     "streakDays": 15
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
public class NotificationLogResponse {

    /**
     * Unique notification log ID
     */
    private UUID id;

    /**
     * Type of notification
     * Values: "DAILY_REMINDER", "STREAK_REMINDER", "ACHIEVEMENT", "SYSTEM"
     */
    private String type;

    /**
     * Recipient address (email, phone, device token)
     * Example: "user@example.com"
     */
    private String recipient;

    /**
     * Delivery status
     * Values: "PENDING", "SENT", "DELIVERED", "FAILED", "BOUNCED"
     */
    private String status;

    /**
     * Timestamp when notification was sent (UTC)
     * Example: "2025-01-10T09:00:00Z"
     */
    private Instant sentAt;

    /**
     * Timestamp when notification was delivered (UTC)
     * null if not yet delivered or failed
     */
    private Instant deliveredAt;

    /**
     * Error message for FAILED/BOUNCED status
     * null if successful
     */
    private String errorMessage;

    /**
     * Additional metadata (parsed from JSONB)
     * Example:
     * {
     *   "dueCardsCount": 20,
     *   "streakDays": 15,
     *   "retryCount": 0
     * }
     */
    private Map<String, Object> metadata;
}
