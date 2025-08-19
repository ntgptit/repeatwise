package com.spacedlearning.controller;

import com.spacedlearning.dto.reminder.RemindScheduleCreateRequest;
import com.spacedlearning.dto.reminder.RemindScheduleResponse;
import com.spacedlearning.dto.reminder.RemindScheduleUpdateRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.service.RemindScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reminder Schedules", description = "API for managing reminder schedules in the RepeatWise spaced repetition system")
public class RemindScheduleController {

    private final RemindScheduleService remindScheduleService;

    @PostMapping
    @Operation(summary = "Create a new reminder", description = "Creates a new reminder schedule for a learning set")
    public ResponseEntity<RemindScheduleResponse> createReminder(
            @Valid @RequestBody RemindScheduleCreateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating reminder for set: {} on date: {}", request.getSetId(), request.getRemindDate());
        RemindScheduleResponse response = remindScheduleService.createReminder(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{reminderId}")
    @Operation(summary = "Get a reminder", description = "Retrieves a specific reminder schedule by ID")
    public ResponseEntity<RemindScheduleResponse> getReminder(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reminder: {}", reminderId);
        RemindScheduleResponse response = remindScheduleService.getReminder(reminderId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reminderId}")
    @Operation(summary = "Update a reminder", description = "Updates an existing reminder schedule")
    public ResponseEntity<RemindScheduleResponse> updateReminder(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @Valid @RequestBody RemindScheduleUpdateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Updating reminder: {}", reminderId);
        RemindScheduleResponse response = remindScheduleService.updateReminder(reminderId, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reminderId}")
    @Operation(summary = "Delete a reminder", description = "Deletes a reminder schedule")
    public ResponseEntity<Void> deleteReminder(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Deleting reminder: {}", reminderId);
        remindScheduleService.deleteReminder(reminderId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/set/{setId}")
    @Operation(summary = "Get reminders by set", description = "Retrieves all reminder schedules for a specific learning set")
    public ResponseEntity<List<RemindScheduleResponse>> getRemindersBySet(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reminders for set: {}", setId);
        List<RemindScheduleResponse> response = remindScheduleService.getRemindersBySet(setId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get reminders by date", description = "Retrieves all reminder schedules for a specific date")
    public ResponseEntity<List<RemindScheduleResponse>> getRemindersByDate(
            @Parameter(description = "Reminder date") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reminders for date: {}", date);
        List<RemindScheduleResponse> response = remindScheduleService.getRemindersByDate(date, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reminderId}/mark-sent")
    @Operation(summary = "Mark reminder as sent", description = "Marks a reminder as sent")
    public ResponseEntity<Void> markReminderAsSent(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Marking reminder as sent: {}", reminderId);
        remindScheduleService.markReminderAsSent(reminderId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reminderId}/mark-done")
    @Operation(summary = "Mark reminder as done", description = "Marks a reminder as completed")
    public ResponseEntity<Void> markReminderAsDone(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Marking reminder as done: {}", reminderId);
        remindScheduleService.markReminderAsDone(reminderId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reminderId}/mark-skipped")
    @Operation(summary = "Mark reminder as skipped", description = "Marks a reminder as skipped")
    public ResponseEntity<Void> markReminderAsSkipped(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Marking reminder as skipped: {}", reminderId);
        remindScheduleService.markReminderAsSkipped(reminderId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{reminderId}/can-reschedule")
    @Operation(summary = "Check if reminder can be rescheduled", description = "Checks if a reminder can still be rescheduled")
    public ResponseEntity<Boolean> canReschedule(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @AuthenticationPrincipal User user) {
        
        log.info("Checking if reminder can be rescheduled: {}", reminderId);
        boolean canReschedule = remindScheduleService.canReschedule(reminderId, user);
        return ResponseEntity.ok(canReschedule);
    }

    @PostMapping("/{reminderId}/reschedule")
    @Operation(summary = "Reschedule reminder", description = "Reschedules a reminder to a new date")
    public ResponseEntity<Void> rescheduleReminder(
            @Parameter(description = "Reminder ID") @PathVariable UUID reminderId,
            @Parameter(description = "New reminder date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @AuthenticationPrincipal User user) {
        
        log.info("Rescheduling reminder: {} to date: {}", reminderId, newDate);
        remindScheduleService.rescheduleReminder(reminderId, newDate, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue reminders", description = "Retrieves all overdue reminder schedules")
    public ResponseEntity<List<RemindScheduleResponse>> getOverdueReminders(
            @AuthenticationPrincipal User user) {
        
        log.info("Getting overdue reminders for user: {}", user.getId());
        List<RemindScheduleResponse> response = remindScheduleService.getOverdueReminders(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    @Operation(summary = "Get reminders to send today", description = "Retrieves all reminders that need to be sent today")
    public ResponseEntity<List<RemindScheduleResponse>> getRemindersToSendToday() {
        
        log.info("Getting reminders to send today");
        List<RemindScheduleResponse> response = remindScheduleService.getRemindersToSendToday();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/handle-overload")
    @Operation(summary = "Handle overload", description = "Handles overload by rescheduling excess reminders")
    public ResponseEntity<Void> handleOverload(
            @Parameter(description = "Date to handle overload for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        log.info("Handling overload for user: {} on date: {}", user.getId(), date);
        remindScheduleService.handleOverload(user, date);
        return ResponseEntity.ok().build();
    }
}
