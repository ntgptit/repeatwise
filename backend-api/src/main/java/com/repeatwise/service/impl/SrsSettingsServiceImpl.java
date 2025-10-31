package com.repeatwise.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.srs.UpdateSrsSettingsRequest;
import com.repeatwise.dto.response.srs.SrsSettingsResponse;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.log.LogEvent;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.service.BaseService;
import com.repeatwise.service.ISrsSettingsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of SRS Settings Service
 *
 * Requirements:
 * - UC-028: Configure SRS Settings
 *
 * Business Rules:
 * - BR-SRS-01: Total boxes default 7, allowed range 3-10
 * - BR-SRS-02: Forgotten card action default MOVE_TO_BOX_1
 * - BR-SRS-03: Review order default RANDOM
 * - BR-SRS-04: New cards per day and max reviews per day must be positive integers with caps
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SrsSettingsServiceImpl extends BaseService implements ISrsSettingsService {

    private final SrsSettingsRepository srsSettingsRepository;

    // ==================== UC-028: Get SRS Settings ====================

    @Override
    public SrsSettingsResponse getSettings(final UUID userId) {
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Getting SRS settings: userId={}", LogEvent.START, userId);

        final var settings = getSrsSettings(userId);

        final var response = buildResponse(settings);

        log.info("event={} SRS settings retrieved: userId={}", LogEvent.SUCCESS, userId);

        return response;
    }

    // ==================== UC-028: Update SRS Settings ====================

    @Override
    @Transactional
    public SrsSettingsResponse updateSettings(final UpdateSrsSettingsRequest request, final UUID userId) {
        Objects.requireNonNull(request, "UpdateSrsSettingsRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Updating SRS settings: userId={}", LogEvent.START, userId);

        // Step 1: Get existing settings
        final var settings = getSrsSettings(userId);

        // Step 2: Validate moveDownBoxes when forgottenCardAction is MOVE_DOWN_N_BOXES
        if ((request.getForgottenCardAction() == ForgottenCardAction.MOVE_DOWN_N_BOXES) && (request
                .getMoveDownBoxes() == null)) {
            throw new ValidationException(
                    "SRS_002",
                    getMessage("error.srs.movedown.required.when.action"));
        }

        // Step 3: Validate notification time format if provided
        if (request.getNotificationTime() != null) {
            validateNotificationTime(request.getNotificationTime());
        }

        // Step 4: Update settings (only update provided fields)
        updateSettingsFields(settings, request);

        // Step 5: Save settings
        final var savedSettings = this.srsSettingsRepository.save(settings);

        log.info("event={} SRS settings updated successfully: userId={}", LogEvent.SUCCESS, userId);

        return buildResponse(savedSettings);
    }

    // ==================== Helper Methods ====================

    private SrsSettings getSrsSettings(final UUID userId) {
        return this.srsSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("event={} SRS settings not found: userId={}", LogEvent.EX_RESOURCE_NOT_FOUND, userId);
                    return new ResourceNotFoundException(
                            "SRS_001",
                            getMessage("error.srs.settings.not.found"));
                });
    }

    private void validateNotificationTime(final String timeStr) {
        if (StringUtils.isBlank(timeStr)) {
            return;
        }

        try {
            final var time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            // Additional validation: ensure time is within valid range
            if (time.isBefore(LocalTime.MIN) || time.isAfter(LocalTime.MAX)) {
                throw new ValidationException(
                        "SRS_003",
                        getMessage("error.srs.notification.time.format"));
            }
        } catch (final Exception e) {
            throw new ValidationException(
                    "SRS_003",
                    getMessage("error.notification.time.format"));
        }
    }

    private void updateSettingsFields(final SrsSettings settings, final UpdateSrsSettingsRequest request) {
        if (request.getTotalBoxes() != null) {
            settings.setTotalBoxes(request.getTotalBoxes());
        }
        if (request.getReviewOrder() != null) {
            settings.setReviewOrder(request.getReviewOrder());
        }
        if (request.getNewCardsPerDay() != null) {
            settings.setNewCardsPerDay(request.getNewCardsPerDay());
        }
        if (request.getMaxReviewsPerDay() != null) {
            settings.setMaxReviewsPerDay(request.getMaxReviewsPerDay());
        }
        if (request.getForgottenCardAction() != null) {
            settings.setForgottenCardAction(request.getForgottenCardAction());
        }
        if (request.getMoveDownBoxes() != null) {
            settings.setMoveDownBoxes(request.getMoveDownBoxes());
        }
        if (request.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(request.getNotificationEnabled());
        }
        if (request.getNotificationTime() != null) {
            final var time = LocalTime.parse(request.getNotificationTime(), DateTimeFormatter.ofPattern("HH:mm"));
            settings.setNotificationTime(time);
        }
    }

    private SrsSettingsResponse buildResponse(final SrsSettings settings) {
        return SrsSettingsResponse.builder()
                .totalBoxes(settings.getTotalBoxes())
                .reviewOrder(settings.getReviewOrder())
                .newCardsPerDay(settings.getNewCardsPerDay())
                .maxReviewsPerDay(settings.getMaxReviewsPerDay())
                .forgottenCardAction(settings.getForgottenCardAction())
                .moveDownBoxes(settings.getMoveDownBoxes())
                .notificationEnabled(settings.getNotificationEnabled())
                .notificationTime(settings.getNotificationTime())
                .updatedAt(settings.getUpdatedAt() != null ? settings.getUpdatedAt() : null)
                .build();
    }

}
