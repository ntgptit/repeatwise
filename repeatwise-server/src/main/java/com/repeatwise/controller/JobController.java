package com.repeatwise.controller;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.dto.NotificationDto;
import com.repeatwise.service.ReminderSchedulerService;
import com.repeatwise.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Management", description = "APIs for managing scheduled jobs")
public class JobController {

    private final ReminderSchedulerService reminderSchedulerService;
    private final NotificationService notificationService;

    @PostMapping("/reminder-scheduler/process")
    @Operation(summary = "Manually trigger reminder processing", description = "Manually triggers reminder processing for a specific time slot")
    public ResponseEntity<List<RemindScheduleDto>> triggerReminderProcessing(
            @RequestParam String timeSlot,
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> processedReminders = reminderSchedulerService.processDailyReminders(processDate);
        return ResponseEntity.ok(processedReminders);
    }

    @PostMapping("/reminder-scheduler/auto-reschedule-overflow")
    @Operation(summary = "Manually trigger auto-reschedule overflow", description = "Manually triggers the auto-reschedule overflow job (8:00 PM)")
    public ResponseEntity<List<RemindScheduleDto>> triggerAutoRescheduleOverflow(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> rescheduledReminders = reminderSchedulerService.autoRescheduleOverflowReminders(processDate);
        return ResponseEntity.ok(rescheduledReminders);
    }

    @PostMapping("/notification-scheduler/send-morning")
    @Operation(summary = "Manually trigger morning notifications", description = "Manually triggers the morning study notifications job (7:15 AM)")
    public ResponseEntity<List<NotificationDto>> triggerMorningNotifications() {
        return triggerNotifications();
    }

    @PostMapping("/notification-scheduler/send-lunch")
    @Operation(summary = "Manually trigger lunch notifications", description = "Manually triggers the lunch break notifications job (12:15 PM)")
    public ResponseEntity<List<NotificationDto>> triggerLunchNotifications() {
        return triggerNotifications();
    }

    @PostMapping("/notification-scheduler/send-evening")
    @Operation(summary = "Manually trigger evening notifications", description = "Manually triggers the evening study notifications job (7:15 PM)")
    public ResponseEntity<List<NotificationDto>> triggerEveningNotifications() {
        return triggerNotifications();
    }

    private ResponseEntity<List<NotificationDto>> triggerNotifications() {
        var sentNotifications = notificationService.sendScheduledNotifications();
        return ResponseEntity.ok(sentNotifications);
    }

    @PostMapping("/reminder-scheduler/process-user/{userId}")
    @Operation(summary = "Manually trigger user reminder processing", description = "Manually triggers reminder processing for a specific user")
    public ResponseEntity<List<RemindScheduleDto>> triggerUserReminderProcessing(
            @PathVariable UUID userId,
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<RemindScheduleDto> processedReminders = reminderSchedulerService.processUserReminders(userId, processDate);
        return ResponseEntity.ok(processedReminders);
    }

    @GetMapping("/reminder-scheduler/statistics")
    @Operation(summary = "Get job statistics", description = "Gets statistics for reminder processing jobs")
    public ResponseEntity<ReminderSchedulerService.DailyReminderStatistics> getJobStatistics(
            @RequestParam(required = false) String date) {
        LocalDate processDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        var stats = reminderSchedulerService.getDailyReminderStatistics(processDate);
        return ResponseEntity.ok(stats);
    }
} 
