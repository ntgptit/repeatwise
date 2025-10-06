package com.spacedlearning.service;

import com.spacedlearning.entity.EmailVerification;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.repository.EmailVerificationRepository;
import com.spacedlearning.service.impl.EmailVerificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for EmailVerificationService
 * Tests email verification functionality for UC-001: User Registration
 */
@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailVerificationServiceImpl emailVerificationService;

    private User testUser;
    private EmailVerification testVerification;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .build();

        testVerification = EmailVerification.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .verificationToken("test-token-123")
                .expiresAt(LocalDateTime.now().plusHours(24))
                .isVerified(false)
                .build();
    }

    @Test
    void createEmailVerification_WithValidUser_ShouldCreateAndSendEmail() {
        // Given
        when(emailVerificationRepository.hasPendingVerification(eq(testUser), any(LocalDateTime.class)))
                .thenReturn(false);
        when(emailVerificationRepository.save(any(EmailVerification.class)))
                .thenReturn(testVerification);
        when(emailService.sendVerificationEmail(eq(testUser), anyString()))
                .thenReturn(true);

        // When
        EmailVerification result = emailVerificationService.createEmailVerification(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getVerificationToken());
        assertFalse(result.getIsVerified());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));

        verify(emailVerificationRepository).save(any(EmailVerification.class));
        verify(emailService).sendVerificationEmail(eq(testUser), anyString());
    }

    @Test
    void createEmailVerification_WithPendingVerification_ShouldReturnExisting() {
        // Given
        when(emailVerificationRepository.hasPendingVerification(eq(testUser), any(LocalDateTime.class)))
                .thenReturn(true);
        when(emailVerificationRepository.findActiveByUser(eq(testUser), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testVerification));

        // When
        EmailVerification result = emailVerificationService.createEmailVerification(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testVerification, result);

        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
        verify(emailService, never()).sendVerificationEmail(any(User.class), anyString());
    }

    @Test
    void verifyEmail_WithValidToken_ShouldReturnTrue() {
        // Given
        String validToken = "valid-token-123";
        when(emailVerificationRepository.findByVerificationToken(validToken))
                .thenReturn(Optional.of(testVerification));
        when(emailVerificationRepository.save(any(EmailVerification.class)))
                .thenReturn(testVerification);
        when(emailService.sendWelcomeEmail(testUser))
                .thenReturn(true);

        // When
        boolean result = emailVerificationService.verifyEmail(validToken);

        // Then
        assertTrue(result);
        assertTrue(testVerification.getIsVerified());
        assertNotNull(testVerification.getVerifiedAt());

        verify(emailVerificationRepository).save(testVerification);
        verify(emailService).sendWelcomeEmail(testUser);
    }

    @Test
    void verifyEmail_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid-token-123";
        when(emailVerificationRepository.findByVerificationToken(invalidToken))
                .thenReturn(Optional.empty());

        // When
        boolean result = emailVerificationService.verifyEmail(invalidToken);

        // Then
        assertFalse(result);

        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
        verify(emailService, never()).sendWelcomeEmail(any(User.class));
    }

    @Test
    void verifyEmail_WithExpiredToken_ShouldReturnFalse() {
        // Given
        String expiredToken = "expired-token-123";
        EmailVerification expiredVerification = testVerification.toBuilder()
                .expiresAt(LocalDateTime.now().minusHours(1))
                .build();

        when(emailVerificationRepository.findByVerificationToken(expiredToken))
                .thenReturn(Optional.of(expiredVerification));

        // When
        boolean result = emailVerificationService.verifyEmail(expiredToken);

        // Then
        assertFalse(result);

        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
        verify(emailService, never()).sendWelcomeEmail(any(User.class));
    }

    @Test
    void verifyEmail_WithAlreadyVerifiedToken_ShouldReturnFalse() {
        // Given
        String verifiedToken = "verified-token-123";
        EmailVerification verifiedVerification = testVerification.toBuilder()
                .isVerified(true)
                .verifiedAt(LocalDateTime.now().minusMinutes(10))
                .build();

        when(emailVerificationRepository.findByVerificationToken(verifiedToken))
                .thenReturn(Optional.of(verifiedVerification));

        // When
        boolean result = emailVerificationService.verifyEmail(verifiedToken);

        // Then
        assertFalse(result);

        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
        verify(emailService, never()).sendWelcomeEmail(any(User.class));
    }

    @Test
    void hasPendingVerification_WithPendingVerification_ShouldReturnTrue() {
        // Given
        when(emailVerificationRepository.hasPendingVerification(eq(testUser), any(LocalDateTime.class)))
                .thenReturn(true);

        // When
        boolean result = emailVerificationService.hasPendingVerification(testUser);

        // Then
        assertTrue(result);
    }

    @Test
    void hasPendingVerification_WithNoPendingVerification_ShouldReturnFalse() {
        // Given
        when(emailVerificationRepository.hasPendingVerification(eq(testUser), any(LocalDateTime.class)))
                .thenReturn(false);

        // When
        boolean result = emailVerificationService.hasPendingVerification(testUser);

        // Then
        assertFalse(result);
    }
}

