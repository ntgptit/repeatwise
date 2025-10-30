package com.repeatwise.controller;

import com.repeatwise.dto.request.srs.UpdateSrsSettingsRequest;
import com.repeatwise.dto.response.srs.SrsSettingsResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.ISrsSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for SRS Settings Management
 *
 * Requirements:
 * - UC-028: Configure SRS Settings
 *
 * Endpoints:
 * - GET    /api/srs/settings    - Get current SRS settings
 * - PATCH  /api/srs/settings    - Update SRS settings
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/srs")
@RequiredArgsConstructor
@Slf4j
public class SrsSettingsController {

    private final ISrsSettingsService srsSettingsService;

    // ==================== UC-028: Get SRS Settings ====================

    /**
     * Get current SRS settings for authenticated user
     * UC-028: Configure SRS Settings
     *
     * Response: 200 OK with current settings
     *
     * @return SRS settings response
     */
    @GetMapping("/settings")
    public ResponseEntity<SrsSettingsResponse> getSettings() {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/srs/settings - Getting SRS settings: userId={}",
            LogEvent.START, userId);

        final SrsSettingsResponse response = srsSettingsService.getSettings(userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-028: Update SRS Settings ====================

    /**
     * Update SRS settings for authenticated user
     * UC-028: Configure SRS Settings
     *
     * Request Body:
     * {
     *   "totalBoxes": 7,
     *   "reviewOrder": "RANDOM",
     *   "newCardsPerDay": 20,
     *   "maxReviewsPerDay": 200,
     *   "forgottenCardAction": "MOVE_TO_BOX_1",
     *   "moveDownBoxes": 1,
     *   "notificationEnabled": true,
     *   "notificationTime": "09:00"
     * }
     *
     * Response: 200 OK with updated settings
     *
     * @param request Update request
     * @return Updated SRS settings response
     */
    @PatchMapping("/settings")
    public ResponseEntity<SrsSettingsResponse> updateSettings(
            @Valid @RequestBody final UpdateSrsSettingsRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} PATCH /api/srs/settings - Updating SRS settings: userId={}",
            LogEvent.START, userId);

        final SrsSettingsResponse response = srsSettingsService.updateSettings(request, userId);

        log.info("event={} SRS settings updated successfully: userId={}",
            LogEvent.SUCCESS, userId);

        return ResponseEntity.ok(response);
    }
}

