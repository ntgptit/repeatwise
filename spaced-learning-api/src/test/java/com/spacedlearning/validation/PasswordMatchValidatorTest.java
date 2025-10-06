package com.spacedlearning.validation;

import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.entity.User;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for PasswordMatchValidator
 * Tests password confirmation validation for UC-001: User Registration
 */
@ExtendWith(MockitoExtension.class)
class PasswordMatchValidatorTest {

    private PasswordMatchValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchValidator();
        validator.initialize(mock(ValidPasswordMatch.class));
    }

    @Test
    void isValid_WithMatchingPasswords_ShouldReturnTrue() {
        // Given
        RegisterRequest request = createValidRegisterRequest("Password123!", "Password123!");

        // When
        boolean result = validator.isValid(request, context);

        // Then
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValid_WithNonMatchingPasswords_ShouldReturnFalse() {
        // Given
        RegisterRequest request = createValidRegisterRequest("Password123!", "DifferentPassword123!");
        when(context.disableDefaultConstraintViolation()).thenReturn(context);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class));

        // When
        boolean result = validator.isValid(request, context);

        // Then
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Password and confirm password do not match");
    }

    @Test
    void isValid_WithNullRequest_ShouldReturnTrue() {
        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValid_WithNullPassword_ShouldReturnTrue() {
        // Given
        RegisterRequest request = createValidRegisterRequest(null, "Password123!");

        // When
        boolean result = validator.isValid(request, context);

        // Then
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValid_WithNullConfirmPassword_ShouldReturnTrue() {
        // Given
        RegisterRequest request = createValidRegisterRequest("Password123!", null);

        // When
        boolean result = validator.isValid(request, context);

        // Then
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void isValid_WithEmptyPasswords_ShouldReturnTrue() {
        // Given
        RegisterRequest request = createValidRegisterRequest("", "");

        // When
        boolean result = validator.isValid(request, context);

        // Then
        assertTrue(result);
        verify(context, never()).disableDefaultConstraintViolation();
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    private RegisterRequest createValidRegisterRequest(String password, String confirmPassword) {
        return RegisterRequest.builder()
                .email("test@example.com")
                .password(password)
                .confirmPassword(confirmPassword)
                .fullName("Test User")
                .preferredLanguage(User.PreferredLanguage.VI)
                .timezone("Asia/Ho_Chi_Minh")
                .defaultReminderTime(LocalTime.of(9, 0))
                .build();
    }
}

