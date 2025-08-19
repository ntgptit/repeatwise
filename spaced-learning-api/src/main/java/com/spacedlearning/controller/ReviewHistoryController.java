package com.spacedlearning.controller;

import com.spacedlearning.dto.review.ReviewHistoryCreateRequest;
import com.spacedlearning.dto.review.ReviewHistoryResponse;
import com.spacedlearning.dto.review.ReviewHistoryUpdateRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.service.ReviewHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Review History", description = "API for managing review history in the RepeatWise spaced repetition system")
public class ReviewHistoryController {

    private final ReviewHistoryService reviewHistoryService;

    @PostMapping
    @Operation(summary = "Create a new review", description = "Creates a new review history record for a learning set")
    public ResponseEntity<ReviewHistoryResponse> createReview(
            @Valid @RequestBody ReviewHistoryCreateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating review for set: {} cycle: {} review: {}", 
            request.getSetId(), request.getCycleNo(), request.getReviewNo());
        ReviewHistoryResponse response = reviewHistoryService.createReview(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get a review", description = "Retrieves a specific review history record by ID")
    public ResponseEntity<ReviewHistoryResponse> getReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting review: {}", reviewId);
        ReviewHistoryResponse response = reviewHistoryService.getReview(reviewId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update a review", description = "Updates an existing review history record (within 24 hours)")
    public ResponseEntity<ReviewHistoryResponse> updateReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewHistoryUpdateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Updating review: {}", reviewId);
        ReviewHistoryResponse response = reviewHistoryService.updateReview(reviewId, request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/set/{setId}")
    @Operation(summary = "Get reviews by set", description = "Retrieves all review history records for a specific learning set")
    public ResponseEntity<Page<ReviewHistoryResponse>> getReviewsBySet(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reviews for set: {}", setId);
        Page<ReviewHistoryResponse> response = reviewHistoryService.getReviewsBySet(setId, user, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/set/{setId}/cycle/{cycleNo}")
    @Operation(summary = "Get reviews by set and cycle", description = "Retrieves review history records for a specific cycle of a learning set")
    public ResponseEntity<List<ReviewHistoryResponse>> getReviewsBySetAndCycle(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @Parameter(description = "Cycle number") @PathVariable Integer cycleNo,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reviews for set: {} cycle: {}", setId, cycleNo);
        List<ReviewHistoryResponse> response = reviewHistoryService.getReviewsBySetAndCycle(setId, cycleNo, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/set/{setId}/recent")
    @Operation(summary = "Get recent reviews", description = "Retrieves recent review history records (within 24 hours) for a learning set")
    public ResponseEntity<List<ReviewHistoryResponse>> getRecentReviews(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting recent reviews for set: {}", setId);
        List<ReviewHistoryResponse> response = reviewHistoryService.getRecentReviews(setId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/set/{setId}/cycle/{cycleNo}/average-score")
    @Operation(summary = "Get average score for cycle", description = "Calculates the average score for a specific cycle of a learning set")
    public ResponseEntity<Double> getAverageScoreForCycle(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @Parameter(description = "Cycle number") @PathVariable Integer cycleNo,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting average score for set: {} cycle: {}", setId, cycleNo);
        Double avgScore = reviewHistoryService.calculateAverageScoreForCycle(setId, cycleNo, user);
        return ResponseEntity.ok(avgScore);
    }

    @GetMapping("/set/{setId}/date-range")
    @Operation(summary = "Get reviews by date range", description = "Retrieves review history records within a specific date range")
    public ResponseEntity<List<ReviewHistoryResponse>> getReviewsByDateRange(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @Parameter(description = "Start date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting reviews by date range for set: {} from {} to {}", setId, startDate, endDate);
        List<ReviewHistoryResponse> response = reviewHistoryService.getReviewsByDateRange(setId, startDate, endDate, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/all")
    @Operation(summary = "Get all user reviews", description = "Retrieves all review history records for the authenticated user")
    public ResponseEntity<List<ReviewHistoryResponse>> getAllUserReviews(
            @AuthenticationPrincipal User user) {
        
        log.info("Getting all reviews for user: {}", user.getId());
        List<ReviewHistoryResponse> response = reviewHistoryService.getAllUserReviews(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}/can-edit")
    @Operation(summary = "Check if review can be edited", description = "Checks if a review can still be edited (within 24 hours)")
    public ResponseEntity<Boolean> canEditReview(
            @Parameter(description = "Review ID") @PathVariable UUID reviewId,
            @AuthenticationPrincipal User user) {
        
        log.info("Checking if review can be edited: {}", reviewId);
        boolean canEdit = reviewHistoryService.canEditReview(reviewId, user);
        return ResponseEntity.ok(canEdit);
    }
}
