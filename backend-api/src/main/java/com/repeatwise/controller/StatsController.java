package com.repeatwise.controller;

import com.repeatwise.dto.response.stats.BoxDistributionResponse;
import com.repeatwise.dto.response.stats.UserStatsResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Statistics Management
 *
 * Requirements:
 * - UC-031: View User Statistics
 * - UC-032: View Box Distribution
 *
 * Endpoints:
 * - GET    /api/stats/user              - Get user statistics
 * - GET    /api/stats/box-distribution  - Get box distribution
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final IStatsService statsService;

    // ==================== UC-031: Get User Statistics ====================

    /**
     * Get user statistics
     * UC-031: View User Statistics
     *
     * Response: 200 OK with user statistics
     *
     * @return User statistics response
     */
    @GetMapping("/user")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/stats/user - Getting user statistics: userId={}",
            LogEvent.START, userId);

        final UserStatsResponse response = statsService.getUserStats(userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-032: Get Box Distribution ====================

    /**
     * Get box distribution statistics
     * UC-032: View Box Distribution
     *
     * Query Parameters:
     * - scopeType: ALL, DECK, or FOLDER (required)
     * - scopeId: Scope ID (required for DECK and FOLDER)
     *
     * Response: 200 OK with box distribution
     *
     * @param scopeType Scope type (ALL, DECK, FOLDER)
     * @param scopeId Scope ID (optional for ALL)
     * @return Box distribution response
     */
    @GetMapping("/box-distribution")
    public ResponseEntity<BoxDistributionResponse> getBoxDistribution(
            @RequestParam("scopeType") final String scopeType,
            @RequestParam(value = "scopeId", required = false) final UUID scopeId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/stats/box-distribution - Getting box distribution: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, scopeType, scopeId, userId);

        final BoxDistributionResponse response = statsService.getBoxDistribution(scopeType, scopeId, userId);

        return ResponseEntity.ok(response);
    }
}

