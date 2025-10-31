package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.ChangePasswordResponse;

/**
 * User Service Interface - Manages user operations
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - Coding Convention: Service layer with interface + implementation pattern
 *
 * @author RepeatWise Team
 */
public interface IUserService {

    /**
     * Get current user profile
     * Used by: GET /api/users/me
     *
     * @param userId authenticated user ID from JWT token
     * @return UserProfileResponse with user profile data
     */
    UserProfileResponse getCurrentUserProfile(UUID userId);

    /**
     * Update current user profile
     * Used by: PUT /api/users/me
     * UC-005: Update User Profile
     *
     * Business Rules (BR-009):
     * - name: Required, 1-100 characters
     * - timezone: Required, valid IANA timezone, max 50 characters
     * - language: Required, must be VI or EN
     * - theme: Required, must be LIGHT, DARK, or SYSTEM
     * - username and email are read-only (cannot be changed in MVP)
     *
     * @param userId  authenticated user ID from JWT token
     * @param request UpdateProfileRequest with updated profile data
     * @return UpdateProfileResponse with message and updated user profile
     */
    UpdateProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);

    /**
     * Change user password
     * Used by: POST /api/users/change-password
     * UC-006: Change Password
     *
     * Business Rules:
     * - Current password must be verified
     * - New password must be different from current password
     * - New password must be at least 8 characters
     * - All refresh tokens for user are revoked (logout from all devices)
     *
     * @param userId Current authenticated user ID
     * @param request ChangePasswordRequest with currentPassword, newPassword, confirmNewPassword
     * @return ChangePasswordResponse with success message
     * @throws InvalidCredentialsException if current password is incorrect
     */
    ChangePasswordResponse changePassword(UUID userId, com.repeatwise.dto.request.user.ChangePasswordRequest request);
}
