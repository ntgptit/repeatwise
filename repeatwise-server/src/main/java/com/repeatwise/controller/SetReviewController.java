package com.repeatwise.controller;

import com.repeatwise.dto.SetReviewDto;
import com.repeatwise.service.SetReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/set-reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "Set Review Management", description = "APIs for set review management operations")
public class SetReviewController {

    private final SetReviewService setReviewService;

    @PostMapping
    @Operation(summary = "Submit a review", description = "Submits a review with score for a set cycle")
    public ResponseEntity<SetReviewDto> submitReview(@Valid @RequestBody SetReviewDto reviewDto, @RequestParam UUID cycleId, @RequestParam UUID userId) {
        SetReviewDto createdReview = setReviewService.createReview(cycleId, userId, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review by ID", description = "Retrieves review information by review ID")
    public ResponseEntity<SetReviewDto> getReviewById(@PathVariable UUID id) {
        return setReviewService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cycle/{cycleId}")
    @Operation(summary = "Create a new review", description = "Creates a new review for a cycle (automatically finishes cycle when 5 reviews are completed)")
    public ResponseEntity<SetReviewDto> createReview(
            @PathVariable UUID cycleId,
            @RequestParam UUID userId,
            @Valid @RequestBody SetReviewDto reviewDto) {
        SetReviewDto review = setReviewService.createReview(cycleId, userId, reviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/cycle/{cycleId}")
    @Operation(summary = "Get reviews by cycle", description = "Retrieves all reviews for a specific cycle")
    public ResponseEntity<List<SetReviewDto>> getReviewsByCycle(@PathVariable UUID cycleId) {
        List<SetReviewDto> reviews = setReviewService.findByCycleId(cycleId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/set/{setId}")
    @Operation(summary = "Get reviews by set", description = "Retrieves all reviews for a specific set")
    public ResponseEntity<List<SetReviewDto>> getReviewsBySet(@PathVariable UUID setId) {
        List<SetReviewDto> reviews = setReviewService.findBySetId(setId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get reviews by user", description = "Retrieves all reviews for a specific user")
    public ResponseEntity<List<SetReviewDto>> getReviewsByUser(@PathVariable UUID userId) {
        List<SetReviewDto> reviews = setReviewService.findByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update review", description = "Updates review information")
    public ResponseEntity<SetReviewDto> updateReview(@PathVariable UUID id, @Valid @RequestBody SetReviewDto reviewDto, @RequestParam UUID userId) {
        SetReviewDto updatedReview = setReviewService.updateReview(id, userId, reviewDto);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete review", description = "Soft deletes a review")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id, @RequestParam UUID userId) {
        setReviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cycle/{cycleId}/statistics")
    @Operation(summary = "Get review statistics", description = "Retrieves statistics for reviews in a cycle")
    public ResponseEntity<SetReviewService.ReviewStatistics> getReviewStatistics(@PathVariable UUID cycleId) {
        SetReviewService.ReviewStatistics stats = setReviewService.getReviewStatistics(cycleId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/cycle/{cycleId}/ordered")
    @Operation(summary = "Get reviews ordered by review number", description = "Retrieves all reviews for a cycle ordered by review number")
    public ResponseEntity<List<SetReviewDto>> getReviewsOrdered(@PathVariable UUID cycleId) {
        List<SetReviewDto> reviews = setReviewService.findReviewsByCycleIdOrderByReviewNo(cycleId);
        return ResponseEntity.ok(reviews);
    }
} 
