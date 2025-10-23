package com.repeatwise.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.NotificationSettings;

/**
 * NotificationSettings Repository
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 1-5)
 * - Database Schema V7: notification_settings table
 * - API Endpoints: GET/PUT /api/notifications/settings
 *
 * Key Queries:
 * - Find by user (for GET settings endpoint)
 * - Find users to notify (for batch notification job - Step 6)
 *
 * @author RepeatWise Team
 */
@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {

    /**
     * Find notification settings by user ID
     *
     * UC-024 Step 1: Navigate to Notification Settings
     * - One-to-one relationship: one settings per user
     *
     * @param userId User UUID
     * @return Optional of NotificationSettings
     */
    Optional<NotificationSettings> findByUserId(UUID userId);

    /**
     * Check if notification settings exist for user
     *
     * Used to determine if settings need to be created (first time)
     * or updated (subsequent calls)
     *
     * @param userId User UUID
     * @return true if settings exist
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find all users who should receive notifications at a specific time
     *
     * UC-024 Step 6: Receive Daily Notification
     * - Background job triggers at specific times (e.g., 09:00, 19:00)
     * - Filters by: enabled, time, and current day of week
     *
     * Query uses index: idx_notification_settings_batch (line 42-44 in V7 migration)
     *
     * @param reminderTime Time to send reminders (HH:MM)
     * @param dayOfWeek Current day (MON, TUE, WED, etc.)
     * @return List of NotificationSettings to process
     */
    @Query("SELECT ns FROM NotificationSettings ns " +
           "WHERE ns.dailyReminderEnabled = true " +
           "AND ns.dailyReminderTime = :reminderTime " +
           "AND ns.dailyReminderDays LIKE %:dayOfWeek%")
    List<NotificationSettings> findUsersToNotifyAt(
        @Param("reminderTime") LocalTime reminderTime,
        @Param("dayOfWeek") String dayOfWeek
    );

    /**
     * Find all users with daily reminders enabled (for analytics/reporting)
     *
     * @return List of NotificationSettings with reminders enabled
     */
    @Query("SELECT ns FROM NotificationSettings ns " +
           "WHERE ns.dailyReminderEnabled = true")
    List<NotificationSettings> findAllWithRemindersEnabled();

    /**
     * Count users with daily reminders enabled
     *
     * Analytics query for admin dashboard (future)
     *
     * @return Count of users with reminders enabled
     */
    @Query("SELECT COUNT(ns) FROM NotificationSettings ns " +
           "WHERE ns.dailyReminderEnabled = true")
    long countUsersWithRemindersEnabled();

    /**
     * Delete notification settings for a user
     * (Cascades automatically when user is deleted)
     *
     * This method is provided for explicit deletion if needed
     *
     * @param userId User UUID
     */
    void deleteByUserId(UUID userId);
}
