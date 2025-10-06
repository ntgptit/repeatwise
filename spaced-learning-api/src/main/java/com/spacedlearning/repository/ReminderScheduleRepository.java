package com.spacedlearning.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.ReminderSchedule;
import com.spacedlearning.entity.enums.ReminderStatus;

/**
 * Repository interface for ReminderSchedule entity
 * Provides data access methods for reminder schedule management
 */
@Repository
public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, UUID> {

    /**
     * Find reminder schedules by user
     * @param userId user ID
     * @return list of reminder schedules for user
     */
    List<ReminderSchedule> findByUserId(UUID userId);

    /**
     * Find reminder schedules by user with pagination
     * @param userId user ID
     * @param pageable pagination information
     * @return page of reminder schedules for user
     */
    Page<ReminderSchedule> findByUserId(UUID userId, Pageable pageable);

    /**
     * Find reminder schedules by learning set
     * @param setId learning set ID
     * @return list of reminder schedules for learning set
     */
    List<ReminderSchedule> findByLearningSetId(UUID setId);

    /**
     * Find reminder schedules by user and learning set
     * @param userId user ID
     * @param setId learning set ID
     * @return list of reminder schedules for user and learning set
     */
    List<ReminderSchedule> findByUserIdAndLearningSetId(UUID userId, UUID setId);

    /**
     * Find reminder schedules by status
     * @param status reminder status
     * @return list of reminder schedules with status
     */
    List<ReminderSchedule> findByStatus(ReminderStatus status);

    /**
     * Find reminder schedules by user and status
     * @param userId user ID
     * @param status reminder status
     * @return list of reminder schedules for user with status
     */
    List<ReminderSchedule> findByUserIdAndStatus(UUID userId, ReminderStatus status);

    /**
     * Find reminder schedules by scheduled date
     * @param scheduledDate scheduled date
     * @return list of reminder schedules on date
     */
    List<ReminderSchedule> findByScheduledDate(LocalDate scheduledDate);

    /**
     * Find reminder schedules by user and scheduled date
     * @param userId user ID
     * @param scheduledDate scheduled date
     * @return list of reminder schedules for user on date
     */
    List<ReminderSchedule> findByUserIdAndScheduledDate(UUID userId, LocalDate scheduledDate);

    /**
     * Find reminder schedules by scheduled date range
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules between dates
     */
    List<ReminderSchedule> findByScheduledDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find reminder schedules by user and scheduled date range
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules for user between dates
     */
    List<ReminderSchedule> findByUserIdAndScheduledDateBetween(UUID userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find reminder schedules by reminder time
     * @param reminderTime reminder time
     * @return list of reminder schedules at time
     */
    List<ReminderSchedule> findByReminderTime(LocalTime reminderTime);

    /**
     * Find reminder schedules by user and reminder time
     * @param userId user ID
     * @param reminderTime reminder time
     * @return list of reminder schedules for user at time
     */
    List<ReminderSchedule> findByUserIdAndReminderTime(UUID userId, LocalTime reminderTime);

    /**
     * Find pending reminder schedules
     * @return list of pending reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'PENDING'")
    List<ReminderSchedule> findPendingReminders();

    /**
     * Find pending reminder schedules by user
     * @param userId user ID
     * @return list of pending reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'PENDING'")
    List<ReminderSchedule> findPendingRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find pending reminder schedules for today
     * @param today today's date
     * @return list of pending reminder schedules for today
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.scheduledDate = :today AND rs.status = 'PENDING'")
    List<ReminderSchedule> findPendingRemindersForToday(@Param("today") LocalDate today);

    /**
     * Find pending reminder schedules for user today
     * @param userId user ID
     * @param today today's date
     * @return list of pending reminder schedules for user today
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.scheduledDate = :today AND rs.status = 'PENDING'")
    List<ReminderSchedule> findPendingRemindersForUserToday(@Param("userId") UUID userId, @Param("today") LocalDate today);

    /**
     * Find sent reminder schedules
     * @return list of sent reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'SENT'")
    List<ReminderSchedule> findSentReminders();

    /**
     * Find sent reminder schedules by user
     * @param userId user ID
     * @return list of sent reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'SENT'")
    List<ReminderSchedule> findSentRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find cancelled reminder schedules
     * @return list of cancelled reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'CANCELLED'")
    List<ReminderSchedule> findCancelledReminders();

    /**
     * Find cancelled reminder schedules by user
     * @param userId user ID
     * @return list of cancelled reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'CANCELLED'")
    List<ReminderSchedule> findCancelledRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find done reminder schedules
     * @return list of done reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'DONE'")
    List<ReminderSchedule> findDoneReminders();

    /**
     * Find done reminder schedules by user
     * @param userId user ID
     * @return list of done reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'DONE'")
    List<ReminderSchedule> findDoneRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find skipped reminder schedules
     * @return list of skipped reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'SKIPPED'")
    List<ReminderSchedule> findSkippedReminders();

    /**
     * Find skipped reminder schedules by user
     * @param userId user ID
     * @return list of skipped reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'SKIPPED'")
    List<ReminderSchedule> findSkippedRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find rescheduled reminder schedules
     * @return list of rescheduled reminder schedules
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.status = 'RESCHEDULED'")
    List<ReminderSchedule> findRescheduledReminders();

    /**
     * Find rescheduled reminder schedules by user
     * @param userId user ID
     * @return list of rescheduled reminder schedules for user
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.status = 'RESCHEDULED'")
    List<ReminderSchedule> findRescheduledRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find reminder schedules by reschedule count
     * @param rescheduleCount reschedule count
     * @return list of reminder schedules with reschedule count
     */
    List<ReminderSchedule> findByRescheduleCount(Integer rescheduleCount);

    /**
     * Find reminder schedules by user and reschedule count
     * @param userId user ID
     * @param rescheduleCount reschedule count
     * @return list of reminder schedules for user with reschedule count
     */
    List<ReminderSchedule> findByUserIdAndRescheduleCount(UUID userId, Integer rescheduleCount);

    /**
     * Find reminder schedules that can be rescheduled (reschedule count < 2)
     * @return list of reminder schedules that can be rescheduled
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.rescheduleCount < 2")
    List<ReminderSchedule> findReschedulableReminders();

    /**
     * Find reminder schedules that can be rescheduled by user
     * @param userId user ID
     * @return list of reminder schedules for user that can be rescheduled
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.rescheduleCount < 2")
    List<ReminderSchedule> findReschedulableRemindersByUser(@Param("userId") UUID userId);

    /**
     * Find reminder schedules that cannot be rescheduled (reschedule count >= 2)
     * @return list of reminder schedules that cannot be rescheduled
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.rescheduleCount >= 2")
    List<ReminderSchedule> findNonReschedulableReminders();

    /**
     * Find reminder schedules that cannot be rescheduled by user
     * @param userId user ID
     * @return list of reminder schedules for user that cannot be rescheduled
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.rescheduleCount >= 2")
    List<ReminderSchedule> findNonReschedulableRemindersByUser(@Param("userId") UUID userId);

    /**
     * Count reminder schedules by user
     * @param userId user ID
     * @return count of reminder schedules for user
     */
    long countByUserId(UUID userId);

    /**
     * Count reminder schedules by user and status
     * @param userId user ID
     * @param status reminder status
     * @return count of reminder schedules for user with status
     */
    long countByUserIdAndStatus(UUID userId, ReminderStatus status);

    /**
     * Count reminder schedules by learning set
     * @param setId learning set ID
     * @return count of reminder schedules for learning set
     */
    long countByLearningSetId(UUID setId);

    /**
     * Count reminder schedules by learning set and status
     * @param setId learning set ID
     * @param status reminder status
     * @return count of reminder schedules for learning set with status
     */
    long countByLearningSetIdAndStatus(UUID setId, ReminderStatus status);

    /**
     * Count reminder schedules by scheduled date
     * @param scheduledDate scheduled date
     * @return count of reminder schedules on date
     */
    long countByScheduledDate(LocalDate scheduledDate);

    /**
     * Count reminder schedules by user and scheduled date
     * @param userId user ID
     * @param scheduledDate scheduled date
     * @return count of reminder schedules for user on date
     */
    long countByUserIdAndScheduledDate(UUID userId, LocalDate scheduledDate);

    /**
     * Find reminder schedules created after specified date
     * @param date creation date
     * @return list of reminder schedules created after date
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.createdAt > :date")
    List<ReminderSchedule> findByCreatedAtAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find reminder schedules created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules created between dates
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.createdAt BETWEEN :startDate AND :endDate")
    List<ReminderSchedule> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find reminder schedules by user created between dates
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules for user created between dates
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.createdAt BETWEEN :startDate AND :endDate")
    List<ReminderSchedule> findByUserIdAndCreatedAtBetween(@Param("userId") UUID userId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find reminder schedules sent after specified date
     * @param date sent date
     * @return list of reminder schedules sent after date
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.sentAt > :date")
    List<ReminderSchedule> findBySentAtAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find reminder schedules sent between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules sent between dates
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.sentAt BETWEEN :startDate AND :endDate")
    List<ReminderSchedule> findBySentAtBetween(@Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find reminder schedules by user sent between dates
     * @param userId user ID
     * @param startDate start date
     * @param endDate end date
     * @return list of reminder schedules for user sent between dates
     */
    @Query("SELECT rs FROM ReminderSchedule rs WHERE rs.user.id = :userId AND rs.sentAt BETWEEN :startDate AND :endDate")
    List<ReminderSchedule> findByUserIdAndSentAtBetween(@Param("userId") UUID userId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    // Additional methods for LearningCycleService
    /**
     * Find reminder schedules by learning set and cycle number
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @return list of reminder schedules for learning set and cycle
     */
    List<ReminderSchedule> findByLearningSetAndCycleNumber(com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber);

    /**
     * Find reminder schedule by learning set, cycle number and review number
     * @param learningSet learning set
     * @param cycleNumber cycle number
     * @param reviewNumber review number
     * @return optional reminder schedule
     */
    Optional<ReminderSchedule> findByLearningSetAndCycleNumberAndReviewNumber(
            com.spacedlearning.entity.LearningSet learningSet, Integer cycleNumber, Integer reviewNumber);
}
