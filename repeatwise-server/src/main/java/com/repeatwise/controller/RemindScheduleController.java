package com.repeatwise.controller;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.service.RemindScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/remind-schedules")
@RequiredArgsConstructor
@Validated
@Tag(name = "Remind Schedule Management", description = "APIs for remind schedule CRUD operations")
public class RemindScheduleController {

    private final RemindScheduleService remindScheduleService;

    @PostMapping
    @Operation(summary = "Create a remind schedule", description = "Creates a new remind schedule for a set")
    public ResponseEntity<RemindScheduleDto> createRemindSchedule(@RequestParam UUID userId, @RequestParam UUID setId, @RequestParam LocalDate scheduledDate) {
        RemindScheduleDto createdSchedule = remindScheduleService.createRemindSchedule(userId, setId, scheduledDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get remind schedule by ID", description = "Retrieves remind schedule information by ID")
    public ResponseEntity<RemindScheduleDto> getRemindScheduleById(@PathVariable UUID id) {
        return remindScheduleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/daily")
    @Operation(summary = "Get daily reminders", description = "Retrieves daily reminders for a user (max 3 sets)")
    public ResponseEntity<List<RemindScheduleDto>> getDailyReminders(@PathVariable UUID userId, @RequestParam LocalDate date) {
        List<RemindScheduleDto> reminders = remindScheduleService.getDailyReviewReminders(userId, date);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/user/{userId}/overdue")
    @Operation(summary = "Get overdue reminders", description = "Retrieves overdue reminders for a user")
    public ResponseEntity<List<RemindScheduleDto>> getOverdueReminders(@PathVariable UUID userId, @RequestParam LocalDate today) {
        List<RemindScheduleDto> reminders = remindScheduleService.findOverdueRemindersForUser(today, userId);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reminders by user", description = "Retrieves all reminders for a specific user")
    public ResponseEntity<List<RemindScheduleDto>> getRemindersByUser(@PathVariable UUID userId) {
        List<RemindScheduleDto> reminders = remindScheduleService.findByUserId(userId);
        return ResponseEntity.ok(reminders);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update remind schedule", description = "Updates remind schedule information")
    public ResponseEntity<RemindScheduleDto> updateRemindSchedule(@PathVariable UUID id, @Valid @RequestBody RemindScheduleDto remindScheduleDto, @RequestParam UUID userId) {
        RemindScheduleDto updatedSchedule = remindScheduleService.updateRemindSchedule(id, userId, remindScheduleDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete remind schedule", description = "Soft deletes a remind schedule")
    public ResponseEntity<Void> deleteRemindSchedule(@PathVariable UUID id, @RequestParam UUID userId) {
        remindScheduleService.deleteRemindSchedule(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/statistics")
    @Operation(summary = "Get user reminder statistics", description = "Retrieves reminder statistics for a user")
    public ResponseEntity<RemindScheduleService.RemindStatistics> getUserRemindStatistics(@PathVariable UUID userId) {
        RemindScheduleService.RemindStatistics userStats = remindScheduleService.getRemindStatistics(userId);
        return ResponseEntity.ok(userStats);
    }
} 
