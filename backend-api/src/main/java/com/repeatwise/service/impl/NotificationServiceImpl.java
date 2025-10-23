package com.repeatwise.service.impl;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repeatwise.dto.request.notification.UpdateNotificationSettingsRequest;
import com.repeatwise.dto.response.notification.NotificationLogResponse;
import com.repeatwise.dto.response.notification.NotificationSettingsResponse;
import com.repeatwise.dto.response.notification.TestNotificationResponse;
import com.repeatwise.entity.NotificationLog;
import com.repeatwise.entity.NotificationSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.enums.NotificationMethod;
import com.repeatwise.entity.enums.NotificationStatus;
import com.repeatwise.entity.enums.NotificationType;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.mapper.NotificationMapper;
import com.repeatwise.repository.NotificationLogRepository;
import com.repeatwise.repository.NotificationSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IEmailService;
import com.repeatwise.service.INotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;

/**
 * Notification Service Implementation
 *
 * Requirements:
 * - UC-024: Manage Notifications (all steps)
 * - Business Rules: BR-076 to BR-080
 *
 * Features:
 * - Get/Update notification settings
 * - Send test notifications
 * - View notification logs
 * - Process daily reminders (batch job)
 * - Retry failed notifications
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final IEmailService emailService;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int MAX_NOTIFICATIONS_PER_DAY = 10;

    @Override
    public NotificationSettingsResponse getNotificationSettings(final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting notification settings for user: {}", LogEvent.NOTIF_SETTINGS_GET, userId);

        final User user = getUserById(userId);
        NotificationSettings settings = notificationSettingsRepository
            .findByUserId(userId)
            .orElse(null);

        if (settings == null) {
            log.info("event={} Creating default notification settings for user: {}", LogEvent.NOTIF_SETTINGS_CREATE_DEFAULT, userId);
            settings = createDefaultSettings(user);
        }

        final NotificationSettingsResponse response = notificationMapper.toResponse(settings);
        response.setNextReminderAt(calculateNextReminderTime(settings));

        log.info("event={} Notification settings retrieved successfully for user: {}", LogEvent.SUCCESS, userId);
        return response;
    }

    @Transactional
    @Override
    public NotificationSettingsResponse updateNotificationSettings(
        final UUID userId,
        final UpdateNotificationSettingsRequest request
    ) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");

        log.info("event={} Updating notification settings for user: {}", LogEvent.NOTIF_SETTINGS_UPDATE, userId);

        validateUpdateRequest(request);

        final User user = getUserById(userId);
        NotificationSettings settings = notificationSettingsRepository
            .findByUserId(userId)
            .orElse(null);

        if (settings == null) {
            settings = createDefaultSettings(user);
        }

        updateSettingsFromRequest(settings, request);

        final NotificationSettings savedSettings = notificationSettingsRepository.save(settings);

        final NotificationSettingsResponse response = notificationMapper.toResponse(savedSettings);
        response.setNextReminderAt(calculateNextReminderTime(savedSettings));

        log.info("event={} Notification settings updated successfully for user: {}", LogEvent.SUCCESS, userId);
        return response;
    }

    @Transactional
    @Override
    public TestNotificationResponse sendTestNotification(final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Sending test notification for user: {}", com.repeatwise.log.LogEvent.NOTIF_TEST_SEND, userId);

        final User user = getUserById(userId);
        final NotificationSettings settings = notificationSettingsRepository
            .findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(user));

        final String recipientEmail = settings.getEffectiveEmail();
        final int dueCardsCount = calculateDueCardsCount(userId);

        final boolean sent = emailService.sendTestNotificationEmail(
            recipientEmail,
            user.getName(),
            dueCardsCount
        );

        final Instant sentAt = Instant.now();

        if (sent) {
            logNotificationAttempt(
                user,
                NotificationType.DAILY_REMINDER,
                recipientEmail,
                "[RepeatWise] Test Notification",
                NotificationStatus.SENT,
                null,
                createTestMetadata(dueCardsCount, 0),
                sentAt
            );
        } else {
            logNotificationAttempt(
                user,
                NotificationType.DAILY_REMINDER,
                recipientEmail,
                "[RepeatWise] Test Notification",
                NotificationStatus.FAILED,
                "Failed to send test notification",
                createTestMetadata(dueCardsCount, 0),
                sentAt
            );
        }

        final String message = String.format(
            "Test notification sent to %s",
            recipientEmail
        );

        log.info("event={} Test notification sent for user: {} - Success: {}", LogEvent.NOTIF_TEST_SEND, userId, sent);

        return TestNotificationResponse.builder()
            .message(message)
            .sentAt(sentAt)
            .build();
    }

    @Override
    public Page<NotificationLogResponse> getNotificationLogs(
        final UUID userId,
        final Pageable pageable
    ) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        log.info("event={} Getting notification logs for user: {} - Page: {}", LogEvent.START, userId, pageable.getPageNumber());

        getUserById(userId);

        final Page<NotificationLog> logs = notificationLogRepository
            .findByUserIdOrderBySentAtDesc(userId, pageable);

        log.info("event={} Retrieved {} notification logs for user: {}", LogEvent.SUCCESS, logs.getTotalElements(), userId);

        return logs.map(notificationMapper::toLogResponse);
    }

    @Transactional
    @Override
    public int processDailyReminders() {
        log.info("event={} Starting daily reminder processing batch job", LogEvent.NOTIF_SCHEDULE_START);

        final LocalTime currentTime = LocalTime.now();
        final String currentDay = DayOfWeek.from(ZonedDateTime.now()).name().substring(0, 3);

        log.info("event={} Processing reminders for time: {} and day: {}", LogEvent.START, currentTime, currentDay);

        final List<NotificationSettings> usersToNotify = notificationSettingsRepository
            .findUsersToNotifyAt(currentTime, currentDay);

        log.info("event={} Found {} users to notify", LogEvent.START, usersToNotify.size());

        final AtomicInteger successCount = new AtomicInteger(0);

        for (final NotificationSettings settings : usersToNotify) {
            try {
                if (shouldSendNotification(settings)) {
                    final boolean sent = sendDailyReminder(settings);
                    if (sent) {
                        successCount.incrementAndGet();
                    }
                } else {
                    log.debug("event={} Skipping notification for user: {} - conditions not met",
                        LogEvent.START, settings.getUser().getId());
                }
            } catch (Exception e) {
                log.error("event={} Error processing notification for user: {}",
                    LogEvent.NOTIF_SCHEDULE_ERROR, settings.getUser().getId(), e);
            }
        }

        log.info("event={} Daily reminder processing completed - Sent: {}/{}", LogEvent.NOTIF_SCHEDULE_DONE, successCount.get(), usersToNotify.size());

        return successCount.get();
    }

    @Transactional
    @Override
    public int retryFailedNotifications() {
        log.info("event={} Starting failed notification retry job", LogEvent.NOTIF_RETRY_START);

        final List<NotificationLog> failedLogs = notificationLogRepository
            .findFailedNotificationsForRetry(MAX_RETRY_ATTEMPTS);

        log.info("event={} Found {} failed notifications to retry", LogEvent.START, failedLogs.size());

        final AtomicInteger retryCount = new AtomicInteger(0);

        for (final NotificationLog failedLog : failedLogs) {
            try {
                final boolean retried = retryNotification(failedLog);
                if (retried) {
                    retryCount.incrementAndGet();
                }
            } catch (Exception e) {
                log.error("event={} Error retrying notification: {}", LogEvent.NOTIF_RETRY_ERROR, failedLog.getId(), e);
            }
        }

        log.info("event={} Failed notification retry completed - Retried: {}/{}", LogEvent.NOTIF_RETRY_DONE, retryCount.get(), failedLogs.size());

        return retryCount.get();
    }

    // ==================== Private Helper Methods ====================

    /**
     * Get user by ID or throw exception
     */
    private User getUserById(final UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "USER_NOT_FOUND",
                "User not found with ID: " + userId
            ));
    }

    /**
     * Create default notification settings for new user
     */
    @Transactional
    private NotificationSettings createDefaultSettings(final User user) {
        final NotificationSettings settings = NotificationSettings.builder()
            .user(user)
            .dailyReminderEnabled(true)
            .dailyReminderTime(LocalTime.of(9, 0))
            .dailyReminderDays("MON,TUE,WED,THU,FRI,SAT,SUN")
            .notificationMethod(NotificationMethod.EMAIL)
            .notificationEmail(null)
            .pushToken(null)
            .build();

        return notificationSettingsRepository.save(settings);
    }

    /**
     * Validate update request
     */
    private void validateUpdateRequest(final UpdateNotificationSettingsRequest request) {
        if (request.getDailyReminderEnabled() && request.getDailyReminderDays().isEmpty()) {
            throw new ValidationException(
                "VALIDATION_ERROR",
                "At least one day must be selected when daily reminders are enabled"
            );
        }

        if (!"EMAIL".equals(request.getNotificationMethod())) {
            throw new ValidationException(
                "VALIDATION_ERROR",
                "Only EMAIL notification method is supported in MVP"
            );
        }
    }

    /**
     * Update settings entity from request DTO
     */
    private void updateSettingsFromRequest(
        final NotificationSettings settings,
        final UpdateNotificationSettingsRequest request
    ) {
        settings.setDailyReminderEnabled(request.getDailyReminderEnabled());
        settings.setDailyReminderTime(
            LocalTime.parse(request.getDailyReminderTime(), DateTimeFormatter.ofPattern("HH:mm"))
        );
        settings.setDailyReminderDays(String.join(",", request.getDailyReminderDays()));
        settings.setNotificationMethod(NotificationMethod.EMAIL);
        settings.setNotificationEmail(request.getNotificationEmail());
    }

    /**
     * Calculate next reminder time based on settings
     */
    private Instant calculateNextReminderTime(final NotificationSettings settings) {
        if (!settings.isDailyReminderActive()) {
            return null;
        }

        final ZonedDateTime now = ZonedDateTime.now();
        final LocalTime reminderTime = settings.getDailyReminderTime();

        ZonedDateTime nextReminder = now.with(reminderTime);

        if (nextReminder.isBefore(now)) {
            nextReminder = nextReminder.plusDays(1);
        }

        while (!settings.isEnabledForDay(
            nextReminder.getDayOfWeek().name().substring(0, 3)
        )) {
            nextReminder = nextReminder.plusDays(1);
        }

        return nextReminder.toInstant();
    }

    /**
     * Calculate due cards count for user
     * TODO: Implement actual calculation from card_box_position table
     */
    private int calculateDueCardsCount(final UUID userId) {
        return 0;
    }

    /**
     * Check if notification should be sent
     */
    private boolean shouldSendNotification(final NotificationSettings settings) {
        if (!settings.isDailyReminderActive()) {
            return false;
        }

        final UUID userId = settings.getUser().getId();
        final int dueCards = calculateDueCardsCount(userId);

        if (dueCards == 0) {
            log.debug("event={} User {} has no due cards - skipping notification", com.repeatwise.log.LogEvent.START, userId);
            return false;
        }

        final long todayNotifications = countTodayNotifications(userId);
        if (todayNotifications >= MAX_NOTIFICATIONS_PER_DAY) {
            log.warn("event={} User {} exceeded max notifications per day - skipping", com.repeatwise.log.LogEvent.NOTIF_RETRY_ERROR, userId);
            return false;
        }

        return true;
    }

    /**
     * Send daily reminder email
     */
    private boolean sendDailyReminder(final NotificationSettings settings) {
        final User user = settings.getUser();
        final String recipientEmail = settings.getEffectiveEmail();
        final int dueCardsCount = calculateDueCardsCount(user.getId());
        final int streakDays = 0; // TODO: Calculate from stats

        final boolean sent = emailService.sendDailyReminderEmail(
            recipientEmail,
            user.getName(),
            dueCardsCount,
            streakDays
        );

        final String subject = String.format(
            "=Ú [RepeatWise] You have %d card%s due for review",
            dueCardsCount,
            dueCardsCount > 1 ? "s" : ""
        );

        logNotificationAttempt(
            user,
            NotificationType.DAILY_REMINDER,
            recipientEmail,
            subject,
            sent ? NotificationStatus.SENT : NotificationStatus.FAILED,
            sent ? null : "Failed to send email",
            createReminderMetadata(dueCardsCount, streakDays, 0),
            Instant.now()
        );

        return sent;
    }

    /**
     * Count notifications sent today
     */
    private long countTodayNotifications(final UUID userId) {
        final Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        final Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        return notificationLogRepository
            .findByUserIdAndSentAtBetween(userId, startOfDay, endOfDay, Pageable.unpaged())
            .getTotalElements();
    }

    /**
     * Retry failed notification
     */
    private boolean retryNotification(final NotificationLog failedLog) {
        log.info("event={} Retrying notification: {}", com.repeatwise.log.LogEvent.NOTIF_RETRY_START, failedLog.getId());

        // Extract retry count from metadata
        final int currentRetryCount = extractRetryCount(failedLog.getMetadata());

        if (currentRetryCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("event={} Max retry attempts reached for notification: {}", com.repeatwise.log.LogEvent.NOTIF_RETRY_ERROR, failedLog.getId());
            return false;
        }

        // TODO: Implement actual retry logic
        return false;
    }

    /**
     * Log notification attempt
     */
    @Transactional
    private void logNotificationAttempt(
        final User user,
        final NotificationType type,
        final String recipient,
        final String subject,
        final NotificationStatus status,
        final String errorMessage,
        final String metadata,
        final Instant sentAt
    ) {
        final NotificationLog log = NotificationLog.builder()
            .user(user)
            .notificationType(type)
            .notificationMethod(NotificationMethod.EMAIL)
            .recipient(recipient)
            .subject(subject)
            .body(null)
            .status(status)
            .errorMessage(errorMessage)
            .metadata(metadata)
            .sentAt(sentAt)
            .deliveredAt(status == NotificationStatus.SENT ? sentAt : null)
            .build();

        notificationLogRepository.save(log);
    }

    /**
     * Create metadata JSON for reminder
     */
    private String createReminderMetadata(
        final int dueCardsCount,
        final int streakDays,
        final int retryCount
    ) {
        final Map<String, Object> metadata = new HashMap<>();
        metadata.put("due_cards_count", dueCardsCount);
        metadata.put("streak_days", streakDays);
        metadata.put("retry_count", retryCount);

        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.error("event={} Failed to serialize metadata", com.repeatwise.log.LogEvent.FAIL, e);
            return "{}";
        }
    }

    /**
     * Create metadata JSON for test notification
     */
    private String createTestMetadata(final int dueCardsCount, final int retryCount) {
        return createReminderMetadata(dueCardsCount, 0, retryCount);
    }

    /**
     * Extract retry count from metadata JSON
     */
    private int extractRetryCount(final String metadataJson) {
        if (metadataJson == null || metadataJson.isBlank()) {
            return 0;
        }

        try {
            @SuppressWarnings("unchecked")
            final Map<String, Object> metadata = objectMapper.readValue(
                metadataJson,
                Map.class
            );
            final Object retryCount = metadata.get("retry_count");
            return retryCount != null ? (Integer) retryCount : 0;
        } catch (Exception e) {
            log.error("event={} Failed to parse metadata JSON", com.repeatwise.log.LogEvent.FAIL, e);
            return 0;
        }
    }
}


