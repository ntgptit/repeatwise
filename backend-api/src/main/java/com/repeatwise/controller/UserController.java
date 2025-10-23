package com.repeatwise.controller;

import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.UserProfileResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * User Controller - REST API for user operations
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - API Endpoints:
 *   - GET /api/users/me: Get current user profile
 *   - PUT /api/users/me: Update current user profile
 *
 * Security:
 * - All endpoints require JWT authentication
 * - User can only access/update their own profile
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    /**
     * Get current user profile
     * Endpoint: GET /api/users/me
     *
     * Use Case: UC-004 Step 1 - Access Profile Settings
     *
     * Response:
     * - 200 OK: Returns UserProfileResponse with user profile data
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @return UserProfileResponse with current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/users/me - userId: {}", LogEvent.USER_GET_PROFILE, userId);

        final UserProfileResponse profile = userService.getCurrentUserProfile(userId);

        return ResponseEntity.ok(profile);
    }

    /**
     * Update current user profile
     * Endpoint: PUT /api/users/me
     *
     * Use Case: UC-004 Step 3 - Save Changes
     *
     * Validates:
     * - name: Required, 1-100 characters
     * - timezone: Required, valid IANA timezone, max 50 characters
     * - language: Required, must be VI or EN
     * - theme: Required, must be LIGHT, DARK, or SYSTEM
     *
     * Business Rules:
     * - Username and email are read-only (cannot be changed in MVP)
     * - All fields are required (no partial updates in MVP)
     *
     * Response:
     * - 200 OK: Returns updated UserProfileResponse
     * - 400 Bad Request: Validation errors
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @param request UpdateProfileRequest with updated profile data
     * @return UserProfileResponse with updated profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody final UpdateProfileRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} PUT /api/users/me - userId: {}, request: {}", LogEvent.USER_UPDATE_PROFILE, userId, request);

        final UserProfileResponse updatedProfile = userService.updateProfile(userId, request);

        return ResponseEntity.ok(updatedProfile);
    }
}
