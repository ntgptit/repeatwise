package com.repeatwise.service;

import com.repeatwise.dto.RemindScheduleDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReminderSchedulerService {

    /**
     * Daily job to process pending reminders
     * Applies 3-set-per-day rule and sends notifications
     */
    List<RemindScheduleDto> processDailyReminders(LocalDate date);

    /**
     * Process reminders for a specific user
     * Applies 3-set-per-day rule for the user
     */
    List<RemindScheduleDto> processUserReminders(UUID userId, LocalDate date);

    /**
     * Mark reminder as sent (after notification is delivered)
     */
    RemindScheduleDto markAsSent(UUID reminderId, UUID userId);

    /**
     * Mark reminder as completed (user finished review)
     */
    RemindScheduleDto markAsCompleted(UUID reminderId, UUID userId, Integer score, String notes);

    /**
     * Mark reminder as skipped (user skipped review)
     */
    RemindScheduleDto markAsSkipped(UUID reminderId, UUID userId, String reason);

    /**
     * Reschedule reminder to a new date
     */
    RemindScheduleDto rescheduleReminder(UUID reminderId, UUID userId, LocalDate newDate, String reason);

    /**
     * Cancel reminder
     */
    RemindScheduleDto cancelReminder(UUID reminderId, UUID userId, String reason);

    /**
     * Auto-reschedule reminders that exceed daily limit
     */
    List<RemindScheduleDto> autoRescheduleOverflowReminders(LocalDate date);

    /**
     * Get reminders ready for processing (pending reminders for today)
     */
    List<RemindScheduleDto> getRemindersReadyForProcessing(LocalDate date);

    /**
     * Get daily reminder statistics
     */
    DailyReminderStatistics getDailyReminderStatistics(LocalDate date);

    /**
     * Daily reminder statistics
     */
    record DailyReminderStatistics(
        long totalPending,
        long totalSent,
        long totalCompleted,
        long totalSkipped,
        long totalRescheduled,
        long totalCancelled,
        long usersWithReminders,
        long notificationsSent
    ) {}
} 
