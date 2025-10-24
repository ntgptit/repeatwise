package com.repeatwise.service;

import java.util.UUID;

import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.UserProfileResponse;

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
     * @return UserProfileResponse with updated profile
     */
    UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request);
}
