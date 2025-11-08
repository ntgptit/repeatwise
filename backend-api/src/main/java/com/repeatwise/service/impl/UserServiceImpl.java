package com.repeatwise.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.dto.request.user.UpdateUserRequest;
import com.repeatwise.dto.response.user.UserResponse;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of UserService for user profile and password operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int BCRYPT_COST_FACTOR = 12;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;

    /**
     * Update user profile (UC-005).
     */
    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateUserRequest request) {
        log.info("Updating profile for user: {}", userId);

        // Find user
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.USER_NOT_FOUND, userId));

        // Update fields using MapStruct (only non-null fields)
        userMapper.updateEntityFromRequest(request, user);

        // Trim name if provided
        if (StringUtils.hasText(user.getName())) {
            user.setName(user.getName().trim());
        }

        // Save updated user
        final var updatedUser = userRepository.save(user);

        log.info("Profile updated successfully for user: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * Change user password (UC-006).
     * Verifies current password, updates password hash, and revokes all refresh tokens.
     */
    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            log.warn("New passwords do not match for user: {}", userId);
            throw new RepeatWiseException(RepeatWiseError.PASSWORD_MISMATCH);
        }

        // Find user
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.USER_NOT_FOUND, userId));

        // Verify current password
        if (!BCrypt.checkpw(request.getCurrentPassword(), user.getPasswordHash())) {
            log.warn("Current password incorrect for user: {}", userId);
            throw new RepeatWiseException(RepeatWiseError.INCORRECT_CURRENT_PASSWORD);
        }

        // Check if new password is same as current (optional validation)
        if (BCrypt.checkpw(request.getNewPassword(), user.getPasswordHash())) {
            log.warn("New password same as current for user: {}", userId);
            throw new RepeatWiseException(RepeatWiseError.SAME_PASSWORD);
        }

        // Hash new password
        final var newPasswordHash = BCrypt.hashpw(
                request.getNewPassword(),
                BCrypt.gensalt(BCRYPT_COST_FACTOR));

        // Update password
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);

        // Revoke all refresh tokens (logout from all devices)
        refreshTokenRepository.revokeAllTokensByUser(user, LocalDateTime.now());

        log.info("Password changed successfully and all tokens revoked for user: {}", userId);
    }
}
