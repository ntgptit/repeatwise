package com.spacedlearning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.service.AuditService;
import com.spacedlearning.service.AuthService;
import com.spacedlearning.service.RateLimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController registration functionality
 * Tests UC-001: User Registration use case
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RateLimitService rateLimitService;

    @MockBean
    private AuditService auditService;

    private RegisterRequest validRegisterRequest;

    @BeforeEach
    void setUp() {
        validRegisterRequest = RegisterRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .confirmPassword("Password123!")
                .fullName("Test User")
                .preferredLanguage(User.PreferredLanguage.VI)
                .timezone("Asia/Ho_Chi_Minh")
                .defaultReminderTime(LocalTime.of(9, 0))
                .build();
    }

    @Test
    void register_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Given
        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .build();

        when(rateLimitService.isRegistrationAllowed(anyString())).thenReturn(true);
        when(authService.register(any(RegisterRequest.class))).thenReturn(null); // Mock UserResponse

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isCreated());

        verify(rateLimitService).isRegistrationAllowed(anyString());
        verify(authService).register(any(RegisterRequest.class));
        verify(rateLimitService).recordRegistrationAttempt(anyString());
        verify(auditService).logRegistrationAttempt(eq("test@example.com"), anyString(), eq(true), isNull());
    }

    @Test
    void register_WithRateLimitExceeded_ShouldReturnTooManyRequests() throws Exception {
        // Given
        when(rateLimitService.isRegistrationAllowed(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isTooManyRequests());

        verify(rateLimitService).isRegistrationAllowed(anyString());
        verify(authService, never()).register(any(RegisterRequest.class));
        verify(rateLimitService, never()).recordRegistrationAttempt(anyString());
        verify(auditService, never()).logRegistrationAttempt(anyString(), anyString(), anyBoolean(), anyString());
    }

    @Test
    void register_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = validRegisterRequest.toBuilder()
                .email("invalid-email")
                .build();

        when(rateLimitService.isRegistrationAllowed(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(rateLimitService).isRegistrationAllowed(anyString());
        verify(authService, never()).register(any(RegisterRequest.class));
        verify(rateLimitService).recordRegistrationAttempt(anyString());
        verify(auditService).logRegistrationAttempt(eq("invalid-email"), anyString(), eq(false), anyString());
    }

    @Test
    void register_WithPasswordMismatch_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest passwordMismatchRequest = validRegisterRequest.toBuilder()
                .confirmPassword("DifferentPassword123!")
                .build();

        when(rateLimitService.isRegistrationAllowed(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordMismatchRequest)))
                .andExpect(status().isBadRequest());

        verify(rateLimitService).isRegistrationAllowed(anyString());
        verify(authService, never()).register(any(RegisterRequest.class));
        verify(rateLimitService).recordRegistrationAttempt(anyString());
        verify(auditService).logRegistrationAttempt(eq("test@example.com"), anyString(), eq(false), anyString());
    }

    @Test
    void register_WithWeakPassword_ShouldReturnBadRequest() throws Exception {
        // Given
        RegisterRequest weakPasswordRequest = validRegisterRequest.toBuilder()
                .password("weak")
                .confirmPassword("weak")
                .build();

        when(rateLimitService.isRegistrationAllowed(anyString())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weakPasswordRequest)))
                .andExpect(status().isBadRequest());

        verify(rateLimitService).isRegistrationAllowed(anyString());
        verify(authService, never()).register(any(RegisterRequest.class));
        verify(rateLimitService).recordRegistrationAttempt(anyString());
        verify(auditService).logRegistrationAttempt(eq("test@example.com"), anyString(), eq(false), anyString());
    }
}

