package com.spacedlearning.service;

import com.spacedlearning.entity.User;

/**
 * Service interface for audit logging operations
 * Handles logging of user actions and system events for security and compliance
 */
public interface AuditService {

    /**
     * Log user registration attempt
     * @param email user email
     * @param ipAddress client IP address
     * @param success whether registration was successful
     * @param errorMessage error message if registration failed
     */
    void logRegistrationAttempt(String email, String ipAddress, boolean success, String errorMessage);

    /**
     * Log user login attempt
     * @param usernameOrEmail username or email used for login
     * @param ipAddress client IP address
     * @param success whether login was successful
     * @param errorMessage error message if login failed
     */
    void logLoginAttempt(String usernameOrEmail, String ipAddress, boolean success, String errorMessage);

    /**
     * Log email verification attempt
     * @param token verification token
     * @param ipAddress client IP address
     * @param success whether verification was successful
     * @param errorMessage error message if verification failed
     */
    void logEmailVerificationAttempt(String token, String ipAddress, boolean success, String errorMessage);

    /**
     * Log user action
     * @param user user performing the action
     * @param action action performed
     * @param resource resource affected
     * @param ipAddress client IP address
     * @param success whether action was successful
     * @param details additional details
     */
    void logUserAction(User user, String action, String resource, String ipAddress, boolean success, String details);

    /**
     * Log system event
     * @param event system event
     * @param details event details
     * @param severity event severity (INFO, WARN, ERROR)
     */
    void logSystemEvent(String event, String details, String severity);
}

