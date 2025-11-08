package com.repeatwise.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repeatwise.dto.request.user.ChangePasswordRequest;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String CURRENT_PASSWORD = "OldPassword123";
    private static final String NEW_PASSWORD = "NewPassword456";

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setPasswordHash(BCrypt.hashpw(CURRENT_PASSWORD, BCrypt.gensalt(4)));
    }

    @Test
    void changePassword_shouldUpdatePasswordAndRevokeTokens_whenRequestValid() {
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        final var request = ChangePasswordRequest.builder()
                .currentPassword(CURRENT_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .confirmNewPassword(NEW_PASSWORD)
                .build();

        userService.changePassword(userId, request);

        verify(userRepository).save(argThat(savedUser ->
                BCrypt.checkpw(NEW_PASSWORD, savedUser.getPasswordHash())));
        verify(refreshTokenRepository).revokeAllTokensByUser(eq(user), any(LocalDateTime.class));
    }

    @Test
    void changePassword_shouldThrowPasswordMismatchException_whenNewPasswordsDoNotMatch() {
        final var request = ChangePasswordRequest.builder()
                .currentPassword(CURRENT_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .confirmNewPassword("Mismatch123")
                .build();

        final var exception = assertThrows(RepeatWiseException.class,
                () -> userService.changePassword(userId, request));
        assertEquals(RepeatWiseError.PASSWORD_MISMATCH, exception.getError());

        verify(userRepository, never()).findById(any());
        verify(refreshTokenRepository, never()).revokeAllTokensByUser(any(), any());
    }

    @Test
    void changePassword_shouldThrowUserNotFoundException_whenUserMissing() {
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        final var request = ChangePasswordRequest.builder()
                .currentPassword(CURRENT_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .confirmNewPassword(NEW_PASSWORD)
                .build();

        final var exception = assertThrows(RepeatWiseException.class,
                () -> userService.changePassword(userId, request));
        assertEquals(RepeatWiseError.USER_NOT_FOUND, exception.getError());

        verify(userRepository).findById(userId);
        verify(refreshTokenRepository, never()).revokeAllTokensByUser(any(), any());
    }

    @Test
    void changePassword_shouldThrowIncorrectPasswordException_whenCurrentPasswordWrong() {
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        final var request = ChangePasswordRequest.builder()
                .currentPassword("WrongPassword789")
                .newPassword(NEW_PASSWORD)
                .confirmNewPassword(NEW_PASSWORD)
                .build();

        final var exception = assertThrows(RepeatWiseException.class,
                () -> userService.changePassword(userId, request));
        assertEquals(RepeatWiseError.INCORRECT_CURRENT_PASSWORD, exception.getError());

        verify(refreshTokenRepository, never()).revokeAllTokensByUser(any(), any());
    }

    @Test
    void changePassword_shouldThrowSamePasswordException_whenNewPasswordMatchesCurrent() {
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        final var request = ChangePasswordRequest.builder()
                .currentPassword(CURRENT_PASSWORD)
                .newPassword(CURRENT_PASSWORD)
                .confirmNewPassword(CURRENT_PASSWORD)
                .build();

        final var exception = assertThrows(RepeatWiseException.class,
                () -> userService.changePassword(userId, request));
        assertEquals(RepeatWiseError.SAME_PASSWORD, exception.getError());

        verify(refreshTokenRepository, never()).revokeAllTokensByUser(any(), any());
    }
}

