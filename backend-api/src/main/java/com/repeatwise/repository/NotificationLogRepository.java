package com.repeatwise.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.NotificationLog;
import com.repeatwise.entity.enums.NotificationStatus;
import com.repeatwise.entity.enums.NotificationType;

/**
 * NotificationLog Repository
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 6, Alternative A3)
 * - Database Schema V7: notification_logs table
 * - API Endpoints: GET /api/notifications/logs
 *
 * Key Queries:
 * - Find logs by user (for history endpoint)
 * - Find failed notifications (for retry mechanism A3)
 * - Cleanup old logs (retention policy)
 *
 * @author RepeatWise Team
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    /**
     * Find notification logs by user (paginated)
     *
     * UC-024 API: GET /api/notifications/logs
     * - Returns user's notification history
     * - Sorted by sentAt DESC (most recent first)
     *
     * Uses index: idx_notification_logs_user_date (line 83-84 in V7 migration)
     *
     * @param userId User UUID
     * @param pageable Pagination parameters
     * @return Page of NotificationLog
     */
    Page<NotificationLog> findByUserIdOrderBySentAtDesc(UUID userId, Pageable pageable);

    /**
     * Find notification logs by user and type (paginated)
     *
     * Filter history by notification type (e.g., only DAILY_REMINDER)
     *
     * @param userId User UUID
     * @param notificationType Type to filter by
     * @param pageable Pagination parameters
     * @return Page of NotificationLog
     */
    Page<NotificationLog> findByUserIdAndNotificationTypeOrderBySentAtDesc(
        UUID userId,
        NotificationType notificationType,
        Pageable pageable
    );

    /**
     * Find failed notifications eligible for retry
     *
     * UC-024 A3: Email Delivery Failure
     * - Finds FAILED/BOUNCED notifications
     * - Filters by max retry attempts (metadata.retry_count < 3)
     * - Ordered by sentAt ASC (oldest first)
     *
     * Uses index: idx_notification_logs_status (line 87-89 in V7 migration)
     *
     * @param maxRetries Maximum retry attempts (default 3)
     * @return List of failed NotificationLog entries
     */
    @Query("SELECT nl FROM NotificationLog nl " +
           "WHERE nl.status IN ('FAILED', 'BOUNCED') " +
           "AND CAST(JSON_VALUE(nl.metadata, '$.retry_count') AS integer) < :maxRetries " +
           "ORDER BY nl.sentAt ASC")
    List<NotificationLog> findFailedNotificationsForRetry(@Param("maxRetries") int maxRetries);

    /**
     * Find notifications by status (for monitoring/analytics)
     *
     * @param status Notification status
     * @param pageable Pagination parameters
     * @return Page of NotificationLog
     */
    Page<NotificationLog> findByStatusOrderBySentAtDesc(
        NotificationStatus status,
        Pageable pageable
    );

    /**
     * Count notifications by user and status
     *
     * Analytics query: How many successful/failed notifications per user
     *
     * @param userId User UUID
     * @param status Notification status
     * @return Count of notifications
     */
    long countByUserIdAndStatus(UUID userId, NotificationStatus status);

    /**
     * Find notifications sent in a date range
     *
     * Used for analytics and reporting
     *
     * @param userId User UUID
     * @param startDate Start of date range
     * @param endDate End of date range
     * @param pageable Pagination parameters
     * @return Page of NotificationLog
     */
    @Query("SELECT nl FROM NotificationLog nl " +
           "WHERE nl.userId = :userId " +
           "AND nl.sentAt BETWEEN :startDate AND :endDate " +
           "ORDER BY nl.sentAt DESC")
    Page<NotificationLog> findByUserIdAndSentAtBetween(
        @Param("userId") UUID userId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate,
        Pageable pageable
    );

    /**
     * Delete old notification logs (cleanup job)
     *
     * UC-024: Retention policy - keep logs for 90 days
     * - Deletes SENT/DELIVERED logs older than retentionDate
     * - Keeps FAILED/BOUNCED logs indefinitely for debugging
     *
     * Uses index: idx_notification_logs_cleanup (line 95-97 in V7 migration)
     *
     * @param retentionDate Date before which to delete (e.g., NOW() - 90 days)
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM NotificationLog nl " +
           "WHERE nl.status IN ('SENT', 'DELIVERED') " +
           "AND nl.sentAt < :retentionDate")
    int deleteOldSuccessfulLogs(@Param("retentionDate") Instant retentionDate);

    /**
     * Find latest notification for user by type
     *
     * Used to check when user last received a specific notification type
     *
     * @param userId User UUID
     * @param notificationType Notification type
     * @return Latest NotificationLog or null
     */
    @Query("SELECT nl FROM NotificationLog nl " +
           "WHERE nl.userId = :userId " +
           "AND nl.notificationType = :notificationType " +
           "ORDER BY nl.sentAt DESC " +
           "LIMIT 1")
    NotificationLog findLatestByUserIdAndType(
        @Param("userId") UUID userId,
        @Param("notificationType") NotificationType notificationType
    );
}
