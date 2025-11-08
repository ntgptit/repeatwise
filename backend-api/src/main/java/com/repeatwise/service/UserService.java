package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.dto.request.user.UpdateUserRequest;
import com.repeatwise.dto.response.user.UserResponse;

/**
 * Service interface for user operations.
 */
public interface UserService {

    /**
     * Update user profile (UC-005).
     * Updates name, timezone, language, and theme.
     *
     * @param userId User ID from authenticated context
     * @param request Update profile data
     * @return Updated user response
     */
    UserResponse updateProfile(UUID userId, UpdateUserRequest request);

    /**
     * Change user password (UC-006).
     * Verifies current password, updates to new password, and revokes all refresh tokens.
     *
     * @param userId User ID from authenticated context
     * @param request Change password data
     */
    void changePassword(UUID userId, ChangePasswordRequest request);
}
