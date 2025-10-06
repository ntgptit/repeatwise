package com.spacedlearning.service;

import com.spacedlearning.dto.notification.NotificationRequest;
import com.spacedlearning.dto.notification.NotificationResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Notification operations
 */
public interface NotificationService {

    /**
     * Send notification to user
     * 
     * @param request Notification request
     * @return Notification response
     */
    NotificationResponse sendNotification(NotificationRequest request);

    /**
     * Get notifications for user
     * 
     * @param userId User ID
     * @param limit Maximum number of notifications to return
     * @return List of notifications
     */
    List<NotificationResponse> getUserNotifications(UUID userId, int limit);

    /**
     * Mark notification as read
     * 
     * @param notificationId Notification ID
     * @param userId User ID
     * @return Updated notification response
     */
    NotificationResponse markAsRead(UUID notificationId, UUID userId);

    /**
     * Mark all notifications as read for user
     * 
     * @param userId User ID
     * @return Number of notifications marked as read
     */
    int markAllAsRead(UUID userId);

    /**
     * Delete notification
     * 
     * @param notificationId Notification ID
     * @param userId User ID
     */
    void deleteNotification(UUID notificationId, UUID userId);

    /**
     * Get unread notification count for user
     * 
     * @param userId User ID
     * @return Number of unread notifications
     */
    int getUnreadCount(UUID userId);
}
