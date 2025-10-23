package com.repeatwise.service.impl;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.exception.*;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.security.JwtTokenProvider;
import com.repeatwise.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    @Override
    public UserResponse register(final RegisterRequest request) {
        log.info("event={} Starting user registration: username={}, email={}",
            LogEvent.AUTH_REGISTER_START, request.getUsername(), request.getEmail());

        validateRegistrationRequest(request);
        checkUsernameAvailability(request.getUsername());
        checkEmailAvailability(request.getEmail());

        final User user = createUser(request);
        final User savedUser = userRepository.save(user);

        createDefaultSettings(savedUser);

        log.info("event={} User registered successfully: userId={}, username={}, email={}",
            LogEvent.AUTH_REGISTER_SUCCESS, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    private void validateRegistrationRequest(final RegisterRequest request) {
        Objects.requireNonNull(request, "RegisterRequest cannot be null");

        if (StringUtils.isBlank(request.getUsername())) {
            log.error("event={} Registration validation failed: username is blank", LogEvent.EX_VALIDATION);
            throw new IllegalArgumentException(
                getMessage("error.user.username.required"));
        }

        if (StringUtils.isBlank(request.getEmail())) {
            log.error("event={} Registration validation failed: email is blank", LogEvent.EX_VALIDATION);
            throw new IllegalArgumentException(
                getMessage("error.user.email.required"));
        }

        if (StringUtils.isBlank(request.getPassword())) {
            log.error("event={} Registration validation failed: password is blank", LogEvent.EX_VALIDATION);
            throw new IllegalArgumentException(
                getMessage("error.user.password.required"));
        }
    }

    private void checkUsernameAvailability(final String username) {
        final String normalizedUsername = normalizeUsername(username);

        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            log.warn("event={} Registration failed: username already exists: {}", LogEvent.EX_DUPLICATE_RESOURCE, normalizedUsername);
            throw new DuplicateUsernameException(
                "USER_002",
                getMessage("error.user.username.already.exists", normalizedUsername)
            );
        }
    }

    private void checkEmailAvailability(final String email) {
        final String normalizedEmail = normalizeEmail(email);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("event={} Registration failed: email already exists: {}", LogEvent.EX_DUPLICATE_RESOURCE, normalizedEmail);
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

        log.debug("event={} Created default SRS settings for user: userId={}", LogEvent.START, user.getId());

        // Create default user stats
        final UserStats userStats = UserStats.createDefault(user);
        userStatsRepository.save(userStats);

        log.debug("event={} Created default user stats for user: userId={}", LogEvent.START, user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(final LoginRequest request) {
        log.info("event={} Login attempt: usernameOrEmail={}", LogEvent.AUTH_LOGIN_START, request.getUsernameOrEmail());

        validateLoginRequest(request);

        final User user = findUserByUsernameOrEmail(request.getUsernameOrEmail());
        verifyPassword(request.getPassword(), user.getPasswordHash());

        final String accessToken = generateAccessToken(user);
        final long expiresIn = jwtTokenProvider.getAccessTokenExpirationSeconds();

        log.info("event={} User logged in successfully: userId={}, username={}, email={}",
            LogEvent.AUTH_LOGIN_SUCCESS, user.getId(), user.getUsername(), user.getEmail());

        return LoginResponse.builder()
            .accessToken(accessToken)
            .expiresIn(expiresIn)
            .build();
    }

    private String generateAccessToken(final User user) {
        final String accessToken = jwtTokenProvider.generateAccessToken(
            user.getId(),
            user.getEmail()
        );

        log.debug("event={} Generated access token for user: userId={}, email={}",
            LogEvent.AUTH_LOGIN_SUCCESS, user.getId(), user.getEmail());

        return accessToken;
    }

    private void validateLoginRequest(final LoginRequest request) {
        Objects.requireNonNull(request, "LoginRequest cannot be null");

        if (StringUtils.isBlank(request.getUsernameOrEmail())) {
            log.error("event={} Login validation failed: usernameOrEmail is blank", LogEvent.EX_VALIDATION);
            throw new IllegalArgumentException(
                getMessage("error.user.username.or.email.required"));
        }

        if (StringUtils.isBlank(request.getPassword())) {
            log.error("event={} Login validation failed: password is blank", LogEvent.EX_VALIDATION);
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
            log.warn("event={} Login failed: user not found: {}", LogEvent.EX_INVALID_CREDENTIALS, normalized);
            return new InvalidCredentialsException(
                "AUTH_001",
                getMessage("error.user.invalid.credentials")
            );
        });
    }

    private void verifyPassword(final String rawPassword, final String passwordHash) {
        if (!passwordEncoder.matches(rawPassword, passwordHash)) {
            log.warn("event={} Login failed: incorrect password", LogEvent.EX_INVALID_CREDENTIALS);
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

    @Transactional
    @Override
    public void logout(final String refreshToken, final UUID userId) {
        log.info("event={} Logout attempt: userId={}", LogEvent.AUTH_TOKEN_REVOKE, userId);

        validateLogoutRequest(refreshToken);

        final RefreshToken token = findRefreshToken(refreshToken);
        verifyTokenOwnership(token, userId);

        revokeToken(token);

        log.info("event={} User logged out successfully: userId={}, tokenId={}",
            LogEvent.AUTH_TOKEN_REVOKE, userId, token.getId());
    }

    private void validateLogoutRequest(final String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            log.error("event={} Logout validation failed: refresh token is blank", LogEvent.EX_VALIDATION);
            throw new InvalidTokenException(
                "AUTH_002",
                getMessage("error.auth.token.required")
            );
        }
    }

    private RefreshToken findRefreshToken(final String tokenString) {
        return refreshTokenRepository.findByToken(tokenString)
            .orElseThrow(() -> {
                log.warn("event={} Logout failed: refresh token not found", LogEvent.EX_INVALID_TOKEN);
                return new InvalidTokenException(
                    "AUTH_003",
                    getMessage("error.auth.token.invalid")
                );
            });
    }

    private void verifyTokenOwnership(final RefreshToken token, final UUID userId) {
        if (!token.getUser().getId().equals(userId)) {
            log.warn("event={} Logout failed: token does not belong to user: userId={}, tokenUserId={}",
                LogEvent.EX_FORBIDDEN, userId, token.getUser().getId());
            throw new ForbiddenException(
                "AUTH_004",
                getMessage("error.auth.token.not.owned")
            );
        }

        if (Boolean.TRUE.equals(token.getIsRevoked())) {
            log.warn("event={} Logout failed: token already revoked: tokenId={}", LogEvent.EX_INVALID_TOKEN, token.getId());
            throw new InvalidTokenException(
                "AUTH_005",
                getMessage("error.auth.token.already.revoked")
            );
        }

        if (token.isExpired()) {
            log.warn("event={} Logout failed: token expired: tokenId={}, expiresAt={}",
                LogEvent.EX_INVALID_TOKEN, token.getId(), token.getExpiresAt());
            throw new InvalidTokenException(
                "AUTH_006",
                getMessage("error.auth.token.expired")
            );
        }
    }

    private void revokeToken(final RefreshToken token) {
        token.revoke();
        refreshTokenRepository.save(token);
        log.debug("event={} Refresh token revoked: tokenId={}", LogEvent.AUTH_TOKEN_REVOKE, token.getId());
    }

    @Transactional
    @Override
    public void logoutAll(final UUID userId) {
        log.info("event={} Logout-all attempt: userId={}", LogEvent.AUTH_TOKEN_REVOKE, userId);

        final User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("event={} Logout-all failed: user not found: userId={}", LogEvent.EX_RESOURCE_NOT_FOUND, userId);
                return new ResourceNotFoundException(
                    "USER_003",
                    getMessage("error.user.not.found", userId)
                );
            });

        final int revokedCount = refreshTokenRepository.revokeAllByUserId(
            userId,
            Instant.now()
        );

        log.info("event={} User logged out from all devices: userId={}, tokensRevoked={}",
            LogEvent.AUTH_TOKEN_REVOKE, userId, revokedCount);
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
