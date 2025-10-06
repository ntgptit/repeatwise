package com.spacedlearning.service;

import com.spacedlearning.entity.EmailVerification;
import com.spacedlearning.entity.User;

/**
 * Service interface for email verification operations
 * Handles email verification token generation, validation, and cleanup
 */
public interface EmailVerificationService {

    /**
     * Create and send email verification for user
     * @param user user to create verification for
     * @return EmailVerification entity
     */
    EmailVerification createEmailVerification(User user);

    /**
     * Verify email using verification token
     * @param token verification token
     * @return true if verification was successful
     */
    boolean verifyEmail(String token);

    /**
     * Resend email verification for user
     * @param user user to resend verification for
     * @return true if verification was resent successfully
     */
    boolean resendEmailVerification(User user);

    /**
     * Clean up expired email verifications
     * @return number of expired verifications cleaned up
     */
    int cleanupExpiredVerifications();

    /**
     * Check if user has pending email verification
     * @param user user to check
     * @return true if user has pending verification
     */
    boolean hasPendingVerification(User user);
}

