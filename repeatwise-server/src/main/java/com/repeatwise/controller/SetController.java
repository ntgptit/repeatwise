package com.repeatwise.controller;

import com.repeatwise.dto.SetDto;
import com.repeatwise.service.SetService;
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
@RequestMapping("/api/v1/sets")
@RequiredArgsConstructor
@Validated
@Tag(name = "Set Management", description = "APIs for set management operations")
public class SetController {

    private final SetService setService;

    @PostMapping
    @Operation(summary = "Create a new set", description = "Creates a new learning set for a user")
    public ResponseEntity<SetDto> createSet(@Valid @RequestBody SetDto setDto, @RequestParam UUID userId) {
        SetDto createdSet = setService.createSet(userId, setDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSet);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get set by ID", description = "Retrieves set information by set ID")
    public ResponseEntity<SetDto> getSetById(@PathVariable UUID id) {
        return setService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get sets by user", description = "Retrieves all sets for a specific user")
    public ResponseEntity<List<SetDto>> getSetsByUser(@PathVariable UUID userId) {
        List<SetDto> sets = setService.findByUserId(userId);
        return ResponseEntity.ok(sets);
    }

    @GetMapping
    @Operation(summary = "Get sets by user with filter", description = "Retrieves all sets for a specific user with optional status filter")
    public ResponseEntity<List<SetDto>> getSetsByUserWithFilter(
            @RequestParam UUID userId,
            @RequestParam(required = false) String status) {
        List<SetDto> sets;
        if (status != null) {
            sets = setService.findByUserIdAndStatus(userId, com.repeatwise.enums.SetStatus.valueOf(status.toUpperCase()));
        } else {
            sets = setService.findByUserId(userId);
        }
        return ResponseEntity.ok(sets);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update set", description = "Updates set information")
    public ResponseEntity<SetDto> updateSet(@PathVariable UUID id, @Valid @RequestBody SetDto setDto, @RequestParam UUID userId) {
        SetDto updatedSet = setService.updateSet(id, userId, setDto);
        return ResponseEntity.ok(updatedSet);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete set", description = "Soft deletes a set")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID id, @RequestParam UUID userId) {
        setService.deleteSet(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start-learning")
    @Operation(summary = "Start learning a set", description = "Starts learning a set (changes status to LEARNING)")
    public ResponseEntity<SetDto> startLearning(@PathVariable UUID id, @RequestParam UUID userId) {
        SetDto updatedSet = setService.startLearning(id, userId);
        return ResponseEntity.ok(updatedSet);
    }

    @PostMapping("/{id}/mark-mastered")
    @Operation(summary = "Mark set as mastered", description = "Marks a set as mastered")
    public ResponseEntity<SetDto> markAsMastered(@PathVariable UUID id, @RequestParam UUID userId) {
        SetDto updatedSet = setService.markAsMastered(id, userId);
        return ResponseEntity.ok(updatedSet);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get set statistics", description = "Retrieves learning statistics for a specific set")
    public ResponseEntity<SetService.SetStatistics> getSetStatistics(@PathVariable UUID id, @RequestParam UUID userId) {
        SetService.SetStatistics setStats = setService.getSetStatistics(id, userId);
        return ResponseEntity.ok(setStats);
    }

    @GetMapping("/user/{userId}/daily-review")
    @Operation(summary = "Get daily review sets", description = "Retrieves up to 3 sets that need to be reviewed today for a user")
    public ResponseEntity<List<SetDto>> getDailyReviewSets(
            @PathVariable UUID userId,
            @RequestParam(required = false) String date) {
        LocalDate reviewDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        List<SetDto> dailySets = setService.getDailyReviewSets(userId, reviewDate);
        return ResponseEntity.ok(dailySets);
    }
} 
