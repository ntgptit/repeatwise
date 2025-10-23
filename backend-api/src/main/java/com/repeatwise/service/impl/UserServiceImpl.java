package com.repeatwise.service.impl;

import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.UserProfileResponse;
import com.repeatwise.entity.User;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

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
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    @Override
    public UserProfileResponse getCurrentUserProfile(final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting profile for user: {}", LogEvent.USER_GET_PROFILE, userId);

        final User user = getUserById(userId);

        log.info("event={} Profile retrieved successfully for user: {}", LogEvent.SUCCESS, userId);
        return userMapper.toProfileResponse(user);
    }

    @Transactional
    @Override
    public UserProfileResponse updateProfile(final UUID userId, final UpdateProfileRequest request) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(request, "UpdateProfileRequest cannot be null");

        validateRequest(request);

        log.info("event={} Updating profile for user: {}, name={}, timezone={}, language={}, theme={}",
                LogEvent.USER_UPDATE_PROFILE, userId, request.getName(), request.getTimezone(), request.getLanguage(), request.getTheme());

        final User user = getUserById(userId);

        updateUserFields(user, request);

        final User savedUser = userRepository.save(user);

        log.info("event={} Profile updated successfully for user: {}", LogEvent.SUCCESS, userId);
        return userMapper.toProfileResponse(savedUser);
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
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("event={} User not found: {}", LogEvent.USER_NOT_FOUND, userId);
                    return new ResourceNotFoundException(
                            "USER_001",
                            getMessage("error.user.not.found", userId)
                    );
                });
    }

    private void updateUserFields(final User user, final UpdateProfileRequest request) {
        user.setName(StringUtils.trim(request.getName()));
        user.setTimezone(StringUtils.trim(request.getTimezone()));
        user.setLanguage(request.getLanguage());
        user.setTheme(request.getTheme());
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
