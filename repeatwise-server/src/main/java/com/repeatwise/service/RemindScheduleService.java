package com.repeatwise.service;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.enums.RemindStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RemindScheduleService {

    /**
     * Create a new reminder schedule
     */
    RemindScheduleDto createRemindSchedule(UUID userId, UUID setId, LocalDate scheduledDate);

    /**
     * Find reminder by ID
     */
    Optional<RemindScheduleDto> findById(UUID id);

    /**
     * Find reminder by ID and user ID
     */
    Optional<RemindScheduleDto> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Update reminder
     */
    RemindScheduleDto updateRemindSchedule(UUID id, UUID userId, RemindScheduleDto remindScheduleDto);

    /**
     * Delete reminder (soft delete)
     */
    void deleteRemindSchedule(UUID id, UUID userId);

    /**
     * Find all reminders by user ID
     */
    List<RemindScheduleDto> findByUserId(UUID userId);

    /**
     * Find reminders by user ID and status
     */
    List<RemindScheduleDto> findByUserIdAndStatus(UUID userId, RemindStatus status);

    /**
     * Find reminders scheduled for a specific date
     */
    List<RemindScheduleDto> findRemindersScheduledOnDate(LocalDate date);

    /**
     * Find reminders scheduled for a specific date and user
     */
    List<RemindScheduleDto> findRemindersScheduledOnDateForUser(LocalDate date, UUID userId);

    /**
     * Find pending reminders for a specific date
     */
    List<RemindScheduleDto> findPendingRemindersOnDate(LocalDate date);

    /**
     * Find pending reminders for a specific date and user
     */
    List<RemindScheduleDto> findPendingRemindersOnDateForUser(LocalDate date, UUID userId);

    /**
     * Find overdue reminders
     */
    List<RemindScheduleDto> findOverdueReminders(LocalDate today);

    /**
     * Find overdue reminders for a specific user
     */
    List<RemindScheduleDto> findOverdueRemindersForUser(LocalDate today, UUID userId);

    /**
     * Find reminders scheduled between dates
     */
    List<RemindScheduleDto> findRemindersScheduledBetweenDates(LocalDate startDate, LocalDate endDate);

    /**
     * Find reminders scheduled between dates for a specific user
     */
    List<RemindScheduleDto> findRemindersScheduledBetweenDatesForUser(LocalDate startDate, LocalDate endDate, UUID userId);

    /**
     * Count reminders by user ID and status
     */
    long countByUserIdAndStatus(UUID userId, RemindStatus status);

    /**
     * Count reminders scheduled on a specific date
     */
    long countRemindersScheduledOnDate(LocalDate date);

    /**
     * Count reminders scheduled on a specific date for a user
     */
    long countRemindersScheduledOnDateForUser(LocalDate date, UUID userId);

    /**
     * Find reminders that need to be sent
     */
    List<RemindScheduleDto> findRemindersToSend(LocalDate today);

    /**
     * Find reminders that need to be sent for a specific user
     */
    List<RemindScheduleDto> findRemindersToSendForUser(LocalDate today, UUID userId);

    /**
     * Find the next available date for scheduling reminders
     */
    List<LocalDate> findAvailableDatesForUser(UUID userId);

    /**
     * Reschedule a reminder
     */
    RemindScheduleDto rescheduleReminder(UUID id, UUID userId, LocalDate newDate, String reason);

    /**
     * Mark reminder as sent
     */
    RemindScheduleDto markAsSent(UUID id, UUID userId);

    /**
     * Mark reminder as done
     */
    RemindScheduleDto markAsDone(UUID id, UUID userId);

    /**
     * Mark reminder as skipped
     */
    RemindScheduleDto markAsSkipped(UUID id, UUID userId);

    /**
     * Get daily review reminders (respecting 3-set limit)
     */
    List<RemindScheduleDto> getDailyReviewReminders(UUID userId, LocalDate date);

    /**
     * Schedule reminders for a set's next cycle
     */
    List<RemindScheduleDto> scheduleRemindersForNextCycle(UUID setId, UUID userId, LocalDate nextCycleDate);

    /**
     * Get reminder statistics
     */
    RemindStatistics getRemindStatistics(UUID userId);

    /**
     * Reminder statistics data class
     */
    record RemindStatistics(
        long totalReminders,
        long pendingReminders,
        long sentReminders,
        long completedReminders,
        long overdueReminders,
        LocalDate nextReminderDate
    ) {}
} 
