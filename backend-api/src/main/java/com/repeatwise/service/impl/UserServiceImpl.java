package com.repeatwise.service.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.ChangePasswordResponse;
import com.repeatwise.dto.response.user.UpdateProfileResponse;
import com.repeatwise.dto.response.user.UserProfileResponse;
import com.repeatwise.entity.User;
import com.repeatwise.exception.InvalidCredentialsException;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.log.LogEvent;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.BaseService;
import com.repeatwise.service.IUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User Service Implementation - Manages user operations
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - Coding Convention: Service layer business logic
 *
 * Business Rules:
 * - BR-009: Profile Fields validation
 * - BR-010: Theme behavior (LIGHT, DARK, SYSTEM)
 * - BR-011: Language behavior (VI, EN)
 * - BR-012: Timezone behavior (IANA timezone)
 * - BR-013: Username and email immutability in MVP
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends BaseService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserProfileResponse getCurrentUserProfile(final UUID userId) {
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Getting profile for user: {}", LogEvent.USER_GET_PROFILE, userId);

        final var user = getUserById(userId);

        log.info("event={} Profile retrieved successfully for user: {}", LogEvent.SUCCESS, userId);
        return this.userMapper.toProfileResponse(user);
    }

    private static final String MSG_SUCCESS_PROFILE_UPDATE = "success.user.profile.updated";

    @Transactional
    @Override
    public UpdateProfileResponse updateProfile(final UUID userId, final UpdateProfileRequest request) {
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(request, "UpdateProfileRequest cannot be null");

        validateRequest(request);
        validateTimezone(request.getTimezone());

        log.info("event={} Updating profile for user: {}, name={}, timezone={}, language={}, theme={}",
                LogEvent.USER_UPDATE_PROFILE, userId, request.getName(), request.getTimezone(), request.getLanguage(),
                request.getTheme());

        final var user = getUserById(userId);

        updateUserFields(user, request);

        final var savedUser = this.userRepository.save(user);

        log.info("event={} Profile updated successfully for user: {}", LogEvent.SUCCESS, userId);

        return UpdateProfileResponse.builder()
                .message(getMessage(MSG_SUCCESS_PROFILE_UPDATE))
                .user(this.userMapper.toProfileResponse(savedUser))
                .build();
    }

    /**
     * Validate timezone is a valid IANA timezone identifier
     * UC-005: Timezone must be valid IANA timezone
     */
    private void validateTimezone(final String timezone) {
        if (StringUtils.isBlank(timezone)) {
            return; // Already validated by @NotBlank
        }

        try {
            // Validate timezone using ZoneId - throws exception if invalid
            ZoneId.of(timezone.trim());
        } catch (final Exception ex) {
            log.warn("event={} Invalid timezone: {}", LogEvent.EX_VALIDATION, timezone);
            throw new IllegalArgumentException(
                    getMessage("error.user.timezone.invalid"));
        }
    }

    private void validateRequest(final UpdateProfileRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new IllegalArgumentException(getMessage("error.user.name.required"));
        }

        if (StringUtils.isBlank(request.getTimezone())) {
            throw new IllegalArgumentException(getMessage("error.user.timezone.required"));
        }

        if (request.getLanguage() == null) {
            throw new IllegalArgumentException(getMessage("error.user.language.required"));
        }

        if (request.getTheme() == null) {
            throw new IllegalArgumentException(getMessage("error.user.theme.required"));
        }
    }

    private User getUserById(final UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("event={} User not found: {}", LogEvent.USER_NOT_FOUND, userId);
                    return new ResourceNotFoundException(
                            "USER_001",
                            getMessage("error.user.not.found", userId));
                });
    }

    private void updateUserFields(final User user, final UpdateProfileRequest request) {
        user.setName(StringUtils.trim(request.getName()));
        user.setTimezone(StringUtils.trim(request.getTimezone()));
        user.setLanguage(request.getLanguage());
        user.setTheme(request.getTheme());
    }

    private static final String MSG_SUCCESS_PASSWORD_CHANGE = "success.user.password.changed";

    @Transactional
    @Override
    public ChangePasswordResponse changePassword(final UUID userId, final ChangePasswordRequest request) {
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(request, "ChangePasswordRequest cannot be null");

        validateChangePasswordRequest(request);

        log.info("event={} Changing password for user: {}", LogEvent.USER_CHANGE_PASSWORD, userId);

        final var user = getUserById(userId);

        // Verify current password
        if (!this.passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("event={} Password change failed: incorrect current password for user: {}",
                    LogEvent.EX_INVALID_CREDENTIALS, userId);
            throw new InvalidCredentialsException(
                    "USER_004",
                    getMessage("error.user.password.current.incorrect"));
        }

        // Verify new password is different from current password
        if (this.passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            log.warn("event={} Password change failed: new password same as current for user: {}",
                    LogEvent.EX_INVALID_CREDENTIALS, userId);
            throw new IllegalArgumentException(
                    getMessage("error.user.password.same.as.current"));
        }

        // Update password
        final var newPasswordHash = this.passwordEncoder.encode(request.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        this.userRepository.save(user);

        // Revoke all refresh tokens (logout from all devices)
        final var revokedCount = this.refreshTokenRepository.revokeAllByUserId(userId, Instant.now());

        log.info("event={} Password changed successfully: userId={}, tokensRevoked={}",
                LogEvent.SUCCESS, userId, revokedCount);

        return ChangePasswordResponse.builder()
                .message(getMessage(MSG_SUCCESS_PASSWORD_CHANGE))
                .build();
    }

    private void validateChangePasswordRequest(final ChangePasswordRequest request) {
        if (StringUtils.isBlank(request.getCurrentPassword()) || StringUtils.isBlank(request.getNewPassword())
                || StringUtils.isBlank(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(getMessage("error.user.password.required"));
        }

        if (request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException(
                    getMessage("error.user.password.too.short", 8));
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException(
                    getMessage("error.user.password.mismatch"));
        }
    }

}
