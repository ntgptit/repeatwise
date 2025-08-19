package com.spacedlearning.controller;

import com.spacedlearning.dto.set.LearningSetCreateRequest;
import com.spacedlearning.dto.set.LearningSetDetailResponse;
import com.spacedlearning.dto.set.LearningSetResponse;
import com.spacedlearning.dto.set.LearningSetUpdateRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.service.LearningSetService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning-sets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Sets", description = "API for managing learning sets in the RepeatWise spaced repetition system")
public class LearningSetController {

    private final LearningSetService learningSetService;

    @PostMapping
    @Operation(summary = "Create a new learning set", description = "Creates a new learning set for the authenticated user")
    public ResponseEntity<LearningSetResponse> createSet(
            @Valid @RequestBody LearningSetCreateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating learning set: {}", request.getName());
        LearningSetResponse response = learningSetService.createSet(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{setId}")
    @Operation(summary = "Get a learning set", description = "Retrieves a specific learning set by ID")
    public ResponseEntity<LearningSetResponse> getSet(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting learning set: {}", setId);
        LearningSetResponse response = learningSetService.getSet(setId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{setId}/detail")
    @Operation(summary = "Get detailed learning set", description = "Retrieves a detailed view of a learning set including review history and reminders")
    public ResponseEntity<LearningSetDetailResponse> getSetDetail(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting detailed learning set: {}", setId);
        LearningSetDetailResponse response = learningSetService.getSetDetail(setId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{setId}")
    @Operation(summary = "Update a learning set", description = "Updates an existing learning set")
    public ResponseEntity<LearningSetResponse> updateSet(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @Valid @RequestBody LearningSetUpdateRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Updating learning set: {}", setId);
        LearningSetResponse response = learningSetService.updateSet(setId, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{setId}")
    @Operation(summary = "Delete a learning set", description = "Soft deletes a learning set")
    public ResponseEntity<Void> deleteSet(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Deleting learning set: {}", setId);
        learningSetService.deleteSet(setId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get user's learning sets", description = "Retrieves all learning sets for the authenticated user with pagination")
    public ResponseEntity<Page<LearningSetResponse>> getUserSets(
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting learning sets for user: {}", user.getId());
        Page<LearningSetResponse> response = learningSetService.getUserSets(user, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get learning sets by category", description = "Retrieves learning sets filtered by category")
    public ResponseEntity<Page<LearningSetResponse>> getSetsByCategory(
            @Parameter(description = "Set category") @PathVariable SetCategory category,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting learning sets by category: {} for user: {}", category, user.getId());
        Page<LearningSetResponse> response = learningSetService.getUserSetsByCategory(user, category, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search learning sets", description = "Searches learning sets by name")
    public ResponseEntity<Page<LearningSetResponse>> searchSets(
            @Parameter(description = "Search term") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User user) {
        
        log.info("Searching learning sets with term: '{}' for user: {}", q, user.getId());
        Page<LearningSetResponse> response = learningSetService.searchUserSets(user, q, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{setId}/start-learning")
    @Operation(summary = "Start learning a set", description = "Marks a set as being in learning status")
    public ResponseEntity<Void> startLearning(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Starting learning for set: {}", setId);
        learningSetService.startLearning(setId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{setId}/start-reviewing")
    @Operation(summary = "Start reviewing a set", description = "Marks a set as being in reviewing status")
    public ResponseEntity<Void> startReviewing(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Starting reviewing for set: {}", setId);
        learningSetService.startReviewing(setId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{setId}/mark-mastered")
    @Operation(summary = "Mark set as mastered", description = "Marks a set as mastered")
    public ResponseEntity<Void> markAsMastered(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Marking set as mastered: {}", setId);
        learningSetService.markAsMastered(setId, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/due-for-review")
    @Operation(summary = "Get sets due for review", description = "Retrieves sets that are due for review on a specific date")
    public ResponseEntity<List<LearningSetResponse>> getSetsDueForReview(
            @Parameter(description = "Review date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting sets due for review on: {} for user: {}", date, user.getId());
        List<LearningSetResponse> response = learningSetService.getSetsDueForReview(user, date)
            .stream()
            .map(set -> learningSetService.getSet(set.getId(), user))
            .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue sets", description = "Retrieves sets that are overdue for review")
    public ResponseEntity<List<LearningSetResponse>> getOverdueSets(
            @Parameter(description = "Current date") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting overdue sets for user: {} as of: {}", user.getId(), date);
        List<LearningSetResponse> response = learningSetService.getOverdueSets(user, date)
            .stream()
            .map(set -> learningSetService.getSet(set.getId(), user))
            .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{setId}/schedule-next-cycle")
    @Operation(summary = "Schedule next cycle", description = "Schedules the next learning cycle for a set")
    public ResponseEntity<Void> scheduleNextCycle(
            @Parameter(description = "Learning set ID") @PathVariable UUID setId,
            @AuthenticationPrincipal User user) {
        
        log.info("Scheduling next cycle for set: {}", setId);
        learningSetService.scheduleNextCycle(setId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/handle-overload")
    @Operation(summary = "Handle overload", description = "Handles overload by rescheduling excess sets")
    public ResponseEntity<Void> handleOverload(
            @Parameter(description = "Date to handle overload for") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal User user) {
        
        log.info("Handling overload for user: {} on date: {}", user.getId(), date);
        learningSetService.handleOverload(user, date);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats/count-by-status")
    @Operation(summary = "Get set count by status", description = "Retrieves count of sets grouped by status")
    public ResponseEntity<Long> getSetCountByStatus(
            @Parameter(description = "Set status") @RequestParam String status,
            @AuthenticationPrincipal User user) {
        
        log.info("Getting set count by status: {} for user: {}", status, user.getId());
        long count = learningSetService.countSetsByStatus(user, com.spacedlearning.entity.enums.SetStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(count);
    }
}
