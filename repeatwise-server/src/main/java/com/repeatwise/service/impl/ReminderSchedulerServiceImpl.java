package com.repeatwise.service.impl;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.enums.RemindStatus;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.RemindScheduleMapper;
import com.repeatwise.model.RemindSchedule;
import com.repeatwise.model.User;
import com.repeatwise.repository.RemindScheduleRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.ReminderSchedulerService;
import com.repeatwise.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReminderSchedulerServiceImpl implements ReminderSchedulerService {

    private final RemindScheduleRepository remindScheduleRepository;
    private final UserRepository userRepository;
    private final RemindScheduleMapper remindScheduleMapper;
    private final NotificationService notificationService;

    private static final int MAX_REMINDERS_PER_DAY = 3;

    @Override
    public List<RemindScheduleDto> processDailyReminders(LocalDate date) {
        log.info("Processing daily reminders for date: {}", date);
        
        List<RemindScheduleDto> processedReminders = new ArrayList<>();
        
        // Get all users with pending reminders for the date
        List<UUID> userIds = remindScheduleRepository.findUserIdsWithPendingReminders(date);
        
        for (UUID userId : userIds) {
            List<RemindScheduleDto> userReminders = processUserReminders(userId, date);
            processedReminders.addAll(userReminders);
        }
        
        log.info("Processed {} reminders for {} users on date: {}", 
                processedReminders.size(), userIds.size(), date);
        
        return processedReminders;
    }

    @Override
    public List<RemindScheduleDto> processUserReminders(UUID userId, LocalDate date) {
        log.debug("Processing reminders for user ID: {} on date: {}", userId, date);
        
        // Get pending reminders for the user on the specified date
        List<RemindSchedule> pendingReminders = remindScheduleRepository
                .findPendingRemindersOnDateForUser(date, userId);
        
        // Sort by priority: overdue first, then by word count (descending)
        pendingReminders.sort((r1, r2) -> {
            boolean r1Overdue = r1.getScheduledDate().isBefore(date);
            boolean r2Overdue = r2.getScheduledDate().isBefore(date);
            
            if (r1Overdue && !r2Overdue) return -1;
            if (!r1Overdue && r2Overdue) return 1;
            
            // Both overdue or both not overdue, sort by word count
            return Integer.compare(r2.getSet().getWordCount(), r1.getSet().getWordCount());
        });
        
        // Take only the first 3 reminders (daily limit)
        List<RemindSchedule> selectedReminders = pendingReminders.stream()
                .limit(MAX_REMINDERS_PER_DAY)
                .collect(Collectors.toList());
        
        // Mark selected reminders as sent and send notifications
        List<RemindScheduleDto> processedReminders = new ArrayList<>();
        for (RemindSchedule reminder : selectedReminders) {
            try {
                // Mark as sent
                reminder.setStatus(RemindStatus.SENT);
                reminder.setSentAt(LocalDateTime.now());
                RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
                
                // Send notification
                notificationService.createReviewDueNotification(
                    userId, 
                    reminder.getSet().getId(), 
                    reminder.getSet().getName()
                );
                
                processedReminders.add(remindScheduleMapper.toDto(savedReminder));
                log.debug("Sent reminder for set: {}", reminder.getSet().getName());
                
            } catch (Exception e) {
                log.error("Failed to process reminder ID: {}, error: {}", reminder.getId(), e.getMessage());
            }
        }
        
        // Auto-reschedule remaining reminders
        List<RemindSchedule> remainingReminders = pendingReminders.stream()
                .skip(MAX_REMINDERS_PER_DAY)
                .collect(Collectors.toList());
        
        for (RemindSchedule reminder : remainingReminders) {
            try {
                rescheduleReminder(reminder.getId(), userId, date.plusDays(1), "Auto-rescheduled due to daily limit");
            } catch (Exception e) {
                log.error("Failed to auto-reschedule reminder ID: {}, error: {}", reminder.getId(), e.getMessage());
            }
        }
        
        return processedReminders;
    }

    @Override
    public RemindScheduleDto markAsSent(UUID reminderId, UUID userId) {
        log.info("Marking reminder as sent: {} for user: {}", reminderId, userId);
        
        RemindSchedule reminder = getReminderWithOwnershipCheck(reminderId, userId);
        reminder.setStatus(RemindStatus.SENT);
        reminder.setSentAt(LocalDateTime.now());
        
        RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
        return remindScheduleMapper.toDto(savedReminder);
    }

    @Override
    public RemindScheduleDto markAsCompleted(UUID reminderId, UUID userId, Integer score, String notes) {
        log.info("Marking reminder as completed: {} for user: {} with score: {}", reminderId, userId, score);
        
        RemindSchedule reminder = getReminderWithOwnershipCheck(reminderId, userId);
        reminder.setStatus(RemindStatus.DONE);
        reminder.setCompletedAt(LocalDateTime.now());
        reminder.setReviewScore(score);
        reminder.setReviewNotes(notes);
        
        RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
        
        // TODO: Trigger cycle completion logic if this was the 5th review
        // This would integrate with SetReviewService to create the review record
        
        return remindScheduleMapper.toDto(savedReminder);
    }

    @Override
    public RemindScheduleDto markAsSkipped(UUID reminderId, UUID userId, String reason) {
        log.info("Marking reminder as skipped: {} for user: {} with reason: {}", reminderId, userId, reason);
        
        RemindSchedule reminder = getReminderWithOwnershipCheck(reminderId, userId);
        reminder.setStatus(RemindStatus.SKIPPED);
        reminder.setReviewNotes(reason);
        
        RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
        return remindScheduleMapper.toDto(savedReminder);
    }

    @Override
    public RemindScheduleDto rescheduleReminder(UUID reminderId, UUID userId, LocalDate newDate, String reason) {
        log.info("Rescheduling reminder: {} for user: {} to date: {} with reason: {}", 
                reminderId, userId, newDate, reason);
        
        RemindSchedule reminder = getReminderWithOwnershipCheck(reminderId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check daily limit for new date
        long dailyCount = remindScheduleRepository.countRemindersScheduledOnDateForUser(newDate, userId);
        if (dailyCount >= MAX_REMINDERS_PER_DAY) {
            throw new IllegalStateException("Daily reminder limit reached for new date (3 reminders per day)");
        }
        
        reminder.setStatus(RemindStatus.RESCHEDULED);
        reminder.setScheduledDate(newDate);
        reminder.setRescheduledBy(user);
        reminder.setRescheduledAt(LocalDateTime.now());
        reminder.setRescheduleReason(reason);
        
        RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
        return remindScheduleMapper.toDto(savedReminder);
    }

    @Override
    public RemindScheduleDto cancelReminder(UUID reminderId, UUID userId, String reason) {
        log.info("Cancelling reminder: {} for user: {} with reason: {}", reminderId, userId, reason);
        
        RemindSchedule reminder = getReminderWithOwnershipCheck(reminderId, userId);
        reminder.setStatus(RemindStatus.CANCELLED);
        reminder.setReviewNotes(reason);
        
        RemindSchedule savedReminder = remindScheduleRepository.save(reminder);
        return remindScheduleMapper.toDto(savedReminder);
    }

    @Override
    public List<RemindScheduleDto> autoRescheduleOverflowReminders(LocalDate date) {
        log.info("Auto-rescheduling overflow reminders for date: {}", date);
        
        List<RemindScheduleDto> rescheduledReminders = new ArrayList<>();
        
        // Get all users with more than 3 reminders on the date
        List<UUID> userIds = remindScheduleRepository.findUserIdsWithOverflowReminders(date, MAX_REMINDERS_PER_DAY);
        
        for (UUID userId : userIds) {
            List<RemindSchedule> overflowReminders = remindScheduleRepository
                    .findOverflowRemindersForUser(date, userId, MAX_REMINDERS_PER_DAY);
            
            // Apply limit manually since JPA doesn't support LIMIT in JPQL
            List<RemindSchedule> limitedOverflowReminders = overflowReminders.stream()
                    .limit(MAX_REMINDERS_PER_DAY)
                    .collect(Collectors.toList());
            
            for (int i = 0; i < limitedOverflowReminders.size(); i++) {
                RemindSchedule reminder = limitedOverflowReminders.get(i);
                LocalDate newDate = date.plusDays(i + 1); // Spread across subsequent days
                
                try {
                    RemindScheduleDto rescheduled = rescheduleReminder(
                        reminder.getId(), 
                        userId, 
                        newDate, 
                        "Auto-rescheduled due to daily limit overflow"
                    );
                    rescheduledReminders.add(rescheduled);
                } catch (Exception e) {
                    log.error("Failed to auto-reschedule reminder ID: {}, error: {}", reminder.getId(), e.getMessage());
                }
            }
        }
        
        log.info("Auto-rescheduled {} overflow reminders", rescheduledReminders.size());
        return rescheduledReminders;
    }

    @Override
    public List<RemindScheduleDto> getRemindersReadyForProcessing(LocalDate date) {
        log.debug("Getting reminders ready for processing on date: {}", date);
        
        return remindScheduleRepository.findPendingRemindersOnDate(date)
                .stream()
                .map(remindScheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DailyReminderStatistics getDailyReminderStatistics(LocalDate date) {
        log.debug("Getting daily reminder statistics for date: {}", date);
        
        long totalPending = remindScheduleRepository.countByStatusAndDate(RemindStatus.PENDING, date);
        long totalSent = remindScheduleRepository.countByStatusAndDate(RemindStatus.SENT, date);
        long totalCompleted = remindScheduleRepository.countByStatusAndDate(RemindStatus.DONE, date);
        long totalSkipped = remindScheduleRepository.countByStatusAndDate(RemindStatus.SKIPPED, date);
        long totalRescheduled = remindScheduleRepository.countByStatusAndDate(RemindStatus.RESCHEDULED, date);
        long totalCancelled = remindScheduleRepository.countByStatusAndDate(RemindStatus.CANCELLED, date);
        long usersWithReminders = remindScheduleRepository.countUsersWithRemindersOnDate(date);
        long notificationsSent = totalSent; // Assuming each sent reminder corresponds to a notification
        
        return new DailyReminderStatistics(
                totalPending,
                totalSent,
                totalCompleted,
                totalSkipped,
                totalRescheduled,
                totalCancelled,
                usersWithReminders,
                notificationsSent
        );
    }

    private RemindSchedule getReminderWithOwnershipCheck(UUID reminderId, UUID userId) {
        RemindSchedule reminder = remindScheduleRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with ID: " + reminderId));
        
        if (!reminder.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Reminder not found with ID: " + reminderId + " for user ID: " + userId);
        }
        
        return reminder;
    }
} 
