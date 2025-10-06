package com.spacedlearning.service;

import com.spacedlearning.entity.User;

/**
 * Service interface for email operations
 * Handles sending various types of emails including verification emails
 */
public interface EmailService {

    /**
     * Send email verification to user
     * @param user user to send verification email to
     * @param verificationToken verification token
     * @return true if email was sent successfully
     */
    boolean sendVerificationEmail(User user, String verificationToken);

    /**
     * Send welcome email to newly registered user
     * @param user newly registered user
     * @return true if email was sent successfully
     */
    boolean sendWelcomeEmail(User user);

    /**
     * Send password reset email
     * @param user user requesting password reset
     * @param resetToken password reset token
     * @return true if email was sent successfully
     */
    boolean sendPasswordResetEmail(User user, String resetToken);

    /**
     * Check if email service is available
     * @return true if email service is available
     */
    boolean isEmailServiceAvailable();
}

