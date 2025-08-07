package com.repeatwise.service;

import com.repeatwise.dto.NotificationDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationService {

    /**
     * Create a new notification
     */
    NotificationDto createNotification(NotificationDto notificationDto);

    /**
     * Find notification by ID
     */
    Optional<NotificationDto> findById(UUID id);

    /**
     * Find notifications by user ID
     */
    List<NotificationDto> findByUserId(UUID userId);

    /**
     * Find unread notifications by user ID
     */
    List<NotificationDto> findUnreadByUserId(UUID userId);

    /**
     * Mark notification as read
     */
    NotificationDto markAsRead(UUID id, UUID userId);

    /**
     * Mark all notifications as read for user
     */
    void markAllAsRead(UUID userId);

    /**
     * Delete notification
     */
    void deleteNotification(UUID id, UUID userId);

    /**
     * Send scheduled notifications
     */
    List<NotificationDto> sendScheduledNotifications();

    /**
     * Create review due notification
     */
    NotificationDto createReviewDueNotification(UUID userId, UUID setId, String setName);

    /**
     * Create cycle completed notification
     */
    NotificationDto createCycleCompletedNotification(UUID userId, UUID setId, String setName, int cycleNo);

    /**
     * Create set mastered notification
     */
    NotificationDto createSetMasteredNotification(UUID userId, UUID setId, String setName);

    /**
     * Get notification statistics for user
     */
    NotificationStatistics getNotificationStatistics(UUID userId);

    /**
     * Notification statistics data class
     */
    record NotificationStatistics(
        long totalNotifications,
        long unreadNotifications,
        long highPriorityNotifications,
        LocalDateTime lastNotificationDate
    ) {}
} 
