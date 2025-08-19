package com.spacedlearning.service.impl;

import com.spacedlearning.dto.reminder.RemindScheduleCreateRequest;
import com.spacedlearning.dto.reminder.RemindScheduleResponse;
import com.spacedlearning.dto.reminder.RemindScheduleUpdateRequest;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.RemindSchedule;
import com.spacedlearning.entity.SRSConfiguration;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.RemindStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.RemindScheduleMapper;
import com.spacedlearning.repository.LearningSetRepository;
import com.spacedlearning.repository.RemindScheduleRepository;
import com.spacedlearning.repository.SRSConfigurationRepository;
import com.spacedlearning.service.RemindScheduleService;
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
    private final LearningSetRepository learningSetRepository;
    private final SRSConfigurationRepository srsConfigurationRepository;
    private final RemindScheduleMapper remindScheduleMapper;

    @Override
    public RemindScheduleResponse createReminder(RemindScheduleCreateRequest request, User user) {
        log.info("Creating reminder for set: {} by user: {}", request.getSetId(), user.getId());
        
        // Validate that the set belongs to the user
        LearningSet learningSet = getLearningSetByIdAndUser(request.getSetId(), user);
        
        // Check if reminder already exists for this set and date
        Optional<RemindSchedule> existingReminder = remindScheduleRepository.findBySetAndRemindDate(
            learningSet, request.getRemindDate());
        
        if (existingReminder.isPresent()) {
            throw SpacedLearningException.resourceAlreadyExists("Reminder", "combination", 
                String.format("set=%s,date=%s", request.getSetId(), request.getRemindDate()));
        }
        
        // Create new reminder
        RemindSchedule remindSchedule = remindScheduleMapper.toEntity(request);
        remindSchedule.setSet(learningSet);
        remindSchedule.setUser(user);
        
        RemindSchedule savedReminder = remindScheduleRepository.save(remindSchedule);
        log.info("Created reminder with ID: {}", savedReminder.getId());
        
        return remindScheduleMapper.toResponse(savedReminder);
    }

    @Override
    public RemindScheduleResponse updateReminder(UUID reminderId, RemindScheduleUpdateRequest request, User user) {
        log.info("Updating reminder: {} by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        
        // Check if reminder can be rescheduled
        if (request.getRemindDate() != null && !remindSchedule.canReschedule()) {
            throw SpacedLearningException.validationError("Reminder cannot be rescheduled more than 2 times");
        }
        
        remindScheduleMapper.updateEntityFromRequest(request, remindSchedule);
        
        RemindSchedule updatedReminder = remindScheduleRepository.save(remindSchedule);
        log.info("Updated reminder: {}", reminderId);
        
        return remindScheduleMapper.toResponse(updatedReminder);
    }

    @Override
    public void deleteReminder(UUID reminderId, User user) {
        log.info("Deleting reminder: {} by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        remindScheduleRepository.delete(remindSchedule);
        
        log.info("Deleted reminder: {}", reminderId);
    }

    @Override
    public RemindScheduleResponse getReminder(UUID reminderId, User user) {
        log.info("Getting reminder: {} for user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        return remindScheduleMapper.toResponse(remindSchedule);
    }

    @Override
    public List<RemindScheduleResponse> getRemindersBySet(UUID setId, User user) {
        log.info("Getting reminders for set: {} by user: {}", setId, user.getId());
        
        LearningSet learningSet = getLearningSetByIdAndUser(setId, user);
        List<RemindSchedule> reminders = remindScheduleRepository.findBySetOrderByRemindDateAsc(learningSet);
        
        return remindScheduleMapper.toResponseList(reminders);
    }

    @Override
    public List<RemindScheduleResponse> getRemindersByDate(LocalDate date, User user) {
        log.info("Getting reminders for date: {} by user: {}", date, user.getId());
        
        List<RemindSchedule> reminders = remindScheduleRepository.findByUserAndRemindDateOrderByRemindDateAsc(user, date);
        return remindScheduleMapper.toResponseList(reminders);
    }

    @Override
    public void markReminderAsSent(UUID reminderId, User user) {
        log.info("Marking reminder as sent: {} by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        remindSchedule.markAsSent();
        
        remindScheduleRepository.save(remindSchedule);
        log.info("Marked reminder as sent: {}", reminderId);
    }

    @Override
    public void markReminderAsDone(UUID reminderId, User user) {
        log.info("Marking reminder as done: {} by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        remindSchedule.markAsDone();
        
        remindScheduleRepository.save(remindSchedule);
        log.info("Marked reminder as done: {}", reminderId);
    }

    @Override
    public void markReminderAsSkipped(UUID reminderId, User user) {
        log.info("Marking reminder as skipped: {} by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        remindSchedule.markAsSkipped();
        
        remindScheduleRepository.save(remindSchedule);
        log.info("Marked reminder as skipped: {}", reminderId);
    }

    @Override
    public boolean canReschedule(UUID reminderId, User user) {
        log.info("Checking if reminder: {} can be rescheduled by user: {}", reminderId, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        return remindSchedule.canReschedule();
    }

    @Override
    public void rescheduleReminder(UUID reminderId, LocalDate newDate, User user) {
        log.info("Rescheduling reminder: {} to date: {} by user: {}", reminderId, newDate, user.getId());
        
        RemindSchedule remindSchedule = getRemindScheduleByIdAndUser(reminderId, user);
        
        if (!remindSchedule.canReschedule()) {
            throw SpacedLearningException.validationError("Reminder cannot be rescheduled more than 2 times");
        }
        
        remindSchedule.reschedule(newDate);
        remindScheduleRepository.save(remindSchedule);
        
        log.info("Rescheduled reminder: {} to date: {}", reminderId, newDate);
    }

    @Override
    public List<RemindScheduleResponse> getOverdueReminders(User user) {
        log.info("Getting overdue reminders for user: {}", user.getId());
        
        LocalDate today = LocalDate.now();
        List<RemindSchedule> overdueReminders = remindScheduleRepository.findOverdueReminders(user, today);
        
        return remindScheduleMapper.toResponseList(overdueReminders);
    }

    @Override
    public List<RemindScheduleResponse> getRemindersToSendToday() {
        log.info("Getting reminders to send today");
        
        LocalDate today = LocalDate.now();
        List<RemindSchedule> remindersToSend = remindScheduleRepository.findRemindersToSendToday(today);
        
        return remindScheduleMapper.toResponseList(remindersToSend);
    }

    @Override
    public void handleOverload(User user, LocalDate date) {
        log.info("Handling overload for user: {} on date: {}", user.getId(), date);
        
        SRSConfiguration srsConfig = srsConfigurationRepository.findByIsActiveTrue()
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("SRS Configuration", "active"));
        
        int maxSetsPerDay = srsConfig.getMaxSetsPerDay();
        
        // Count reminders for the given date
        long reminderCount = remindScheduleRepository.countByUserAndRemindDateAndStatus(
            user, date, RemindStatus.PENDING);
        
        if (reminderCount <= maxSetsPerDay) {
            log.info("No overload detected for user: {} on date: {}", user.getId(), date);
            return;
        }
        
        // Get all pending reminders for the date
        List<RemindSchedule> pendingReminders = remindScheduleRepository.findByUserAndRemindDateAndStatusOrderByRemindDateAsc(
            user, date, RemindStatus.PENDING);
        
        // Keep only the first maxSetsPerDay reminders (already ordered by priority)
        List<RemindSchedule> remindersToKeep = pendingReminders.subList(0, maxSetsPerDay);
        List<RemindSchedule> remindersToReschedule = pendingReminders.subList(maxSetsPerDay, pendingReminders.size());
        
        // Reschedule the remaining reminders
        for (RemindSchedule reminder : remindersToReschedule) {
            if (reminder.canReschedule()) {
                reminder.reschedule(date.plusDays(1));
                remindScheduleRepository.save(reminder);
                log.info("Rescheduled reminder: {} to date: {}", reminder.getId(), date.plusDays(1));
            } else {
                log.warn("Cannot reschedule reminder: {} - max reschedule count reached", reminder.getId());
            }
        }
        
        log.info("Handled overload for user: {} - kept: {} reminders, rescheduled: {} reminders", 
            user.getId(), remindersToKeep.size(), remindersToReschedule.size());
    }

    // Private helper methods
    private LearningSet getLearningSetByIdAndUser(UUID setId, User user) {
        return learningSetRepository.findByUserAndIdAndDeletedAtIsNull(user, setId)
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("Learning Set", setId));
    }

    private RemindSchedule getRemindScheduleByIdAndUser(UUID reminderId, User user) {
        RemindSchedule remindSchedule = remindScheduleRepository.findById(reminderId)
            .orElseThrow(() -> SpacedLearningException.resourceNotFound("Reminder", reminderId));
        
        // Verify that the reminder belongs to the user
        if (!remindSchedule.getUser().getId().equals(user.getId())) {
            throw SpacedLearningException.forbidden("Access denied to reminder");
        }
        
        return remindSchedule;
    }
}
