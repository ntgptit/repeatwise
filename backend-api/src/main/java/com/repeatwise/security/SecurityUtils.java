package com.repeatwise.security;

import com.repeatwise.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * Security Utility Class
 *
 * Requirements:
 * - UC-003: User Logout - Get current authenticated user
 * - Security: Extract user ID from security context
 *
 * @author RepeatWise Team
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class - private constructor
    }

    /**
     * Get current authenticated user ID from security context
     *
     * Business Logic:
     * 1. Get authentication from SecurityContext
     * 2. Check if authenticated
     * 3. Extract user ID from principal
     * 4. Return UUID
     *
     * @return Current user UUID
     * @throws InvalidCredentialsException if user not authenticated
     */
    public static UUID getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User not authenticated");
            throw new InvalidCredentialsException(
                "AUTH_007",
                "User not authenticated"
            );
        }

        // For MVP, we assume the principal is the user ID string
        // In production, you might have a custom UserPrincipal object
        final Object principal = authentication.getPrincipal();

        if (principal instanceof String) {
            try {
                return UUID.fromString((String) principal);
            } catch (IllegalArgumentException e) {
                log.error("Invalid user ID format in principal: {}", principal);
                throw new InvalidCredentialsException(
                    "AUTH_008",
                    "Invalid user ID format"
                );
            }
        }

        log.error("Unexpected principal type: {}", principal.getClass().getName());
        throw new InvalidCredentialsException(
            "AUTH_009",
            "Invalid authentication principal"
        );
    }

    /**
     * Get current authenticated user email from security context
     *
     * @return Current user email or null if not available
     */
    public static String getCurrentUserEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Check if current user is authenticated
     *
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
