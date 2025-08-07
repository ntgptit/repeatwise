package com.repeatwise.service.impl;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.enums.RemindStatus;
import com.repeatwise.exception.DailyLimitExceededException;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.RemindScheduleMapper;
import com.repeatwise.model.RemindSchedule;
import com.repeatwise.model.Set;
import com.repeatwise.model.User;
import com.repeatwise.repository.RemindScheduleRepository;
import com.repeatwise.repository.SetRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.RemindScheduleService;
import com.repeatwise.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RemindScheduleServiceImpl implements RemindScheduleService {

    private final RemindScheduleRepository remindScheduleRepository;
    private final SetRepository setRepository;
    private final UserRepository userRepository;
    private final RemindScheduleMapper remindScheduleMapper;

    @Override
    public RemindScheduleDto createRemindSchedule(UUID userId, UUID setId, LocalDate scheduledDate) {
        ServiceUtils.logOperationStart("reminder schedule creation", userId, setId, scheduledDate);
        
        User user = ServiceUtils.findEntityOrThrow(
                () -> userRepository.findById(userId), 
                "User", 
                userId
        );
        
        Set set = ServiceUtils.findEntityOrThrow(
                () -> setRepository.findByIdAndUserId(setId, userId), 
                "Set", 
                setId, 
                userId
        );
        
        // Check daily limit (max 3 reminders per day)
        long dailyCount = remindScheduleRepository.countRemindersScheduledOnDateForUser(scheduledDate, userId);
        if (dailyCount >= 3) {
            throw new DailyLimitExceededException("Daily reminder limit reached (3 reminders per day)");
        }
        
        RemindSchedule remindSchedule = RemindSchedule.builder()
                .user(user)
                .set(set)
                .scheduledDate(scheduledDate)
                .status(RemindStatus.PENDING)
                .build();
        
        RemindSchedule savedRemind = remindScheduleRepository.save(remindSchedule);
        ServiceUtils.logOperationSuccess("reminder schedule creation", savedRemind.getId());
        
        return remindScheduleMapper.toDto(savedRemind);
    }

    @Override
    public Optional<RemindScheduleDto> findById(UUID id) {
        ServiceUtils.logEntityLookup("reminder", id);
        return remindScheduleRepository.findById(id)
                .map(remindScheduleMapper::toDto);
    }

    @Override
    public Optional<RemindScheduleDto> findByIdAndUserId(UUID id, UUID userId) {
        ServiceUtils.logEntityLookup("reminder", id, userId);
        return remindScheduleRepository.findByIdAndUserId(id, userId)
                .map(remindScheduleMapper::toDto);
    }

    @Override
    public RemindScheduleDto updateRemindSchedule(UUID id, UUID userId, RemindScheduleDto remindScheduleDto) {
        ServiceUtils.logOperationStart("reminder schedule update", id, userId);
        
        RemindSchedule remindSchedule = ServiceUtils.findEntityOrThrow(
                () -> remindScheduleRepository.findByIdAndUserId(id, userId), 
                "Reminder", 
                id, 
                userId
        );
        
        // Update fields
        remindSchedule.setScheduledDate(remindScheduleDto.getScheduledDate());
        remindSchedule.setStatus(remindScheduleDto.getStatus());
        
        RemindSchedule updatedRemind = remindScheduleRepository.save(remindSchedule);
        ServiceUtils.logOperationSuccess("reminder schedule update", updatedRemind.getId());
        
        return remindScheduleMapper.toDto(updatedRemind);
    }

    @Override
    public void deleteRemindSchedule(UUID id, UUID userId) {
        ServiceUtils.logOperationStart("reminder schedule deletion", id, userId);
        
        RemindSchedule remindSchedule = ServiceUtils.findEntityOrThrow(
                () -> remindScheduleRepository.findByIdAndUserId(id, userId), 
                "Reminder", 
                id, 
                userId
        );
        
        remindScheduleRepository.delete(remindSchedule);
        ServiceUtils.logOperationSuccess("reminder schedule deletion", id);
    }

    @Override
    public List<RemindScheduleDto> findByUserId(UUID userId) {
        log.debug("Finding all reminders for user ID: {}", userId);
        return remindScheduleRepository.findByUserId(userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findByUserIdAndStatus(UUID userId, RemindStatus status) {
        log.debug("Finding reminders for user ID: {} with status: {}", userId, status);
        return remindScheduleRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findRemindersScheduledOnDate(LocalDate date) {
        log.debug("Finding reminders scheduled on date: {}", date);
        return remindScheduleRepository.findRemindersScheduledOnDate(date)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findRemindersScheduledOnDateForUser(LocalDate date, UUID userId) {
        log.debug("Finding reminders scheduled on date: {} for user ID: {}", date, userId);
        return remindScheduleRepository.findRemindersScheduledOnDateForUser(date, userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findPendingRemindersOnDate(LocalDate date) {
        log.debug("Finding pending reminders on date: {}", date);
        return remindScheduleRepository.findPendingRemindersOnDate(date)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findPendingRemindersOnDateForUser(LocalDate date, UUID userId) {
        log.debug("Finding pending reminders on date: {} for user ID: {}", date, userId);
        return remindScheduleRepository.findPendingRemindersOnDateForUser(date, userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findOverdueReminders(LocalDate today) {
        log.debug("Finding overdue reminders for date: {}", today);
        return remindScheduleRepository.findOverdueReminders(today)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findOverdueRemindersForUser(LocalDate today, UUID userId) {
        log.debug("Finding overdue reminders for date: {} and user ID: {}", today, userId);
        return remindScheduleRepository.findOverdueRemindersForUser(today, userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findRemindersScheduledBetweenDates(LocalDate startDate, LocalDate endDate) {
        log.debug("Finding reminders scheduled between dates: {} and {}", startDate, endDate);
        return remindScheduleRepository.findRemindersScheduledBetweenDates(startDate, endDate)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findRemindersScheduledBetweenDatesForUser(LocalDate startDate, LocalDate endDate, UUID userId) {
        log.debug("Finding reminders scheduled between dates: {} and {} for user ID: {}", startDate, endDate, userId);
        return remindScheduleRepository.findRemindersScheduledBetweenDatesForUser(startDate, endDate, userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public long countByUserIdAndStatus(UUID userId, RemindStatus status) {
        return remindScheduleRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public long countRemindersScheduledOnDate(LocalDate date) {
        return remindScheduleRepository.countRemindersScheduledOnDate(date);
    }

    @Override
    public long countRemindersScheduledOnDateForUser(LocalDate date, UUID userId) {
        return remindScheduleRepository.countRemindersScheduledOnDateForUser(date, userId);
    }

    @Override
    public List<RemindScheduleDto> findRemindersToSend(LocalDate today) {
        log.debug("Finding reminders to send for date: {}", today);
        return remindScheduleRepository.findRemindersToSend(today)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> findRemindersToSendForUser(LocalDate today, UUID userId) {
        log.debug("Finding reminders to send for date: {} and user ID: {}", today, userId);
        return remindScheduleRepository.findRemindersToSendForUser(today, userId)
                .stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<LocalDate> findAvailableDatesForUser(UUID userId) {
        log.debug("Finding available dates for user ID: {}", userId);
        return remindScheduleRepository.findAvailableDatesForUser(userId);
    }

    @Override
    public RemindScheduleDto rescheduleReminder(UUID id, UUID userId, LocalDate newDate, String reason) {
        log.info("Rescheduling reminder with ID: {} for user ID: {} to date: {}", id, userId, newDate);
        
        RemindSchedule remindSchedule = remindScheduleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with ID: " + id + " for user ID: " + userId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check daily limit for new date
        long dailyCount = remindScheduleRepository.countRemindersScheduledOnDateForUser(newDate, userId);
        if (dailyCount >= 3) {
            throw new DailyLimitExceededException("Daily reminder limit reached for new date (3 reminders per day)");
        }
        
        remindSchedule.setScheduledDate(newDate);
        remindSchedule.setStatus(RemindStatus.RESCHEDULED);
        remindSchedule.setRescheduledBy(user);
        remindSchedule.setRescheduledAt(java.time.LocalDateTime.now());
        remindSchedule.setRescheduleReason(reason);
        
        RemindSchedule updatedRemind = remindScheduleRepository.save(remindSchedule);
        log.info("Reminder rescheduled successfully with ID: {}", updatedRemind.getId());
        
        return remindScheduleMapper.toDto(updatedRemind);
    }

    @Override
    public RemindScheduleDto markAsSent(UUID id, UUID userId) {
        log.info("Marking reminder as sent with ID: {} for user ID: {}", id, userId);
        
        RemindSchedule remindSchedule = remindScheduleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with ID: " + id + " for user ID: " + userId));
        
        remindSchedule.setStatus(RemindStatus.SENT);
        RemindSchedule updatedRemind = remindScheduleRepository.save(remindSchedule);
        
        log.info("Reminder marked as sent successfully with ID: {}", updatedRemind.getId());
        return remindScheduleMapper.toDto(updatedRemind);
    }

    @Override
    public RemindScheduleDto markAsDone(UUID id, UUID userId) {
        log.info("Marking reminder as done with ID: {} for user ID: {}", id, userId);
        
        RemindSchedule remindSchedule = remindScheduleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with ID: " + id + " for user ID: " + userId));
        
        remindSchedule.setStatus(RemindStatus.DONE);
        RemindSchedule updatedRemind = remindScheduleRepository.save(remindSchedule);
        
        log.info("Reminder marked as done successfully with ID: {}", updatedRemind.getId());
        return remindScheduleMapper.toDto(updatedRemind);
    }

    @Override
    public RemindScheduleDto markAsSkipped(UUID id, UUID userId) {
        log.info("Marking reminder as skipped with ID: {} for user ID: {}", id, userId);
        
        RemindSchedule remindSchedule = remindScheduleRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder not found with ID: " + id + " for user ID: " + userId));
        
        remindSchedule.setStatus(RemindStatus.SKIPPED);
        RemindSchedule updatedRemind = remindScheduleRepository.save(remindSchedule);
        
        log.info("Reminder marked as skipped successfully with ID: {}", updatedRemind.getId());
        return remindScheduleMapper.toDto(updatedRemind);
    }

    @Override
    public List<RemindScheduleDto> getDailyReviewReminders(UUID userId, LocalDate date) {
        log.debug("Getting daily review reminders for user ID: {} on date: {}", userId, date);
        
        // Get pending reminders for the date
        List<RemindSchedule> pendingReminders = remindScheduleRepository.findPendingRemindersOnDateForUser(date, userId);
        
        // Get overdue reminders
        List<RemindSchedule> overdueReminders = remindScheduleRepository.findOverdueRemindersForUser(date, userId);
        
        // Combine and prioritize (overdue first, then by set word count)
        List<RemindSchedule> allReminders = new java.util.ArrayList<>();
        allReminders.addAll(overdueReminders);
        allReminders.addAll(pendingReminders);
        
        // Sort by priority: overdue first, then by word count (descending)
        allReminders.sort((r1, r2) -> {
            boolean r1Overdue = r1.getScheduledDate().isBefore(date);
            boolean r2Overdue = r2.getScheduledDate().isBefore(date);
            
            if (r1Overdue && !r2Overdue) return -1;
            if (!r1Overdue && r2Overdue) return 1;
            
            // Both overdue or both not overdue, sort by word count
            return Integer.compare(r2.getSet().getWordCount(), r1.getSet().getWordCount());
        });
        
        // Limit to 3 reminders per day
        return allReminders.stream()
                .limit(3)
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<RemindScheduleDto> scheduleRemindersForNextCycle(UUID setId, UUID userId, LocalDate nextCycleDate) {
        log.info("Scheduling reminders for next cycle of set ID: {} for user ID: {} on date: {}", setId, userId, nextCycleDate);
        
        Set set = setRepository.findByIdAndUserId(setId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Set not found with ID: " + setId + " for user ID: " + userId));
        
        // Create 5 reminders for the cycle (one for each review)
        List<RemindSchedule> reminders = new java.util.ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            LocalDate reminderDate = nextCycleDate.plusDays(i - 1); // Spread reviews over 5 days
            
            // Check daily limit
            long dailyCount = remindScheduleRepository.countRemindersScheduledOnDateForUser(reminderDate, userId);
            if (dailyCount >= 3) {
                // Find next available date
                List<LocalDate> availableDates = remindScheduleRepository.findAvailableDatesForUser(userId);
                if (!availableDates.isEmpty()) {
                    reminderDate = availableDates.get(0);
                } else {
                    // If no available dates, skip this reminder
                    continue;
                }
            }
            
            RemindSchedule reminder = RemindSchedule.builder()
                    .user(set.getUser())
                    .set(set)
                    .scheduledDate(reminderDate)
                    .status(RemindStatus.PENDING)
                    .build();
            
            reminders.add(reminder);
        }
        
        List<RemindSchedule> savedReminders = remindScheduleRepository.saveAll(reminders);
        log.info("Scheduled {} reminders for next cycle of set ID: {}", savedReminders.size(), setId);
        
        return savedReminders.stream()
                .map(remindScheduleMapper::toDto)
                .toList();
    }

    @Override
    public RemindStatistics getRemindStatistics(UUID userId) {
        log.debug("Getting reminder statistics for user ID: {}", userId);
        
        long totalReminders = remindScheduleRepository.countByUserIdAndStatus(userId, null);
        long pendingReminders = remindScheduleRepository.countByUserIdAndStatus(userId, RemindStatus.PENDING);
        long sentReminders = remindScheduleRepository.countByUserIdAndStatus(userId, RemindStatus.SENT);
        long completedReminders = remindScheduleRepository.countByUserIdAndStatus(userId, RemindStatus.DONE);
        
        LocalDate today = LocalDate.now();
        long overdueReminders = remindScheduleRepository.findOverdueRemindersForUser(today, userId).size();
        
        // Find next reminder date
        LocalDate nextReminderDate = remindScheduleRepository.findAvailableDatesForUser(userId)
                .stream()
                .findFirst()
                .orElse(null);
        
        return new RemindStatistics(
                totalReminders,
                pendingReminders,
                sentReminders,
                completedReminders,
                overdueReminders,
                nextReminderDate
        );
    }
} 
