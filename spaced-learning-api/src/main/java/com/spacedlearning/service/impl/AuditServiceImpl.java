package com.spacedlearning.service.impl;

import com.spacedlearning.entity.User;
import com.spacedlearning.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of AuditService using SLF4J logging
 * For production, consider using a dedicated audit logging system or database
 */
@Service
@Slf4j
public class AuditServiceImpl implements AuditService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void logRegistrationAttempt(String email, String ipAddress, boolean success, String errorMessage) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[AUDIT] REGISTRATION_ATTEMPT | %s | Email: %s | IP: %s | Status: %s | Error: %s",
                timestamp, email, ipAddress, status, errorMessage != null ? errorMessage : "N/A");
        
        if (success) {
            log.info(message);
        } else {
            log.warn(message);
        }
    }

    @Override
    public void logLoginAttempt(String usernameOrEmail, String ipAddress, boolean success, String errorMessage) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String status = success ? "SUCCESS" : "FAILED";
        String message = String.format("[AUDIT] LOGIN_ATTEMPT | %s | Username/Email: %s | IP: %s | Status: %s | Error: %s",
                timestamp, usernameOrEmail, ipAddress, status, errorMessage != null ? errorMessage : "N/A");
        
        if (success) {
            log.info(message);
        } else {
            log.warn(message);
        }
    }

    @Override
    public void logEmailVerificationAttempt(String token, String ipAddress, boolean success, String errorMessage) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String status = success ? "SUCCESS" : "FAILED";
        // Mask token for security (show only first 8 characters)
        String maskedToken = token != null && token.length() > 8 ? 
                token.substring(0, 8) + "..." : token;
        String message = String.format("[AUDIT] EMAIL_VERIFICATION_ATTEMPT | %s | Token: %s | IP: %s | Status: %s | Error: %s",
                timestamp, maskedToken, ipAddress, status, errorMessage != null ? errorMessage : "N/A");
        
        if (success) {
            log.info(message);
        } else {
            log.warn(message);
        }
    }

    @Override
    public void logUserAction(User user, String action, String resource, String ipAddress, boolean success, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String status = success ? "SUCCESS" : "FAILED";
        String userId = user != null ? user.getId().toString() : "N/A";
        String userEmail = user != null ? user.getEmail() : "N/A";
        String message = String.format("[AUDIT] USER_ACTION | %s | UserId: %s | UserEmail: %s | Action: %s | Resource: %s | IP: %s | Status: %s | Details: %s",
                timestamp, userId, userEmail, action, resource, ipAddress, status, details != null ? details : "N/A");
        
        if (success) {
            log.info(message);
        } else {
            log.warn(message);
        }
    }

    @Override
    public void logSystemEvent(String event, String details, String severity) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String message = String.format("[AUDIT] SYSTEM_EVENT | %s | Event: %s | Severity: %s | Details: %s",
                timestamp, event, severity, details != null ? details : "N/A");
        
        switch (severity.toUpperCase()) {
            case "ERROR":
                log.error(message);
                break;
            case "WARN":
                log.warn(message);
                break;
            default:
                log.info(message);
                break;
        }
    }
}

