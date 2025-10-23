package com.repeatwise.service;

import java.util.Map;

/**
 * Email Service Interface - Handles email sending operations
 *
 * Requirements:
 * - UC-024: Manage Notifications (Step 6: Send email notifications)
 * - Email delivery for daily reminders and test notifications
 * - SMTP integration with retry mechanism
 *
 * MVP Implementation:
 * - Uses Spring Mail with SMTP
 * - HTML email templates with Thymeleaf
 * - Async sending for better performance
 *
 * @author RepeatWise Team
 */
public interface IEmailService {

    /**
     * Send daily reminder email to user
     *
     * UC-024 Step 6: Daily notification email
     * Subject: "📚 [RepeatWise] You have X cards due for review"
     * Body: HTML template with due cards count, streak info, CTA button
     *
     * @param recipientEmail Email address to send to
     * @param recipientName User's name for personalization
     * @param dueCardsCount Number of cards due for review
     * @param streakDays Current streak days (for motivation)
     * @return true if sent successfully, false otherwise
     */
    boolean sendDailyReminderEmail(
        String recipientEmail,
        String recipientName,
        int dueCardsCount,
        int streakDays
    );

    /**
     * Send test notification email
     *
     * UC-024 Step 4: Test notification feature
     * Subject: "[RepeatWise] Test Notification"
     * Body: Simple test message confirming email delivery works
     *
     * @param recipientEmail Email address to send to
     * @param recipientName User's name for personalization
     * @param dueCardsCount Current due cards count (for preview)
     * @return true if sent successfully, false otherwise
     */
    boolean sendTestNotificationEmail(
        String recipientEmail,
        String recipientName,
        int dueCardsCount
    );

    /**
     * Send generic notification email with custom content
     *
     * Generic method for future notification types
     * (streak reminders, achievements, system announcements)
     *
     * @param recipientEmail Email address
     * @param subject Email subject line
     * @param templateName Thymeleaf template name (e.g., "daily-reminder")
     * @param templateVariables Variables for template rendering
     * @return true if sent successfully, false otherwise
     */
    boolean sendTemplatedEmail(
        String recipientEmail,
        String subject,
        String templateName,
        Map<String, Object> templateVariables
    );

    /**
     * Send plain text email (fallback if HTML fails)
     *
     * Fallback method when HTML rendering fails
     * Also useful for debugging SMTP issues
     *
     * @param recipientEmail Email address
     * @param subject Email subject line
     * @param body Plain text email body
     * @return true if sent successfully, false otherwise
     */
    boolean sendPlainTextEmail(
        String recipientEmail,
        String subject,
        String body
    );

    /**
     * Validate email address format
     *
     * Used before sending to catch invalid emails early
     * Prevents bounces and reduces retry attempts
     *
     * @param email Email address to validate
     * @return true if valid email format
     */
    boolean isValidEmail(String email);
}
