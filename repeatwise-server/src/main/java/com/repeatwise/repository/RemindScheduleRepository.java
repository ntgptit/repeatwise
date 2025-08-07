package com.repeatwise.repository;

import com.repeatwise.enums.RemindStatus;
import com.repeatwise.model.RemindSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RemindScheduleRepository extends JpaRepository<RemindSchedule, UUID> {

    /**
     * Find all reminders by user ID
     */
    List<RemindSchedule> findByUserId(UUID userId);

    /**
     * Find reminders by user ID and status
     */
    List<RemindSchedule> findByUserIdAndStatus(UUID userId, RemindStatus status);

    /**
     * Find reminders by set ID
     */
    List<RemindSchedule> findBySetId(UUID setId);

    /**
     * Find reminders by set ID and status
     */
    List<RemindSchedule> findBySetIdAndStatus(UUID setId, RemindStatus status);

    /**
     * Find reminder by ID and user ID
     */
    Optional<RemindSchedule> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Find reminders scheduled for a specific date
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersScheduledOnDate(@Param("date") LocalDate date);

    /**
     * Find reminders scheduled for a specific date and user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.user.id = :userId AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersScheduledOnDateForUser(@Param("date") LocalDate date, @Param("userId") UUID userId);

    /**
     * Find pending reminders for a specific date (for daily limit enforcement)
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.status = 'PENDING' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findPendingRemindersOnDate(@Param("date") LocalDate date);

    /**
     * Find pending reminders for a specific date and user (for daily limit enforcement)
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.user.id = :userId AND rs.status = 'PENDING' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findPendingRemindersOnDateForUser(@Param("date") LocalDate date, @Param("userId") UUID userId);

    /**
     * Find overdue reminders (scheduled date < today and status is PENDING)
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate < :today AND rs.status = 'PENDING' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findOverdueReminders(@Param("today") LocalDate today);

    /**
     * Find overdue reminders for a specific user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate < :today AND rs.user.id = :userId AND rs.status = 'PENDING' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findOverdueRemindersForUser(@Param("today") LocalDate today, @Param("userId") UUID userId);

    /**
     * Find reminders scheduled between dates
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate BETWEEN :startDate AND :endDate AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersScheduledBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find reminders scheduled between dates for a specific user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.scheduledDate BETWEEN :startDate AND :endDate AND rs.user.id = :userId AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersScheduledBetweenDatesForUser(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("userId") UUID userId);

    /**
     * Count reminders by user ID and status
     */
    long countByUserIdAndStatus(UUID userId, RemindStatus status);

    /**
     * Count reminders scheduled on a specific date
     */
    @Query("SELECT COUNT(rs) FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.deletedAt IS NULL")
    long countRemindersScheduledOnDate(@Param("date") LocalDate date);

    /**
     * Count reminders scheduled on a specific date for a user
     */
    @Query("SELECT COUNT(rs) FROM RemindSchedule rs WHERE rs.scheduledDate = :date AND rs.user.id = :userId AND rs.deletedAt IS NULL")
    long countRemindersScheduledOnDateForUser(@Param("date") LocalDate date, @Param("userId") UUID userId);

    /**
     * Find reminders that need to be sent (PENDING status and scheduled date <= today)
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.status = 'PENDING' AND rs.scheduledDate <= :today AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersToSend(@Param("today") LocalDate today);

    /**
     * Find reminders that need to be sent for a specific user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.status = 'PENDING' AND rs.scheduledDate <= :today AND rs.user.id = :userId AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersToSendForUser(@Param("today") LocalDate today, @Param("userId") UUID userId);

    /**
     * Find the next available date for scheduling reminders (for load balancing)
     */
    @Query("SELECT rs.scheduledDate FROM RemindSchedule rs WHERE rs.user.id = :userId AND rs.deletedAt IS NULL GROUP BY rs.scheduledDate HAVING COUNT(rs) < 3 ORDER BY rs.scheduledDate ASC")
    List<LocalDate> findAvailableDatesForUser(@Param("userId") UUID userId);

    /**
     * Find reminders by rescheduled by user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.rescheduledBy.id = :userId AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersRescheduledByUser(@Param("userId") UUID userId);

    /**
     * Find reminders with specific reschedule reason
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.rescheduleReason LIKE %:reason% AND rs.deletedAt IS NULL")
    List<RemindSchedule> findRemindersByRescheduleReason(@Param("reason") String reason);

    /**
     * Find the most recent reminder for a set
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.set.id = :setId AND rs.deletedAt IS NULL ORDER BY rs.scheduledDate DESC LIMIT 1")
    Optional<RemindSchedule> findLatestReminderBySetId(@Param("setId") UUID setId);

    /**
     * Find reminders that were sent but not completed
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.status = 'SENT' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findSentReminders();

    /**
     * Find completed reminders for a user
     */
    @Query("SELECT rs FROM RemindSchedule rs WHERE rs.user.id = :userId AND rs.status = 'DONE' AND rs.deletedAt IS NULL")
    List<RemindSchedule> findCompletedRemindersForUser(@Param("userId") UUID userId);

    // Additional methods for production workflow
    
    // Find reminders by user ID ordered by scheduled date
    List<RemindSchedule> findByUserIdOrderByScheduledDateAsc(UUID userId);
    
    // Find reminders by user ID and status ordered by scheduled date
    List<RemindSchedule> findByUserIdAndStatusOrderByScheduledDateAsc(UUID userId, RemindStatus status);
    
    // Find reminders scheduled on a specific date ordered by user ID
    List<RemindSchedule> findByScheduledDateOrderByUserIdAsc(LocalDate scheduledDate);
    
    // Find reminders by user ID and scheduled date ordered by created at
    List<RemindSchedule> findByUserIdAndScheduledDateOrderByCreatedAtAsc(UUID userId, LocalDate scheduledDate);
    
    // Find reminders by user ID, scheduled date, and status ordered by created at
    List<RemindSchedule> findByUserIdAndScheduledDateAndStatusOrderByCreatedAtAsc(UUID userId, LocalDate scheduledDate, RemindStatus status);
    
    // Count reminders by status and date
    @Query("SELECT COUNT(r) FROM RemindSchedule r WHERE r.status = :status AND r.scheduledDate = :date AND r.deletedAt IS NULL")
    long countByStatusAndDate(@Param("status") RemindStatus status, @Param("date") LocalDate date);
    
    // Count users with reminders on a specific date
    @Query("SELECT COUNT(DISTINCT r.user.id) FROM RemindSchedule r WHERE r.scheduledDate = :date AND r.deletedAt IS NULL")
    long countUsersWithRemindersOnDate(@Param("date") LocalDate date);
    
    // Find user IDs with pending reminders on a specific date
    @Query("SELECT DISTINCT r.user.id FROM RemindSchedule r WHERE r.scheduledDate = :date AND r.status = 'PENDING' AND r.deletedAt IS NULL")
    List<UUID> findUserIdsWithPendingReminders(@Param("date") LocalDate date);
    
    // Find user IDs with overflow reminders (more than limit)
    @Query("SELECT r.user.id FROM RemindSchedule r WHERE r.scheduledDate = :date AND r.status = 'PENDING' AND r.deletedAt IS NULL GROUP BY r.user.id HAVING COUNT(r) > :limit")
    List<UUID> findUserIdsWithOverflowReminders(@Param("date") LocalDate date, @Param("limit") int limit);
    
    // Find overflow reminders for a specific user
    @Query("SELECT r FROM RemindSchedule r WHERE r.scheduledDate = :date AND r.user.id = :userId AND r.status = 'PENDING' AND r.deletedAt IS NULL ORDER BY r.createdAt")
    List<RemindSchedule> findOverflowRemindersForUser(@Param("date") LocalDate date, @Param("userId") UUID userId);
} 
