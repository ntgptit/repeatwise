package com.repeatwise.service.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.exception.DuplicateEmailException;
import com.repeatwise.exception.DuplicateUsernameException;
import com.repeatwise.exception.ForbiddenException;
import com.repeatwise.exception.InvalidCredentialsException;
import com.repeatwise.exception.InvalidTokenException;
import com.repeatwise.exception.ResourceNotFoundException;
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

    private void checkEmailAvailability(final String email) {
        final var normalizedEmail = normalizeEmail(email);

        if (this.userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("event={} Email already exists: {}", LogEvent.EX_DUPLICATE_RESOURCE, normalizedEmail);
            throw new DuplicateEmailException(
                    "USER_001",
                    getMessage("error.user.email.already.exists", normalizedEmail));
        }
    }

    private void checkUsernameAvailability(final String username) {
        final var normalizedUsername = normalizeUsername(username);

        if (this.userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            log.warn("event={} Username already exists: {}", LogEvent.EX_DUPLICATE_RESOURCE, normalizedUsername);
            throw new DuplicateUsernameException(
                    "USER_002",
                    getMessage("error.user.username.already.exists", normalizedUsername));
        }
    }

    private void createDefaultSettings(final User user) {
        // Create default SRS settings
        final var srsSettings = SrsSettings.createDefault(user);
        this.srsSettingsRepository.save(srsSettings);

        log.debug("event={} Created default SRS settings for user: userId={}", LogEvent.SUCCESS, user.getId());

        // Create default user stats
        final var userStats = UserStats.createDefault(user);
        this.userStatsRepository.save(userStats);

        log.debug("event={} Created default user stats for user: userId={}", LogEvent.SUCCESS, user.getId());
    }

    private User createUser(final RegisterRequest request) {
        final var user = this.userMapper.toEntity(request);

        // Normalize and set username (lowercase)
        user.setUsername(normalizeUsername(request.getUsername()));

        // Normalize and set email (lowercase)
        user.setEmail(normalizeEmail(request.getEmail()));

        // Hash password with bcrypt (cost 12)
        final var hashedPassword = this.passwordEncoder.encode(request.getPassword());
        user.setPasswordHash(hashedPassword);

        // Set default name if not provided
        if (StringUtils.isBlank(user.getName())) {
            user.setName(extractNameFromEmail(user.getEmail()));
        }

        return user;
    }

    private String extractNameFromEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return "User";
        }
        final var atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return "User";
    }

    private RefreshToken findRefreshToken(final String tokenString) {
        return this.refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> {
                    log.warn("event={} Refresh token not found", LogEvent.EX_INVALID_TOKEN);
                    return new InvalidTokenException(
                            "AUTH_003",
                            getMessage("error.auth.token.invalid"));
                });
    }

    private User findUserByUsernameOrEmail(final String usernameOrEmail) {
        final var normalized = StringUtils.trim(usernameOrEmail).toLowerCase();

        // Auto-detect if input is email (contains @) or username
        final var userOptional = isEmail(normalized)
                ? this.userRepository.findByEmailIgnoreCase(normalized)
                : this.userRepository.findByUsernameIgnoreCase(normalized);

        return userOptional.orElseThrow(() -> {
            log.warn("event={} User not found: {}", LogEvent.EX_INVALID_CREDENTIALS, normalized);
            return new InvalidCredentialsException(
                    "AUTH_001",
                    getMessage("error.user.invalid.credentials"));
        });
    }

    private String generateAccessToken(final User user) {
        final var accessToken = this.jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail());

        log.debug("Generated access token for user: userId={}, email={}",
                user.getId(), user.getEmail());

        return accessToken;
    }

    private String getMessage(final String code, final Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private boolean isEmail(final String input) {
        return StringUtils.contains(input, '@');
    }

    @Transactional(readOnly = true)
    @Override
    public LoginResponse login(final LoginRequest request) {
        log.info("event={} Login attempt: usernameOrEmail={}", LogEvent.AUTH_LOGIN_START, request.getUsernameOrEmail());

        validateLoginRequest(request);

        final var user = findUserByUsernameOrEmail(request.getUsernameOrEmail());
        verifyPassword(request.getPassword(), user.getPasswordHash());

        final var accessToken = generateAccessToken(user);
        final var expiresIn = this.jwtTokenProvider.getAccessTokenExpirationSeconds();

        log.info("event={} User logged in: userId={}, username={}, email={}",
                LogEvent.AUTH_LOGIN_SUCCESS, user.getId(), user.getUsername(), user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .build();
    }

    @Transactional
    @Override
    public void logout(final String refreshToken, final UUID userId) {
        log.info("event={} Logout attempt: userId={}", LogEvent.AUTH_TOKEN_REVOKE, userId);

        validateLogoutRequest(refreshToken);

        final var token = findRefreshToken(refreshToken);
        verifyTokenOwnership(token, userId);

        revokeToken(token);

        log.info("event={} User logged out: userId={}, tokenId={}",
                LogEvent.SUCCESS, userId, token.getId());
    }

    @Transactional
    @Override
    public void logoutAll(final UUID userId) {
        log.info("Logout-all attempt: userId={}", userId);

        this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("event={} Logout-all failed: user not found: userId={}", LogEvent.EX_RESOURCE_NOT_FOUND, userId);
                    return new ResourceNotFoundException(
                            "USER_003",
                            getMessage("error.user.not.found", userId));
                });

        final var revokedCount = this.refreshTokenRepository.revokeAllByUserId(
                userId,
                Instant.now());

        log.info("event={} Logout-all success: userId={}, tokensRevoked={}",
                LogEvent.SUCCESS, userId, revokedCount);
    }

    private String normalizeEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        return StringUtils.trim(email).toLowerCase();
    }

    private String normalizeUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            return username;
        }
        return StringUtils.trim(username).toLowerCase();
    }

    @Transactional
    @Override
    public UserResponse register(final RegisterRequest request) {
        log.info("event={} Starting user registration: username={}, email={}",
                LogEvent.AUTH_REGISTER_START, request.getUsername(), request.getEmail());

        validateRegistrationRequest(request);
        checkUsernameAvailability(request.getUsername());
        checkEmailAvailability(request.getEmail());

        final var user = createUser(request);
        final var savedUser = this.userRepository.save(user);

        createDefaultSettings(savedUser);

        log.info("User registered successfully: userId={}, username={}, email={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return this.userMapper.toResponse(savedUser);
    }

    private void revokeToken(final RefreshToken token) {
        token.revoke();
        this.refreshTokenRepository.save(token);
        log.debug("Refresh token revoked: tokenId={}", token.getId());
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

    private void validateLogoutRequest(final String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            log.error("Logout validation failed: refresh token is blank");
            throw new InvalidTokenException(
                    "AUTH_002",
                    getMessage("error.auth.token.required"));
        }
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

    private void verifyPassword(final String rawPassword, final String passwordHash) {
        if (!this.passwordEncoder.matches(rawPassword, passwordHash)) {
            log.warn("Login failed: incorrect password");
            throw new InvalidCredentialsException(
                    "AUTH_001",
                    getMessage("error.user.invalid.credentials"));
        }
    }

    private void verifyTokenOwnership(final RefreshToken token, final UUID userId) {
        if (!token.getUser().getId().equals(userId)) {
            log.warn("Logout failed: token does not belong to user: userId={}, tokenUserId={}",
                    userId, token.getUser().getId());
            throw new ForbiddenException(
                    "AUTH_004",
                    getMessage("error.auth.token.not.owned"));
        }

        if (Boolean.TRUE.equals(token.getIsRevoked())) {
            log.warn("Logout failed: token already revoked: tokenId={}", token.getId());
            throw new InvalidTokenException(
                    "AUTH_005",
                    getMessage("error.auth.token.already.revoked"));
        }

        if (token.isExpired()) {
            log.warn("Logout failed: token expired: tokenId={}, expiresAt={}",
                    token.getId(), token.getExpiresAt());
            throw new InvalidTokenException(
                    "AUTH_006",
                    getMessage("error.auth.token.expired"));
        }
    }
}
