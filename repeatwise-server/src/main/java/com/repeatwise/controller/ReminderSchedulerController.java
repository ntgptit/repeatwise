package com.repeatwise.controller;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.service.ReminderSchedulerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reminder-scheduler")
@RequiredArgsConstructor
@Tag(name = "Reminder Scheduler", description = "APIs for production-ready reminder scheduling operations")
public class ReminderSchedulerController {

    private final ReminderSchedulerService reminderSchedulerService;

    @PostMapping("/process-daily")
    @Operation(summary = "Process daily reminders", description = "Processes all pending reminders for today, applies 3-set-per-day rule")
    public ResponseEntity<List<RemindScheduleDto>> processDailyReminders(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> processedReminders = reminderSchedulerService.processDailyReminders(processDate);
        return ResponseEntity.ok(processedReminders);
    }

    @PostMapping("/process-user/{userId}")
    @Operation(summary = "Process user reminders", description = "Processes reminders for a specific user, applies 3-set-per-day rule")
    public ResponseEntity<List<RemindScheduleDto>> processUserReminders(
            @PathVariable UUID userId,
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> processedReminders = reminderSchedulerService.processUserReminders(userId, processDate);
        return ResponseEntity.ok(processedReminders);
    }

    @PostMapping("/reminders/{reminderId}/mark-sent")
    @Operation(summary = "Mark reminder as sent", description = "Marks a reminder as sent after notification is delivered")
    public ResponseEntity<RemindScheduleDto> markAsSent(
            @PathVariable UUID reminderId,
            @RequestParam UUID userId) {
        RemindScheduleDto reminder = reminderSchedulerService.markAsSent(reminderId, userId);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping("/reminders/{reminderId}/mark-completed")
    @Operation(summary = "Mark reminder as completed", description = "Marks a reminder as completed when user finishes review")
    public ResponseEntity<RemindScheduleDto> markAsCompleted(
            @PathVariable UUID reminderId,
            @RequestParam UUID userId,
            @RequestParam Integer score,
            @RequestParam(required = false) String notes) {
        RemindScheduleDto reminder = reminderSchedulerService.markAsCompleted(reminderId, userId, score, notes);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping("/reminders/{reminderId}/mark-skipped")
    @Operation(summary = "Mark reminder as skipped", description = "Marks a reminder as skipped when user skips review")
    public ResponseEntity<RemindScheduleDto> markAsSkipped(
            @PathVariable UUID reminderId,
            @RequestParam UUID userId,
            @RequestParam String reason) {
        RemindScheduleDto reminder = reminderSchedulerService.markAsSkipped(reminderId, userId, reason);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping("/reminders/{reminderId}/reschedule")
    @Operation(summary = "Reschedule reminder", description = "Reschedules a reminder to a new date")
    public ResponseEntity<RemindScheduleDto> rescheduleReminder(
            @PathVariable UUID reminderId,
            @RequestParam UUID userId,
            @RequestParam String newDate,
            @RequestParam String reason) {
        LocalDate targetDate = LocalDate.parse(newDate);
        RemindScheduleDto reminder = reminderSchedulerService.rescheduleReminder(reminderId, userId, targetDate, reason);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping("/reminders/{reminderId}/cancel")
    @Operation(summary = "Cancel reminder", description = "Cancels a reminder")
    public ResponseEntity<RemindScheduleDto> cancelReminder(
            @PathVariable UUID reminderId,
            @RequestParam UUID userId,
            @RequestParam String reason) {
        RemindScheduleDto reminder = reminderSchedulerService.cancelReminder(reminderId, userId, reason);
        return ResponseEntity.ok(reminder);
    }

    @PostMapping("/auto-reschedule-overflow")
    @Operation(summary = "Auto-reschedule overflow reminders", description = "Automatically reschedules reminders that exceed daily limit")
    public ResponseEntity<List<RemindScheduleDto>> autoRescheduleOverflowReminders(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> rescheduledReminders = reminderSchedulerService.autoRescheduleOverflowReminders(processDate);
        return ResponseEntity.ok(rescheduledReminders);
    }

    @GetMapping("/reminders/ready-for-processing")
    @Operation(summary = "Get reminders ready for processing", description = "Gets all pending reminders that are ready to be processed")
    public ResponseEntity<List<RemindScheduleDto>> getRemindersReadyForProcessing(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> readyReminders = reminderSchedulerService.getRemindersReadyForProcessing(processDate);
        return ResponseEntity.ok(readyReminders);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get daily reminder statistics", description = "Gets comprehensive statistics for reminder processing")
    public ResponseEntity<ReminderSchedulerService.DailyReminderStatistics> getDailyReminderStatistics(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        ReminderSchedulerService.DailyReminderStatistics stats = reminderSchedulerService.getDailyReminderStatistics(processDate);
        return ResponseEntity.ok(stats);
    }
} 
