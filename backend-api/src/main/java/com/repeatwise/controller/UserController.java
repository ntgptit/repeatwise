package com.repeatwise.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.dto.request.user.UpdateUserRequest;
import com.repeatwise.dto.response.user.UserResponse;
import com.repeatwise.entity.User;
import com.repeatwise.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for user profile and password operations.
 */
@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile and password management endpoints")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    /**
     * Update user profile (UC-005).
     * Updates name, timezone, language, and theme.
     */
    @PatchMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update authenticated user's profile settings")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Profile update request for user: {}", user.getId());

        final var updatedUser = userService.updateProfile(user.getId(), request);

        final var response = new HashMap<String, Object>();
        final var locale = LocaleContextHolder.getLocale();
        final var successMessage = messageSource.getMessage("success.user.profile.updated", null, locale);
        response.put("message", successMessage);
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);
    }

    /**
     * Change user password (UC-006).
     * Revokes all refresh tokens and clears current session cookie.
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change password and logout from all devices")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletResponse response) {
        log.info("Password change request for user: {}", user.getId());

        userService.changePassword(user.getId(), request);

        // Clear refresh token cookie
        final var cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);

        final var locale = LocaleContextHolder.getLocale();
        final var successMessage = messageSource.getMessage("success.user.password.changed", null, locale);

        final var responseBody = new HashMap<String, String>();
        responseBody.put("message", successMessage);

        log.info("Password changed successfully for user: {}", user.getId());
        return ResponseEntity.ok(responseBody);
    }
}
