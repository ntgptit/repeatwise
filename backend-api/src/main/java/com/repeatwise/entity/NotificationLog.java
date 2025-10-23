package com.repeatwise.entity;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.repeatwise.entity.base.BaseEntity;
import com.repeatwise.entity.enums.NotificationMethod;
import com.repeatwise.entity.enums.NotificationStatus;
import com.repeatwise.entity.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * NotificationLog entity - Immutable notification delivery history
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 6, Alternative A3)
 * - Database Schema V7: notification_logs table
 * - API Endpoints: GET /api/notifications/logs
 *
 * Purpose:
 * - Audit trail for all notification deliveries
 * - Support retry mechanism (A3: Email Delivery Failure)
 * - Analytics for notification effectiveness
 * - Debugging failed deliveries
 *
 * Business Rules (UC-024):
 * - Immutable: Once created, cannot be updated (only insert)
 * - Retention: Keep for 90 days (cleanup job)
 * - Retry: Max 3 attempts for FAILED status (A3)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "notification_logs")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog extends BaseEntity {

    /**
     * User who received this notification
     * FK to users table
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Type of notification sent
     * UC-024 BR-076: DAILY_REMINDER (MVP), STREAK_REMINDER/ACHIEVEMENT (Future)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    /**
     * Delivery method used
     * UC-024 BR-078: EMAIL (MVP), PUSH/SMS (Future)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", nullable = false, length = 20)
    private NotificationMethod notificationMethod;

    /**
     * Recipient address (email, phone, device token)
     * - EMAIL: user@example.com
     * - PUSH: FCM/APNs device token (Future)
     * - SMS: +84912345678 (Future)
     */
    @NotBlank
    @Size(max = 255)
    @Column(name = "recipient", nullable = false, length = 255)
    private String recipient;

    /**
     * Email subject line
     * UC-024 Step 6: "=� [RepeatWise] You have 20 cards due for review"
     * Nullable for non-email notifications
     */
    @Size(max = 255)
    @Column(name = "subject", length = 255)
    private String subject;

    /**
     * Notification body content
     * - EMAIL: HTML or plain text email body
     * - PUSH: Notification message (Future)
     * - SMS: Text message (Future)
     */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * Delivery status
     * PENDING � SENT � DELIVERED (success path)
     * PENDING � FAILED (retry up to 3 times)
     * SENT � BOUNCED (email bounce)
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    /**
     * Error message for FAILED/BOUNCED status
     * UC-024 A3: "Mailbox not found", "SMTP connection timeout", etc.
     * Used for debugging and retry decision
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Additional metadata in JSON format
     * JSONB for extensibility without schema changes
     *
     * Example for DAILY_REMINDER:
     * {
     *   "due_cards_count": 20,
     *   "streak_days": 15,
     *   "retry_count": 0,
     *   "smtp_response_code": 250
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /**
     * Timestamp when notification was sent (or attempted)
     * Auto-populated by BaseEntity.createdAt, but duplicated for clarity
     * Used for retention cleanup (delete after 90 days)
     */
    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    /**
     * Timestamp when notification was confirmed delivered
     * - EMAIL: When mail server accepts (SENT) or user opens (DELIVERED)
     * - PUSH: When device confirms receipt (Future)
     * Nullable if not yet delivered or failed
     */
    @Column(name = "delivered_at")
    private Instant deliveredAt;

    /**
     * Check if this notification was successfully delivered
     * @return true if status is SENT or DELIVERED
     */
    public boolean isSuccessful() {
        return status != null && status.isSuccess();
    }

    /**
     * Check if this notification failed and is eligible for retry
     * @return true if status is FAILED or BOUNCED
     */
    public boolean isEligibleForRetry() {
        return status != null && status.isFailure();
    }

    /**
     * Check if notification is still pending
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return status != null && status.isPending();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationLog)) {
            return false;
        }
        final NotificationLog that = (NotificationLog) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
