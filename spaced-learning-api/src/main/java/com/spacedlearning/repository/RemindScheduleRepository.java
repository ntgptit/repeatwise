package com.spacedlearning.repository;

import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.RemindSchedule;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.RemindStatus;
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

    // Find reminders by user
    List<RemindSchedule> findByUserOrderByRemindDateAsc(User user);
    
    // Find reminders by set
    List<RemindSchedule> findBySetOrderByRemindDateAsc(LearningSet set);
    
    // Find reminders by user and date
    List<RemindSchedule> findByUserAndRemindDateOrderByRemindDateAsc(User user, LocalDate remindDate);
    
    // Find reminders by user and status
    List<RemindSchedule> findByUserAndStatusOrderByRemindDateAsc(User user, RemindStatus status);
    
    // Find pending reminders for a specific date
    List<RemindSchedule> findByUserAndRemindDateAndStatusOrderByRemindDateAsc(User user, LocalDate remindDate, RemindStatus status);
    
    // Find overdue reminders (before today)
    @Query("SELECT rs FROM RemindSchedule rs " +
           "WHERE rs.user = :user AND rs.remindDate < :today " +
           "AND rs.status = 'PENDING' " +
           "ORDER BY rs.remindDate ASC")
    List<RemindSchedule> findOverdueReminders(@Param("user") User user, @Param("today") LocalDate today);
    
    // Count pending reminders for a user on a specific date
    long countByUserAndRemindDateAndStatus(User user, LocalDate remindDate, RemindStatus status);
    
    // Find the next available date with less than max sets per day
    @Query("SELECT rs.remindDate FROM RemindSchedule rs " +
           "WHERE rs.user = :user AND rs.remindDate >= :startDate " +
           "AND rs.status = 'PENDING' " +
           "GROUP BY rs.remindDate " +
           "HAVING COUNT(rs) < :maxSetsPerDay " +
           "ORDER BY rs.remindDate ASC")
    List<LocalDate> findAvailableDates(@Param("user") User user, 
                                      @Param("startDate") LocalDate startDate, 
                                      @Param("maxSetsPerDay") int maxSetsPerDay);
    
    // Find reminder by set and date
    Optional<RemindSchedule> findBySetAndRemindDate(LearningSet set, LocalDate remindDate);
    
    // Find active reminder for a set (pending or sent)
    @Query("SELECT rs FROM RemindSchedule rs " +
           "WHERE rs.set = :set AND rs.status IN ('PENDING', 'SENT') " +
           "ORDER BY rs.remindDate ASC")
    List<RemindSchedule> findActiveRemindersForSet(@Param("set") LearningSet set);
    
    // Delete all reminders for a set
    void deleteBySet(LearningSet set);
    
    // Find reminders that need to be sent (for notification service)
    @Query("SELECT rs FROM RemindSchedule rs " +
           "WHERE rs.remindDate = :today AND rs.status = 'PENDING' " +
           "ORDER BY rs.user.id, rs.remindDate ASC")
    List<RemindSchedule> findRemindersToSendToday(@Param("today") LocalDate today);
}
