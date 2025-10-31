package com.repeatwise.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.dto.request.user.UpdateProfileRequest;
import com.repeatwise.dto.response.user.ChangePasswordResponse;
import com.repeatwise.dto.response.user.UpdateProfileResponse;
import com.repeatwise.dto.response.user.UserProfileResponse;
import com.repeatwise.log.LogEvent;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * User Controller - REST API for user operations
 *
 * Requirements:
 * - UC-004: User Profile Management
 * - API Endpoints:
 * - GET /api/users/me: Get current user profile
 * - PUT /api/users/me: Update current user profile
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
        final var userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/users/me - userId: {}", LogEvent.USER_GET_PROFILE, userId);

        final UserProfileResponse profile = this.userService.getCurrentUserProfile(userId);

        return ResponseEntity.ok(profile);
    }

    /**
     * Update current user profile
     * UC-005: Update User Profile
     * Endpoint: PUT /api/users/me
     *
     * Use Case: UC-005 Step 9 - Save Changes
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
     * - 200 OK: Returns UpdateProfileResponse with message and updated user profile
     * - 400 Bad Request: Validation errors
     * - 401 Unauthorized: User not authenticated
     * - 404 Not Found: User not found
     *
     * @param request UpdateProfileRequest with updated profile data
     * @return UpdateProfileResponse with message and updated user profile
     */
    @PutMapping("/me")
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @Valid @RequestBody final UpdateProfileRequest request) {

        final var userId = SecurityUtils.getCurrentUserId();

        log.info("event={} PUT /api/users/me - userId: {}, request: {}", LogEvent.USER_UPDATE_PROFILE, userId, request);

        final UpdateProfileResponse response = this.userService.updateProfile(userId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * Change user password
     * UC-006: Change Password
     * Endpoint: POST /api/users/change-password
     *
     * Use Case: UC-006 Step 18 - Password Changed Successfully
     *
     * Validates:
     * - currentPassword: Required
     * - newPassword: Required, min 8 characters
     * - confirmNewPassword: Required, must match newPassword
     *
     * Business Rules:
     * - Current password must be verified
     * - New password must be different from current password
     * - All refresh tokens are revoked (logout from all devices)
     *
     * Response:
     * - 200 OK: Returns ChangePasswordResponse with success message
     * - 400 Bad Request: Validation errors or incorrect current password
     * - 401 Unauthorized: User not authenticated
     *
     * Set-Cookie Header:
     * - Clears refresh_token cookie (Max-Age=0)
     *
     * @param request ChangePasswordRequest with currentPassword, newPassword, confirmNewPassword
     * @return ResponseEntity with ChangePasswordResponse and Set-Cookie header to clear refresh token
     */
    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @Valid @RequestBody final ChangePasswordRequest request) {

        final var userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/users/change-password - userId: {}", LogEvent.USER_CHANGE_PASSWORD, userId);

        final var response = this.userService.changePassword(userId, request);

        // UC-006: Clear refresh token cookie
        final var clearCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true) // HTTPS only in production
                .sameSite("Strict")
                .maxAge(0) // Expires immediately
                .path("/api/auth")
                .build();

        log.info("event={} Password changed successfully: userId: {}", LogEvent.SUCCESS, userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(response);
    }
}
