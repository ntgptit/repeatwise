package com.repeatwise.service.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.LogoutResponse;
import com.repeatwise.dto.response.auth.RegisterResponse;
import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.exception.DuplicateEmailException;
import com.repeatwise.exception.DuplicateUsernameException;
import com.repeatwise.exception.InvalidCredentialsException;
import com.repeatwise.exception.InvalidTokenException;
import com.repeatwise.exception.TokenReuseException;
import com.repeatwise.log.LogEvent;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.security.JwtTokenProvider;
import com.repeatwise.service.IAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
public class AuthServiceImpl extends BaseService implements IAuthService {

    private final UserRepository userRepository;
    private final SrsSettingsRepository srsSettingsRepository;
    private final UserStatsRepository userStatsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
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

    /**
     * Check username availability (case-sensitive)
     *
     * UC-001: Username check is case-sensitive
     */
    private void checkUsernameAvailability(final String username) {
        final var trimmedUsername = trimUsername(username);

        // UC-001: Username check is case-sensitive (use findByUsername, not findByUsernameIgnoreCase)
        if (this.userRepository.findByUsername(trimmedUsername).isPresent()) {
            log.warn("event={} Username already exists: {}", LogEvent.EX_DUPLICATE_RESOURCE, trimmedUsername);
            throw new DuplicateUsernameException(
                    "USER_002",
                    getMessage("error.user.username.already.exists", trimmedUsername));
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

        // UC-001: Username is case-sensitive (only trim, don't normalize)
        // If username is provided, trim it; otherwise keep null
        if (StringUtils.isNotBlank(request.getUsername())) {
            user.setUsername(trimUsername(request.getUsername()));
        } else {
            user.setUsername(null);
        }

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

    /**
     * Find user by username or email
     *
     * UC-002: Username is case-sensitive, email is case-insensitive
     * - If email: normalize to lowercase and query case-insensitive
     * - If username: keep as provided and query case-sensitive
     *
     * @param usernameOrEmail Username or email string
     * @return User entity
     */
    private User findUserByUsernameOrEmail(final String usernameOrEmail) {
        final var trimmed = StringUtils.trim(usernameOrEmail);

        // Auto-detect if input is email (contains @) or username
        final var userOptional = isEmail(trimmed)
                ? this.userRepository.findByEmailIgnoreCase(trimmed.toLowerCase())
                : this.userRepository.findByUsername(trimmed);

        return userOptional.orElseThrow(() -> {
            log.warn("event={} User not found: {}", LogEvent.EX_INVALID_CREDENTIALS, trimmed);
            return new InvalidCredentialsException(
                    "AUTH_001",
                    getMessage("error.user.invalid.credentials"));
        });
    }

    /**
     * Generate access token for user
     *
     * UC-002: JWT payload includes userId, email, username
     */
    private String generateAccessToken(final User user) {
        final var accessToken = this.jwtTokenProvider.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getUsername());

        log.debug("Generated access token for user: userId={}, email={}, username={}",
                user.getId(), user.getEmail(), user.getUsername());

        return accessToken;
    }

    /**
     * Create and save refresh token for user
     *
     * UC-002: Generate refresh token with 7-day expiry
     *
     * @param user User entity
     * @return Refresh token string (plain text, will be set in cookie)
     */
    private String createRefreshToken(final User user) {
        final var tokenString = generateRefreshTokenString();
        final var expiresAt = Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS);

        final var refreshToken = RefreshToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(expiresAt)
                .isRevoked(false)
                .build();

        this.refreshTokenRepository.save(refreshToken);

        log.debug("Created refresh token for user: userId={}, expiresAt={}", user.getId(), expiresAt);

        return tokenString;
    }

    private boolean isEmail(final String input) {
        return StringUtils.contains(input, '@');
    }

    /**
     * Login user with username or email
     *
     * UC-002: User Login
     * - Generate access token (15 minutes)
     * - Generate refresh token (7 days)
     * - Save refresh token to database
     * - Return access token and user info
     * - Refresh token is returned separately (set in cookie by controller)
     */
    @Transactional
    @Override
    public LoginResponse login(final LoginRequest request) {
        log.info("event={} Login attempt: usernameOrEmail={}", LogEvent.AUTH_LOGIN_START, request.getUsernameOrEmail());

        validateLoginRequest(request);

        final var user = findUserByUsernameOrEmail(request.getUsernameOrEmail());
        verifyPassword(request.getPassword(), user.getPasswordHash());

        final var accessToken = generateAccessToken(user);
        final var expiresIn = this.jwtTokenProvider.getAccessTokenExpirationSeconds();
        final var refreshToken = createRefreshToken(user);
        final var userResponse = this.userMapper.toResponse(user);

        log.info("event={} User logged in: userId={}, username={}, email={}",
                LogEvent.AUTH_LOGIN_SUCCESS, user.getId(), user.getUsername(), user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .user(userResponse)
                .build();
    }

    /**
     * Logout user from current device (revoke single refresh token)
     * UC-004: User Logout - Idempotent operation, graceful degradation
     *
     * Business Logic:
     * - If refresh token is blank → return success (nothing to revoke)
     * - If token not found → return success (idempotent)
     * - If token already revoked → return success (idempotent)
     * - Otherwise → revoke token and return success
     */
    @Transactional
    @Override
    public LogoutResponse logout(final String refreshToken, final UUID userId) {
        log.info("event={} Logout attempt: userId={}", LogEvent.AUTH_TOKEN_REVOKE, userId);

        // UC-004: Graceful degradation - logout succeeds even without refresh token
        if (StringUtils.isBlank(refreshToken)) {
            log.info("event={} Logout without refresh token: userId={}", LogEvent.SUCCESS, userId);
            return LogoutResponse.builder()
                    .message(getMessage(MSG_SUCCESS_LOGOUT))
                    .build();
        }

        // UC-004: Try to find token - if not found, logout still succeeds (idempotent)
        final var tokenOptional = this.refreshTokenRepository.findByToken(refreshToken);
        if (tokenOptional.isEmpty()) {
            log.info("event={} Logout - token not found (already logged out?): userId={}",
                    LogEvent.SUCCESS, userId);
            return LogoutResponse.builder()
                    .message(getMessage(MSG_SUCCESS_LOGOUT))
                    .build();
        }

        final var token = tokenOptional.get();

        // UC-004: Verify token ownership - if doesn't belong to user, still succeed (graceful)
        if (!token.getUser().getId().equals(userId)) {
            log.warn("event={} Logout - token doesn't belong to user: userId={}, tokenUserId={}",
                    LogEvent.EX_INVALID_TOKEN, userId, token.getUser().getId());
            return LogoutResponse.builder()
                    .message(getMessage(MSG_SUCCESS_LOGOUT))
                    .build();
        }

        // UC-004: If already revoked, logout still succeeds (idempotent)
        if (Boolean.TRUE.equals(token.getIsRevoked())) {
            log.info("event={} Logout - token already revoked: userId={}, tokenId={}",
                    LogEvent.SUCCESS, userId, token.getId());
            return LogoutResponse.builder()
                    .message(getMessage(MSG_SUCCESS_LOGOUT))
                    .build();
        }

        // Revoke token
        revokeToken(token);

        log.info("event={} User logged out: userId={}, tokenId={}",
                LogEvent.SUCCESS, userId, token.getId());

        return LogoutResponse.builder()
                .message(getMessage(MSG_SUCCESS_LOGOUT))
                .build();
    }

    /**
     * Trim username (keep case, only trim whitespace)
     *
     * UC-001: Username is case-sensitive, only trim leading/trailing whitespace
     */
    private String trimUsername(final String username) {
        if (StringUtils.isBlank(username)) {
            return username;
        }
        return StringUtils.trim(username);
    }

    private String normalizeEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return email;
        }
        return StringUtils.trim(email).toLowerCase();
    }

    /**
     * Register new user account
     *
     * UC-001: User Registration
     * - Username is optional (case-sensitive if provided)
     * - Email is case-insensitive
     * - Default language: VI
     * - Response format: { message, userId }
     */
    @Transactional
    @Override
    public RegisterResponse register(final RegisterRequest request) {
        log.info("event={} Starting user registration: username={}, email={}",
                LogEvent.AUTH_REGISTER_START, request.getUsername(), request.getEmail());

        validateRegistrationRequest(request);

        // UC-001: Only check username availability if username is provided
        if (StringUtils.isNotBlank(request.getUsername())) {
            checkUsernameAvailability(request.getUsername());
        }

        checkEmailAvailability(request.getEmail());

        final var user = createUser(request);
        final var savedUser = this.userRepository.save(user);

        createDefaultSettings(savedUser);

        log.info("User registered successfully: userId={}, username={}, email={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return RegisterResponse.builder()
                .message("Registration successful. Please login.")
                .userId(savedUser.getId())
                .build();
    }

    private static final String MSG_SUCCESS_LOGOUT = "success.auth.logout";

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

    /**
     * Validate registration request
     *
     * UC-001: Validate email, password, confirmPassword
     * Username is optional (validated by @Pattern if provided)
     */
    private void validateRegistrationRequest(final RegisterRequest request) {
        Objects.requireNonNull(request, "RegisterRequest cannot be null");

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

        // UC-001: Validate confirmPassword matches password
        if (StringUtils.isBlank(request.getConfirmPassword())) {
            log.error("Registration validation failed: confirmPassword is blank");
            throw new IllegalArgumentException(
                    getMessage("error.user.confirm.password.required"));
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.error("Registration validation failed: password mismatch");
            throw new IllegalArgumentException(
                    getMessage("error.user.password.mismatch"));
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

    @Transactional
    @Override
    public LoginResponse refreshToken(final String refreshToken) {
        log.info("event={} Refresh token attempt", LogEvent.AUTH_TOKEN_REFRESH);

        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidTokenException(
                    "AUTH_007",
                    getMessage("error.auth.refresh.token.missing"));
        }

        final var token = findRefreshToken(refreshToken);
        validateRefreshToken(token);

        final var user = token.getUser();
        final var newAccessToken = generateAccessToken(user);
        final var expiresIn = this.jwtTokenProvider.getAccessTokenExpirationSeconds();

        // Token rotation: revoke old token and create new one
        final var newRefreshTokenString = rotateRefreshToken(token, user);

        log.info("event={} Token refreshed successfully: userId={}", LogEvent.AUTH_TOKEN_REFRESH, user.getId());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(expiresIn)
                .refreshToken(newRefreshTokenString)
                .build();
    }

    private String generateRefreshTokenString() {
        return UUID.randomUUID().toString();
    }

    /**
     * Rotate refresh token (revoke old, create new)
     *
     * UC-003: Token rotation for security
     *
     * @param oldToken Old refresh token to revoke
     * @param user     User entity
     * @return New refresh token string (plain text)
     */
    private String rotateRefreshToken(final RefreshToken oldToken, final User user) {
        // Revoke old token
        oldToken.revoke();
        this.refreshTokenRepository.save(oldToken);

        // Create new refresh token
        final var newTokenString = generateRefreshTokenString();
        final var expiresAt = Instant.now().plus(7, java.time.temporal.ChronoUnit.DAYS);

        final var newToken = RefreshToken.builder()
                .token(newTokenString)
                .user(user)
                .expiresAt(expiresAt)
                .isRevoked(false)
                .build();

        this.refreshTokenRepository.save(newToken);

        log.debug("Token rotated: oldTokenId={}, newTokenId={}", oldToken.getId(), newToken.getId());

        return newTokenString;
    }

    /**
     * Validate refresh token
     * UC-003: Token reuse detection - if revoked token is reused, revoke all user tokens
     */
    private void validateRefreshToken(final RefreshToken token) {
        // UC-003: Token reuse detection - if token is already revoked, this is a reuse attempt
        if (Boolean.TRUE.equals(token.getIsRevoked())) {
            log.warn("event={} Token reuse detected: tokenId={}, userId={}",
                    LogEvent.EX_INVALID_TOKEN, token.getId(), token.getUser().getId());

            // UC-003: Security measure - revoke ALL tokens for this user
            final var revokedAt = Instant.now();
            final var revokedCount = this.refreshTokenRepository.revokeAllByUserId(
                    token.getUser().getId(), revokedAt);

            log.error("event={} Token reuse detected - revoked all tokens for user: userId={}, tokensRevoked={}",
                    LogEvent.EX_INVALID_TOKEN, token.getUser().getId(), revokedCount);

            throw new TokenReuseException(
                    "AUTH_010",
                    getMessage("error.auth.token.reuse.detected"));
        }

        if (token.isExpired()) {
            log.warn("Refresh failed: token expired: tokenId={}, expiresAt={}",
                    token.getId(), token.getExpiresAt());
            throw new InvalidTokenException(
                    "AUTH_009",
                    getMessage("error.auth.refresh.token.expired"));
        }
    }
}
