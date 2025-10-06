package com.spacedlearning.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.reminder.ReminderRequest;
import com.spacedlearning.dto.reminder.ReminderResponse;
import com.spacedlearning.dto.review.ReviewSessionRequest;
import com.spacedlearning.dto.review.ReviewSessionResponse;
import com.spacedlearning.dto.set.SetCreationRequest;
import com.spacedlearning.dto.set.SetCreationResponse;
import com.spacedlearning.dto.statistics.StatisticsResponse;
import com.spacedlearning.dto.user.UserRegistrationRequest;
import com.spacedlearning.dto.user.UserRegistrationResponse;
import com.spacedlearning.service.DataMaskingIntegrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Example controller demonstrating DTO usage and validation
 * This controller shows how to use the created DTOs, mappers, and validation
 */
@RestController
@RequestMapping("/api/v1/examples")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Example API", description = "Example endpoints demonstrating DTO usage")
public class ExampleController {


    @PostMapping("/user-registration")
    @Operation(summary = "Example user registration", description = "Demonstrates UserRegistrationRequest validation and response")
    public ResponseEntity<DataResponse<UserRegistrationResponse>> exampleUserRegistration(
            @Valid @RequestBody UserRegistrationRequest request) {
        
        log.info("Example user registration request: {}", request.getEmail());
        
        // In a real implementation, you would:
        // 1. Map request to entity using UserMapper
        // 2. Save entity using UserService
        // 3. Map entity to response using DataMaskingIntegrationService
        
        // For demonstration, create a mock response
        UserRegistrationResponse response = UserRegistrationResponse.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .preferredLanguage(request.getPreferredLanguage())
                .timezone(request.getTimezone())
                .defaultReminderTime(request.getDefaultReminderTime())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.of(response));
    }

    @PostMapping("/set-creation")
    @Operation(summary = "Example set creation", description = "Demonstrates SetCreationRequest validation and response")
    public ResponseEntity<DataResponse<SetCreationResponse>> exampleSetCreation(
            @Valid @RequestBody SetCreationRequest request) {
        
        log.info("Example set creation request: {}", request.getName());
        
        // In a real implementation, you would:
        // 1. Map request to entity using LearningSetMapper
        // 2. Save entity using LearningSetService
        // 3. Map entity to response using DataMaskingIntegrationService
        
        // For demonstration, create a mock response
        SetCreationResponse response = SetCreationResponse.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.of(response));
    }

    @PostMapping("/review-session")
    @Operation(summary = "Example review session", description = "Demonstrates ReviewSessionRequest validation and response")
    public ResponseEntity<DataResponse<ReviewSessionResponse>> exampleReviewSession(
            @Valid @RequestBody ReviewSessionRequest request) {
        
        log.info("Example review session request for set: {}", request.getSetId());
        
        // In a real implementation, you would:
        // 1. Map request to entity using ReviewHistoryMapper
        // 2. Save entity using ReviewHistoryService
        // 3. Map entity to response using DataMaskingIntegrationService
        
        // For demonstration, create a mock response
        ReviewSessionResponse response = ReviewSessionResponse.builder()
                .id(UUID.randomUUID())
                .setId(request.getSetId())
                .cycleId(request.getCycleId())
                .reviewNumber(request.getReviewNumber())
                .score(request.getScore())
                .status(request.getStatus())
                .skipReason(request.getSkipReason())
                .reviewDate(request.getReviewDate())
                .notes(request.getNotes())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.of(response));
    }

    @PostMapping("/reminder")
    @Operation(summary = "Example reminder creation", description = "Demonstrates ReminderRequest validation and response")
    public ResponseEntity<DataResponse<ReminderResponse>> exampleReminderCreation(
            @Valid @RequestBody ReminderRequest request) {
        
        log.info("Example reminder creation request for set: {}", request.getSetId());
        
        // In a real implementation, you would:
        // 1. Map request to entity using RemindScheduleMapper
        // 2. Save entity using RemindScheduleService
        // 3. Map entity to response using DataMaskingIntegrationService
        
        // For demonstration, create a mock response
        ReminderResponse response = ReminderResponse.builder()
                .id(UUID.randomUUID())
                .setId(request.getSetId())
                .remindDate(request.getRemindDate())
                .status(request.getStatus())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.of(response));
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "Example statistics", description = "Demonstrates StatisticsResponse structure")
    public ResponseEntity<DataResponse<StatisticsResponse>> exampleStatistics(
            @PathVariable UUID userId) {
        
        log.info("Example statistics request for user: {}", userId);
        
        // In a real implementation, you would:
        // 1. Fetch user data using UserService
        // 2. Calculate statistics using StatisticsService
        // 3. Map to response using StatisticsMapper
        
        // For demonstration, create a mock response
        StatisticsResponse response = StatisticsResponse.builder()
                .totalSets(5)
                .activeSets(3)
                .completedSets(2)
                .totalReviews(150)
                .averageScore(java.math.BigDecimal.valueOf(85.5))
                .currentStreak(7)
                .longestStreak(15)
                .reviewsToday(5)
                .reviewsThisWeek(25)
                .reviewsThisMonth(100)
                .build();
        
        return ResponseEntity.ok(DataResponse.of(response));
    }
}
