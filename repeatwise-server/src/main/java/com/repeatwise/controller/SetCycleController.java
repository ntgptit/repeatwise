package com.repeatwise.controller;

import com.repeatwise.dto.SetCycleDto;
import com.repeatwise.service.SetCycleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/set-cycles")
@RequiredArgsConstructor
@Validated
@Tag(name = "Set Cycle Management", description = "APIs for set cycle management operations")
public class SetCycleController {

    private final SetCycleService setCycleService;

    @PostMapping("/set/{setId}/start")
    @Operation(summary = "Start a new cycle", description = "Starts a new learning cycle for a set")
    public ResponseEntity<SetCycleDto> startCycle(@PathVariable UUID setId, @RequestParam UUID userId) {
        SetCycleDto cycle = setCycleService.startCycle(setId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cycle);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cycle by ID", description = "Retrieves cycle information by cycle ID")
    public ResponseEntity<SetCycleDto> getCycleById(@PathVariable UUID id) {
        return setCycleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/set/{setId}")
    @Operation(summary = "Get cycles by set", description = "Retrieves all cycles for a specific set")
    public ResponseEntity<List<SetCycleDto>> getCyclesBySet(@PathVariable UUID setId) {
        List<SetCycleDto> cycles = setCycleService.findBySetId(setId);
        return ResponseEntity.ok(cycles);
    }

    @GetMapping("/set/{setId}/active")
    @Operation(summary = "Get active cycle", description = "Retrieves the active cycle for a set")
    public ResponseEntity<SetCycleDto> getActiveCycle(@PathVariable UUID setId) {
        return setCycleService.findActiveCycleBySetId(setId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Finish a cycle", description = "Manually finishes a cycle (when 5 reviews are completed)")
    public ResponseEntity<SetCycleDto> finishCycle(@PathVariable UUID id, @RequestParam UUID userId) {
        SetCycleDto cycle = setCycleService.finishCycle(id, userId);
        return ResponseEntity.ok(cycle);
    }

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Get cycle statistics", description = "Retrieves statistics for a specific cycle")
    public ResponseEntity<SetCycleService.CycleStatistics> getCycleStatistics(@PathVariable UUID id) {
        SetCycleService.CycleStatistics stats = setCycleService.getCycleStatistics(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cycles by user", description = "Retrieves all cycles for a specific user")
    public ResponseEntity<List<SetCycleDto>> getCyclesByUser(@PathVariable UUID userId) {
        List<SetCycleDto> cycles = setCycleService.findCyclesByUserId(userId);
        return ResponseEntity.ok(cycles);
    }
} 
