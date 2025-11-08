package com.repeatwise.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.repeatwise.config.properties.JwtProperties;
import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.AuthResponse;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.RefreshTokenResponse;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.service.AuthService;
import com.repeatwise.service.JwtService;
import com.repeatwise.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of AuthService for user authentication operations.
 * Handles registration, login, logout, and token refresh.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int BCRYPT_COST_FACTOR = 12;

    private final UserRepository userRepository;
    private final SrsSettingsRepository srsSettingsRepository;
    private final UserStatsRepository userStatsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;

    /**
     * Register a new user with transactional support.
     * Creates user, SRS settings, and user stats in a single transaction.
     *
     * @param registerRequest Registration data
     * @return UUID of newly created user
     * @throws EmailAlreadyExistsException    if email already exists
     * @throws UsernameAlreadyExistsException if username already exists
     * @throws RepeatWiseException            if password and confirmPassword don't match
     */
    @Override
    @Transactional
    public UUID register(RegisterRequest registerRequest) {
        log.info("Attempting to register user with email: {}", registerRequest.getEmail());

        // Validate password match
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            log.warn("Password mismatch for email: {}", registerRequest.getEmail());
            throw new RepeatWiseException(RepeatWiseError.PASSWORD_MISMATCH);
        }

        // Normalize email
        final var normalizedEmail = registerRequest.getEmail().toLowerCase().trim();

        // Check email uniqueness
        if (this.userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("Email already exists: {}", normalizedEmail);
            throw new RepeatWiseException(RepeatWiseError.EMAIL_ALREADY_EXISTS, normalizedEmail);
        }

        // Normalize username (optional)
        final var normalizedUsername = StringUtils.hasText(registerRequest.getUsername())
                ? registerRequest.getUsername().trim()
                : null;

        // Check username uniqueness (only if provided)
        if ((normalizedUsername != null) && this.userRepository.existsByUsername(normalizedUsername)) {
            log.warn("Username already exists: {}", normalizedUsername);
            throw new RepeatWiseException(RepeatWiseError.USERNAME_ALREADY_EXISTS, normalizedUsername);
        }

        // Hash password
        final var passwordHash = BCrypt.hashpw(
                registerRequest.getPassword(),
                BCrypt.gensalt(BCRYPT_COST_FACTOR));

        // Normalize name (optional)
        final var normalizedName = StringUtils.hasText(registerRequest.getName())
                ? registerRequest.getName().trim()
                : null;

        // Create and save user
        final var savedUser = this.userRepository.save(
                User.builder()
                        .email(normalizedEmail)
                        .username(normalizedUsername)
                        .passwordHash(passwordHash)
                        .name(normalizedName)
                        .timezone("Asia/Ho_Chi_Minh")
                        .language(Language.VI)
                        .theme(Theme.SYSTEM)
                        .build());

        // User ID is guaranteed to be non-null after persist
        final var userId = savedUser.getId();
        log.info("User created successfully with ID: {}", userId);

        // Create initial SRS settings
        this.srsSettingsRepository.save(SrsSettings.createDefault(savedUser));
        log.info("SRS settings initialized for user: {}", userId);

        // Create initial user statistics
        this.userStatsRepository.save(UserStats.createDefault(savedUser));
        log.info("User stats initialized for user: {}", userId);

        log.info("User registration completed successfully for: {}", normalizedEmail);
        return userId;
    }

    /**
     * Authenticate user with username/email and password.
     * Returns JWT access token (15 min) and creates refresh token (7 days).
     *
     * @param loginRequest Login credentials
     * @return LoginResponse with access token, user info, and refresh token
     * @throws RepeatWiseException         if user not found or password is incorrect
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Attempting to login user with identifier: {}", loginRequest.getIdentifier());

        // Find user by username or email
        final var user = this.userRepository.findByUsernameOrEmail(loginRequest.getIdentifier())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", loginRequest.getIdentifier());
                    return new RepeatWiseException(RepeatWiseError.USER_NOT_FOUND, loginRequest.getIdentifier());
                });

        // Verify password using BCrypt
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid credentials for user: {}", loginRequest.getIdentifier());
            throw new RepeatWiseException(RepeatWiseError.INVALID_CREDENTIALS);
        }

        log.info("User authenticated successfully: {}", user.getEmail());

        // Generate JWT access token (15 minutes)
        final var accessToken = this.jwtService.generateAccessToken(user);

        // Generate refresh token (7 days) - will be set in HTTP-only cookie by controller
        final var refreshToken = this.refreshTokenService.createRefreshToken(
                user,
                null, // deviceId - to be extracted from request in controller
                null, // deviceInfo - to be extracted from request in controller
                null // ipAddress - to be extracted from request in controller
        );

        // Convert user entity to DTO
        final var userResponse = this.userMapper.toUserResponse(user);

        // Calculate expiry in seconds
        final var expiresIn = this.jwtProperties.getAccessTokenExpirationMinutes() * 60;

        // Build auth response
        final var authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .user(userResponse)
                .build();

        // Return login response with refresh token
        return LoginResponse.builder()
                .authResponse(authResponse)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Logout user by revoking all refresh tokens.
     *
     * @param userId User ID to logout
     */
    @Override
    @Transactional
    public void logout(UUID userId) {
        log.info("Attempting to logout user: {}", userId);

        final var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.USER_NOT_FOUND, userId));

        // Revoke all refresh tokens
        this.refreshTokenRepository.revokeAllTokensByUser(user, LocalDateTime.now());

        log.info("User logged out successfully: {}", userId);
    }

    /**
     * Refresh access token using refresh token (with token rotation).
     * Validates old refresh token, generates new access token and new refresh token.
     * Old refresh token is revoked (token rotation pattern for security).
     *
     * @param refreshToken Refresh token string
     * @param deviceId Optional device ID for tracking
     * @param deviceInfo Optional device information
     * @param ipAddress Optional IP address
     * @return RefreshTokenResponse with new access token and new refresh token
     */
    @Override
    @Transactional
    public RefreshTokenResponse refreshAccessToken(String refreshToken,
            String deviceId, String deviceInfo, String ipAddress) {
        log.info("Attempting to refresh access token");

        // Validate and rotate refresh token
        // This will: validate old token, revoke it, and generate new token
        // The method internally gets the user from old token and returns new token
        final var newRefreshToken = this.refreshTokenService.validateAndRotateRefreshToken(
                refreshToken, deviceId, deviceInfo, ipAddress);

        // We need to get the user - validateAndRotateRefreshToken returns the user internally
        // but doesn't expose it. We need to get user from the new token entity.
        // However, we should get it from the old token before rotation.
        // Let's use a different approach: validateRefreshToken first to get user,
        // then rotate separately.

        // Actually, looking at validateAndRotateRefreshToken implementation,
        // it validates first, gets user, generates new token, then revokes old one.
        // So the user is available. Let's refactor to return both user and new token.

        // For now, let's work around by getting user from the new refresh token we just created
        final var newRefreshTokenEntity = this.refreshTokenService.validateRefreshToken(newRefreshToken);
        final var user = newRefreshTokenEntity.getUser();

        // Generate new access token
        final var newAccessToken = this.jwtService.generateAccessToken(user);

        // Calculate expiry in seconds
        final var expiresIn = this.jwtProperties.getAccessTokenExpirationMinutes() * 60;

        log.info("Access token refreshed successfully for user: {}", user.getId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(expiresIn)
                .refreshToken(newRefreshToken)
                .build();
    }
}
