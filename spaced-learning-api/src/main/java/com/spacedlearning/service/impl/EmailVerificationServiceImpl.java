package com.spacedlearning.service.impl;

import com.spacedlearning.entity.EmailVerification;
import com.spacedlearning.entity.User;
import com.spacedlearning.repository.EmailVerificationRepository;
import com.spacedlearning.service.EmailService;
import com.spacedlearning.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of EmailVerificationService
 * Handles email verification operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    private static final int VERIFICATION_EXPIRY_HOURS = 24;

    @Override
    @Transactional
    public EmailVerification createEmailVerification(User user) {
        log.debug("Creating email verification for user: {}", user.getEmail());

        // Check if user already has pending verification
        if (hasPendingVerification(user)) {
            log.warn("User {} already has pending email verification", user.getEmail());
            return emailVerificationRepository.findActiveByUser(user, LocalDateTime.now())
                    .orElseThrow(() -> new IllegalStateException("Pending verification not found"));
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(VERIFICATION_EXPIRY_HOURS);

        // Create email verification entity
        EmailVerification emailVerification = EmailVerification.builder()
                .user(user)
                .verificationToken(verificationToken)
                .expiresAt(expiresAt)
                .isVerified(false)
                .build();

        // Save to database
        EmailVerification savedVerification = emailVerificationRepository.save(emailVerification);

        // Send verification email
        boolean emailSent = emailService.sendVerificationEmail(user, verificationToken);
        if (!emailSent) {
            log.error("Failed to send verification email to: {}", user.getEmail());
            // Note: We don't delete the verification record here as it might be a temporary email service issue
        }

        log.info("Email verification created for user: {} with token: {}", user.getEmail(), verificationToken);
        return savedVerification;
    }

    @Override
    @Transactional
    public boolean verifyEmail(String token) {
        log.debug("Verifying email with token: {}", token);

        EmailVerification verification = emailVerificationRepository.findByVerificationToken(token)
                .orElse(null);

        if (verification == null) {
            log.warn("Email verification token not found: {}", token);
            return false;
        }

        if (verification.isExpired()) {
            log.warn("Email verification token expired: {}", token);
            return false;
        }

        if (verification.getIsVerified()) {
            log.warn("Email verification token already used: {}", token);
            return false;
        }

        // Mark as verified
        verification.markAsVerified();
        emailVerificationRepository.save(verification);

        // Send welcome email
        emailService.sendWelcomeEmail(verification.getUser());

        log.info("Email verification successful for user: {}", verification.getUser().getEmail());
        return true;
    }

    @Override
    @Transactional
    public boolean resendEmailVerification(User user) {
        log.debug("Resending email verification for user: {}", user.getEmail());

        // Check if user has pending verification
        if (!hasPendingVerification(user)) {
            log.warn("No pending verification found for user: {}", user.getEmail());
            return false;
        }

        // Get existing verification
        EmailVerification existingVerification = emailVerificationRepository
                .findActiveByUser(user, LocalDateTime.now())
                .orElse(null);

        if (existingVerification == null) {
            log.warn("Active verification not found for user: {}", user.getEmail());
            return false;
        }

        // Resend email
        boolean emailSent = emailService.sendVerificationEmail(user, existingVerification.getVerificationToken());
        if (emailSent) {
            log.info("Email verification resent successfully to: {}", user.getEmail());
        } else {
            log.error("Failed to resend verification email to: {}", user.getEmail());
        }

        return emailSent;
    }

    @Override
    @Transactional
    public int cleanupExpiredVerifications() {
        log.debug("Cleaning up expired email verifications");

        int deletedCount = emailVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());

        log.info("Cleaned up {} expired email verifications", deletedCount);
        return deletedCount;
    }

    @Override
    public boolean hasPendingVerification(User user) {
        return emailVerificationRepository.hasPendingVerification(user, LocalDateTime.now());
    }
}
