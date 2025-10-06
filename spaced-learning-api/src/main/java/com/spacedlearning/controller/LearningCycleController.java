package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.learning.PerformReviewRequest;
import com.spacedlearning.dto.learning.PerformReviewResponse;
import com.spacedlearning.dto.learning.StartLearningCycleRequest;
import com.spacedlearning.dto.learning.StartLearningCycleResponse;
import com.spacedlearning.service.LearningCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Learning Cycle operations
 * Implements UC-010: Start Learning Cycle and UC-011: Perform Review Session
 */
@RestController
@RequestMapping("/api/v1/learning-cycles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Cycle API", description = "Endpoints for learning cycle management")
public class LearningCycleController {

    private final LearningCycleService learningCycleService;

    @PostMapping("/start")
    @Operation(summary = "Start Learning Cycle", 
               description = "Start a new learning cycle for a set based on UC-010")
    public ResponseEntity<DataResponse<StartLearningCycleResponse>> startLearningCycle(
            @Valid @RequestBody StartLearningCycleRequest request,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.info("Starting learning cycle for set {} by user {}", request.getSetId(), userId);
        
        StartLearningCycleResponse response = learningCycleService.startLearningCycle(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(response));
    }

    @PostMapping("/review")
    @Operation(summary = "Perform Review Session", 
               description = "Perform a review session for a set based on UC-011")
    public ResponseEntity<DataResponse<PerformReviewResponse>> performReview(
            @Valid @RequestBody PerformReviewRequest request,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.info("Performing review for set {} cycle {} review {} by user {}", 
                request.getSetId(), request.getCycleNumber(), request.getReviewNumber(), userId);
        
        PerformReviewResponse response = learningCycleService.performReview(request, userId);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/{setId}/can-start")
    @Operation(summary = "Check if can start learning cycle", 
               description = "Check if user can start a new learning cycle for a set")
    public ResponseEntity<DataResponse<Boolean>> canStartLearningCycle(
            @Parameter(description = "Set ID") @PathVariable UUID setId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Checking if can start learning cycle for set {} by user {}", setId, userId);
        
        boolean canStart = learningCycleService.canStartLearningCycle(setId, userId);
        return ResponseEntity.ok(DataResponse.of(canStart));
    }

    @GetMapping("/{setId}/can-review")
    @Operation(summary = "Check if can perform review", 
               description = "Check if user can perform review for a set")
    public ResponseEntity<DataResponse<Boolean>> canPerformReview(
            @Parameter(description = "Set ID") @PathVariable UUID setId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Checking if can perform review for set {} by user {}", setId, userId);
        
        boolean canReview = learningCycleService.canPerformReview(setId, userId);
        return ResponseEntity.ok(DataResponse.of(canReview));
    }

    @GetMapping("/{setId}/current-cycle")
    @Operation(summary = "Get current cycle info", 
               description = "Get current learning cycle information for a set")
    public ResponseEntity<DataResponse<StartLearningCycleResponse>> getCurrentCycleInfo(
            @Parameter(description = "Set ID") @PathVariable UUID setId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting current cycle info for set {} by user {}", setId, userId);
        
        StartLearningCycleResponse response = learningCycleService.getCurrentCycleInfo(setId, userId);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    /**
     * Extract current user ID from authentication
     */
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found");
        }
        
        // Assuming the principal contains user ID
        // This might need adjustment based on your authentication setup
        String userIdString = authentication.getName();
        try {
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid user ID in authentication: " + userIdString);
        }
    }
}
