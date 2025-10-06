package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.statistics.LearningStatisticsRequest;
import com.spacedlearning.dto.statistics.LearningStatisticsResponse;
import com.spacedlearning.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for Statistics operations
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Statistics API", description = "Endpoints for learning statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @PostMapping("/learning")
    @Operation(summary = "Get learning statistics", 
               description = "Get learning statistics based on request parameters")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getLearningStatistics(
            @Valid @RequestBody LearningStatisticsRequest request,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting learning statistics for user {}", userId);
        
        // Override userId in request with authenticated user
        request.setUserId(userId);
        
        LearningStatisticsResponse response = statisticsService.getLearningStatistics(request);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/daily")
    @Operation(summary = "Get daily statistics", 
               description = "Get daily learning statistics for a specific date")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getDailyStatistics(
            @Parameter(description = "Date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting daily statistics for user {} on date {}", userId, date);
        
        LearningStatisticsResponse response = statisticsService.getDailyStatistics(userId, date);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get weekly statistics", 
               description = "Get weekly learning statistics for a specific week")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getWeeklyStatistics(
            @Parameter(description = "Week start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting weekly statistics for user {} starting from {}", userId, startDate);
        
        LearningStatisticsResponse response = statisticsService.getWeeklyStatistics(userId, startDate);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly statistics", 
               description = "Get monthly learning statistics for a specific month")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getMonthlyStatistics(
            @Parameter(description = "Year") @RequestParam int year,
            @Parameter(description = "Month (1-12)") @RequestParam int month,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting monthly statistics for user {} for {}/{}", userId, year, month);
        
        LearningStatisticsResponse response = statisticsService.getMonthlyStatistics(userId, year, month);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/overall")
    @Operation(summary = "Get overall statistics", 
               description = "Get overall learning statistics for the current user")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getOverallStatistics(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting overall statistics for user {}", userId);
        
        LearningStatisticsResponse response = statisticsService.getOverallStatistics(userId);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/sets/{setId}")
    @Operation(summary = "Get set statistics", 
               description = "Get statistics for a specific learning set")
    public ResponseEntity<DataResponse<LearningStatisticsResponse>> getSetStatistics(
            @Parameter(description = "Set ID") @PathVariable UUID setId,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting set statistics for user {} and set {}", userId, setId);
        
        LearningStatisticsResponse response = statisticsService.getSetStatistics(userId, setId);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get statistics history", 
               description = "Get statistics history for a date range")
    public ResponseEntity<DataResponse<List<LearningStatisticsResponse>>> getStatisticsHistory(
            @Parameter(description = "Start date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        
        UUID userId = getCurrentUserId(authentication);
        log.debug("Getting statistics history for user {} from {} to {}", userId, startDate, endDate);
        
        List<LearningStatisticsResponse> response = statisticsService.getStatisticsHistory(userId, startDate, endDate);
        return ResponseEntity.ok(DataResponse.of(response));
    }

    /**
     * Extract current user ID from authentication
     */
    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authentication found");
        }
        
        String userIdString = authentication.getName();
        try {
            return UUID.fromString(userIdString);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid user ID in authentication: " + userIdString);
        }
    }
}
