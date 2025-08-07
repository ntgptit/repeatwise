package com.repeatwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "API Documentation", description = "Overview of all available APIs")
public class ApiDocumentationController {

    @GetMapping("/documentation")
    @Operation(summary = "Get API documentation", description = "Provides an overview of all available APIs")
    public ResponseEntity<Map<String, Object>> getApiDocumentation() {
        Map<String, Object> documentation = new HashMap<>();
        
        documentation.put("application", "RepeatWise Learning Management System");
        documentation.put("version", "1.0.0");
        documentation.put("description", "REST APIs for managing learning sets, cycles, reviews, and reminders using Spaced Repetition System (SRS)");
        
        Map<String, Object> apiEndpoints = new HashMap<>();
        
        // User Management
        Map<String, Object> userApis = new HashMap<>();
        userApis.put("base_path", "/api/v1/users");
        userApis.put("description", "User management operations");
        Map<String, String> userEndpoints = new HashMap<>();
        userEndpoints.put("POST /", "Create a new user");
        userEndpoints.put("GET /{id}", "Get user by ID");
        userEndpoints.put("GET /", "Get all active users");
        userEndpoints.put("PUT /{id}", "Update user information");
        userEndpoints.put("DELETE /{id}", "Soft delete a user");
        userEndpoints.put("GET /{id}/statistics", "Get user learning statistics");
        userApis.put("endpoints", userEndpoints);
        apiEndpoints.put("users", userApis);
        
        // Set endpoints
        Map<String, String> setEndpoints = new HashMap<>();
        setEndpoints.put("POST /api/v1/sets", "Create a new set");
        setEndpoints.put("GET /api/v1/sets/{id}", "Get set by ID");
        setEndpoints.put("GET /api/v1/sets/user/{userId}", "Get all sets for user");
        setEndpoints.put("GET /api/v1/sets/user/{userId}/daily-review", "Get daily review sets (max 3)");
        setEndpoints.put("PUT /api/v1/sets/{id}", "Update set");
        setEndpoints.put("DELETE /api/v1/sets/{id}", "Delete set");
        setEndpoints.put("POST /api/v1/sets/{id}/start-learning", "Start learning a set");
        setEndpoints.put("POST /api/v1/sets/{id}/mark-mastered", "Mark set as mastered");
        setEndpoints.put("GET /api/v1/sets/{id}/statistics", "Get set statistics");
        apiEndpoints.put("sets", setEndpoints);

        // Cycle endpoints
        Map<String, String> cycleEndpoints = new HashMap<>();
        cycleEndpoints.put("POST /api/v1/cycles/set/{setId}/start", "Start a new cycle");
        cycleEndpoints.put("POST /api/v1/cycles/{id}/finish", "Finish a cycle");
        cycleEndpoints.put("GET /api/v1/cycles/{id}", "Get cycle by ID");
        cycleEndpoints.put("GET /api/v1/cycles/{id}/statistics", "Get cycle statistics");
        cycleEndpoints.put("GET /api/v1/cycles/set/{setId}", "Get all cycles for set");
        cycleEndpoints.put("GET /api/v1/cycles/set/{setId}/active", "Get active cycle for set");
        apiEndpoints.put("cycles", cycleEndpoints);

        // Review endpoints
        Map<String, String> reviewEndpoints = new HashMap<>();
        reviewEndpoints.put("POST /api/v1/reviews/cycle/{cycleId}", "Create a new review");
        reviewEndpoints.put("GET /api/v1/reviews/{id}", "Get review by ID");
        reviewEndpoints.put("GET /api/v1/reviews/cycle/{cycleId}", "Get all reviews for cycle");
        reviewEndpoints.put("GET /api/v1/reviews/cycle/{cycleId}/statistics", "Get review statistics");
        reviewEndpoints.put("GET /api/v1/reviews/cycle/{cycleId}/ordered", "Get reviews ordered by review number");
        apiEndpoints.put("reviews", reviewEndpoints);

        // Notification endpoints
        Map<String, String> notificationEndpoints = new HashMap<>();
        notificationEndpoints.put("GET /api/v1/notifications/user/{userId}", "Get notifications by user");
        notificationEndpoints.put("GET /api/v1/notifications/user/{userId}/unread", "Get unread notifications");
        notificationEndpoints.put("GET /api/v1/notifications/{id}", "Get notification by ID");
        notificationEndpoints.put("POST /api/v1/notifications/{id}/mark-read", "Mark notification as read");
        notificationEndpoints.put("POST /api/v1/notifications/user/{userId}/mark-all-read", "Mark all notifications as read");
        notificationEndpoints.put("DELETE /api/v1/notifications/{id}", "Delete notification");
        notificationEndpoints.put("POST /api/v1/notifications/send-scheduled", "Send scheduled notifications");
        notificationEndpoints.put("GET /api/v1/notifications/user/{userId}/statistics", "Get notification statistics");
        apiEndpoints.put("notifications", notificationEndpoints);

        // Remind Schedule Management
        Map<String, String> remindScheduleEndpoints = new HashMap<>();
        remindScheduleEndpoints.put("POST /api/v1/remind-schedules", "Create a remind schedule");
        remindScheduleEndpoints.put("GET /api/v1/remind-schedules/{id}", "Get remind schedule by ID");
        remindScheduleEndpoints.put("GET /api/v1/remind-schedules/user/{userId}/daily", "Get daily reminders (max 3 sets)");
        remindScheduleEndpoints.put("GET /api/v1/remind-schedules/user/{userId}/overdue", "Get overdue reminders");
        remindScheduleEndpoints.put("GET /api/v1/remind-schedules/user/{userId}", "Get all reminders by user");
        remindScheduleEndpoints.put("PUT /api/v1/remind-schedules/{id}", "Update remind schedule");
        remindScheduleEndpoints.put("DELETE /api/v1/remind-schedules/{id}", "Soft delete a remind schedule");
        remindScheduleEndpoints.put("GET /api/v1/remind-schedules/user/{userId}/statistics", "Get user reminder statistics");
        apiEndpoints.put("remind-schedules", remindScheduleEndpoints);

        // Reminder Scheduler endpoints
        Map<String, String> reminderSchedulerEndpoints = new HashMap<>();
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/process-morning", "Process morning reminders (7:00 AM)");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/process-lunch", "Process lunch break reminders (12:00 PM)");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/process-evening", "Process evening reminders (7:00 PM)");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/process-user/{userId}", "Process user reminders");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/reminders/{reminderId}/mark-sent", "Mark reminder as sent");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/reminders/{reminderId}/mark-completed", "Mark reminder as completed");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/reminders/{reminderId}/mark-skipped", "Mark reminder as skipped");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/reminders/{reminderId}/reschedule", "Reschedule reminder");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/reminders/{reminderId}/cancel", "Cancel reminder");
        reminderSchedulerEndpoints.put("POST /api/v1/reminder-scheduler/auto-reschedule-overflow", "Auto-reschedule overflow reminders (8:00 PM)");
        reminderSchedulerEndpoints.put("GET /api/v1/reminder-scheduler/reminders/ready-for-processing", "Get reminders ready for processing");
        reminderSchedulerEndpoints.put("GET /api/v1/reminder-scheduler/statistics", "Get daily reminder statistics");
        apiEndpoints.put("reminder-scheduler", reminderSchedulerEndpoints);

        // Job Management endpoints
        Map<String, String> jobManagementEndpoints = new HashMap<>();
        jobManagementEndpoints.put("POST /api/v1/jobs/reminder-scheduler/process-morning", "Manually trigger morning reminder processing (7:00 AM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/reminder-scheduler/process-lunch", "Manually trigger lunch break reminder processing (12:00 PM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/reminder-scheduler/process-evening", "Manually trigger evening reminder processing (7:00 PM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/reminder-scheduler/auto-reschedule-overflow", "Manually trigger auto-reschedule overflow (8:00 PM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/notification-scheduler/send-morning", "Manually trigger morning notifications (7:15 AM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/notification-scheduler/send-lunch", "Manually trigger lunch notifications (12:15 PM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/notification-scheduler/send-evening", "Manually trigger evening notifications (7:15 PM)");
        jobManagementEndpoints.put("POST /api/v1/jobs/reminder-scheduler/process-user/{userId}", "Manually trigger user reminder processing");
        jobManagementEndpoints.put("GET /api/v1/jobs/reminder-scheduler/statistics", "Get job statistics");
        apiEndpoints.put("job-management", jobManagementEndpoints);
        
        documentation.put("endpoints", apiEndpoints);
        
        // Business Logic Overview
        Map<String, Object> businessLogic = new HashMap<>();
        businessLogic.put("srs_algorithm", "Spaced Repetition System for optimal learning intervals");
        businessLogic.put("cycle_structure", "Each set has cycles with 5 reviews per cycle");
        businessLogic.put("daily_limit", "Maximum 3 sets per day for review");
        businessLogic.put("score_range", "Review scores from 0-100%");
        businessLogic.put("delay_calculation", "next_cycle_delay_days = base_delay - penalty * (100 - avg_score) + scaling * word_count");
        documentation.put("business_logic", businessLogic);
        
        return ResponseEntity.ok(documentation);
    }
} 
