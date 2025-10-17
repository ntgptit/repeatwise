package com.repeatwise.service.impl;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.exception.DuplicateEmailException;
import com.repeatwise.exception.DuplicateUsernameException;
import com.repeatwise.exception.InvalidCredentialsException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Authentication Service Implementation
 *
 * Requirements:
 * - UC-001: User Registration
 * - Coding Convention: Method <= 30 lines, use Apache Commons, MessageSource
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final SrsSettingsRepository srsSettingsRepository;
    private final UserStatsRepository userStatsRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Transactional
    @Override
    public UserResponse register(final RegisterRequest request) {
        log.info("Starting user registration: username={}, email={}",
            request.getUsername(), request.getEmail());

        validateRegistrationRequest(request);
        checkUsernameAvailability(request.getUsername());
        checkEmailAvailability(request.getEmail());

        final User user = createUser(request);
        final User savedUser = userRepository.save(user);

        createDefaultSettings(savedUser);

        log.info("User registered successfully: userId={}, username={}, email={}",
            savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    private void validateRegistrationRequest(final RegisterRequest request) {
        Objects.requireNonNull(request, "RegisterRequest cannot be null");

        if (StringUtils.isBlank(request.getUsername())) {
            log.error("Registration validation failed: username is blank");
            throw new IllegalArgumentException(
                getMessage("error.user.username.required"));
        }

        if (StringUtils.isBlank(request.getEmail())) {
            log.error("Registration validation failed: email is blank");
            throw new IllegalArgumentException(
                getMessage("error.user.email.required"));
        }

        if (StringUtils.isBlank(request.getPassword())) {
            log.error("Registration validation failed: password is blank");
            throw new IllegalArgumentException(
                getMessage("error.user.password.required"));
        }
    }

    private void checkUsernameAvailability(final String username) {
        final String normalizedUsername = normalizeUsername(username);

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            log.warn("Registration failed: username already exists: {}", normalizedUsername);
            throw new DuplicateUsernameException(
                "USER_002",
                getMessage("error.user.username.already.exists", normalizedUsername)
            );
        }
    }

    private void checkEmailAvailability(final String email) {
        final String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("Registration failed: email already exists: {}", normalizedEmail);
            throw new DuplicateEmailException(
                "USER_001",
                getMessage("error.user.email.already.exists", normalizedEmail)
            );
        }
    }

    private User createUser(final RegisterRequest request) {
        final User user = userMapper.toEntity(request);

        // Normalize and set username (lowercase)
        user.setUsername(normalizeUsername(request.getUsername()));

        // Normalize and set email (lowercase)
        user.setEmail(normalizeEmail(request.getEmail()));

        // Hash password with bcrypt (cost 12)
        final String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashedPassword);

        // Set default name if not provided
        if (StringUtils.isBlank(user.getName())) {
            user.setName(extractNameFromEmail(user.getEmail()));
        }

        return user;
    }

    private void createDefaultSettings(final User user) {
        // Create default SRS settings
        final SrsSettings srsSettings = SrsSettings.createDefault(user);
        srsSettingsRepository.save(srsSettings);

        log.debug("Created default SRS settings for user: userId={}", user.getId());

        // Create default user stats
        final UserStats userStats = UserStats.createDefault(user);
        userStatsRepository.save(userStats);

        log.debug("Created default user stats for user: userId={}", user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse login(final LoginRequest request) {
        log.info("Login attempt: usernameOrEmail={}", request.getUsernameOrEmail());

        validateLoginRequest(request);

        final User user = findUserByUsernameOrEmail(request.getUsernameOrEmail());
        verifyPassword(request.getPassword(), user.getPasswordHash());

        log.info("User logged in successfully: userId={}, username={}",
            user.getId(), user.getUsername());

        return userMapper.toResponse(user);
    }

    private void validateLoginRequest(final LoginRequest request) {
        Objects.requireNonNull(request, "LoginRequest cannot be null");

        if (StringUtils.isBlank(request.getUsernameOrEmail())) {
            log.error("Login validation failed: usernameOrEmail is blank");
            throw new IllegalArgumentException(
                getMessage("error.user.username.or.email.required"));
        }

        if (StringUtils.isBlank(request.getPassword())) {
            log.error("Login validation failed: password is blank");
            throw new IllegalArgumentException(
                getMessage("error.user.password.required"));
        }
    }

    private User findUserByUsernameOrEmail(final String usernameOrEmail) {
        final String normalized = StringUtils.trim(usernameOrEmail).toLowerCase();

        // Auto-detect if input is email (contains @) or username
        final Optional<User> userOptional = isEmail(normalized)
            ? userRepository.findByEmailIgnoreCase(normalized)
            : userRepository.findByUsernameIgnoreCase(normalized);

        return userOptional.orElseThrow(() -> {
            log.warn("Login failed: user not found: {}", normalized);
            return new InvalidCredentialsException(
                "AUTH_001",
                getMessage("error.user.invalid.credentials")
            );
        });
    }

    private void verifyPassword(final String rawPassword, final String passwordHash) {
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            log.warn("Login failed: incorrect password");
            throw new InvalidCredentialsException(
                "AUTH_001",
                getMessage("error.user.invalid.credentials")
            );
        }
    }

    private boolean isEmail(final String input) {
        return StringUtils.contains(input, '@');
    }

    private String normalizeUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            return username;
        }
        return StringUtils.trim(username).toLowerCase();
    }

    private String normalizeEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        return StringUtils.trim(email).toLowerCase();
    }

    private String extractNameFromEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return "User";
        }
        final int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return "User";
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
