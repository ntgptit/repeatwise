package com.repeatwise.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repeatwise.config.properties.JwtProperties;
import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.AuthResponse;
import com.repeatwise.dto.response.auth.LoginResponse;
import com.repeatwise.dto.response.auth.RefreshTokenResponse;
import com.repeatwise.dto.response.user.UserResponse;
import com.repeatwise.entity.RefreshToken;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.UserStats;
import com.repeatwise.entity.enums.Language;
import com.repeatwise.entity.enums.Theme;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.repository.RefreshTokenRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.repository.UserStatsRepository;
import com.repeatwise.service.JwtService;
import com.repeatwise.service.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "Secret123!";
    private static final String HASHED_PASSWORD = "$2a$12$abcdef";

    @Mock
    private UserRepository userRepository;

    @Mock
    private SrsSettingsRepository srsSettingsRepository;

    @Mock
    private UserStatsRepository userStatsRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @org.junit.jupiter.api.DisplayName("Register user successfully when input valid")
    void should_RegisterUser_When_InputValid() {
        final var registerRequest = RegisterRequest.builder()
                .email("User@Example.com ")
                .username(" new_user ")
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .name("  Repeat Wise  ")
                .build();

        when(this.userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(this.userRepository.existsByUsername("new_user")).thenReturn(false);
        when(this.userRepository.save(any(User.class))).thenAnswer(invocation -> {
            final User toSave = invocation.getArgument(0);
            toSave.setId(USER_ID);
            return toSave;
        });

        try (MockedStatic<BCrypt> bcrypt = org.mockito.Mockito.mockStatic(BCrypt.class)) {

            bcrypt.when(() -> BCrypt.gensalt(12)).thenReturn("salt");
            bcrypt.when(() -> BCrypt.hashpw(PASSWORD, "salt")).thenReturn(HASHED_PASSWORD);

            final var userId = this.authService.register(registerRequest);

            assertThat(userId).isEqualTo(USER_ID);

            final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(this.userRepository).save(userCaptor.capture());
            final User savedUser = userCaptor.getValue();

            assertThat(savedUser.getEmail()).isEqualTo("user@example.com");
            assertThat(savedUser.getUsername()).isEqualTo("new_user");
            assertThat(savedUser.getPasswordHash()).isEqualTo(HASHED_PASSWORD);
            assertThat(savedUser.getName()).isEqualTo("Repeat Wise");
            assertThat(savedUser.getTimezone()).isEqualTo("Asia/Ho_Chi_Minh");
            assertThat(savedUser.getLanguage()).isEqualTo(Language.VI);
            assertThat(savedUser.getTheme()).isEqualTo(Theme.SYSTEM);

            final ArgumentCaptor<SrsSettings> srsCaptor = ArgumentCaptor.forClass(SrsSettings.class);
            verify(this.srsSettingsRepository).save(srsCaptor.capture());
            assertThat(srsCaptor.getValue().getUser()).isEqualTo(savedUser);

            final ArgumentCaptor<UserStats> statsCaptor = ArgumentCaptor.forClass(UserStats.class);
            verify(this.userStatsRepository).save(statsCaptor.capture());
            assertThat(statsCaptor.getValue().getUser()).isEqualTo(savedUser);
        }
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject registration when passwords do not match")
    void should_ThrowException_When_PasswordMismatch() {
        final var registerRequest = RegisterRequest.builder()
                .email("user@example.com")
                .password("password1")
                .confirmPassword("password2")
                .build();

        final var thrown = catchThrowable(() -> this.authService.register(registerRequest));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.PASSWORD_MISMATCH);

        verify(this.userRepository, never()).existsByEmailIgnoreCase(anyString());
        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject registration when email already exists")
    void should_ThrowException_When_EmailAlreadyExists() {
        final var registerRequest = RegisterRequest.builder()
                .email("existing@example.com")
                .username("newuser")
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .build();

        when(this.userRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

        final var thrown = catchThrowable(() -> this.authService.register(registerRequest));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.EMAIL_ALREADY_EXISTS);

        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject registration when username already exists")
    void should_ThrowException_When_UsernameAlreadyExists() {
        final var registerRequest = RegisterRequest.builder()
                .email("user@example.com")
                .username("duplicate")
                .password(PASSWORD)
                .confirmPassword(PASSWORD)
                .build();

        when(this.userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(this.userRepository.existsByUsername("duplicate")).thenReturn(true);

        final var thrown = catchThrowable(() -> this.authService.register(registerRequest));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.USERNAME_ALREADY_EXISTS);

        verify(this.userRepository, never()).save(any(User.class));
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Login successfully with valid credentials")
    void should_Login_When_CredentialsValid() {
        final var loginRequest = LoginRequest.builder()
                .identifier("user@example.com")
                .password(PASSWORD)
                .build();

        final var user = createUser();
        when(this.userRepository.findByUsernameOrEmail("user@example.com")).thenReturn(Optional.of(user));
        when(this.jwtProperties.getAccessTokenExpirationMinutes()).thenReturn(15);
        when(this.jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(this.refreshTokenService.createRefreshToken(user, null, null, null)).thenReturn("refresh-token");
        final var userResponse = UserResponse.builder().id(USER_ID).email(user.getEmail()).build();
        when(this.userMapper.toUserResponse(user)).thenReturn(userResponse);

        try (MockedStatic<BCrypt> bcrypt = org.mockito.Mockito.mockStatic(BCrypt.class)) {
            bcrypt.when(() -> BCrypt.checkpw(PASSWORD, HASHED_PASSWORD)).thenReturn(true);

            final LoginResponse response = this.authService.login(loginRequest);

            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            final AuthResponse authResponse = response.getAuthResponse();
            assertThat(authResponse.getAccessToken()).isEqualTo("access-token");
            assertThat(authResponse.getExpiresIn()).isEqualTo(900);
            assertThat(authResponse.getUser()).isEqualTo(userResponse);
        }
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject login when user not found")
    void should_ThrowException_When_LoginUserNotFound() {
        final var loginRequest = LoginRequest.builder()
                .identifier("missing")
                .password(PASSWORD)
                .build();

        when(this.userRepository.findByUsernameOrEmail("missing")).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.authService.login(loginRequest));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.USER_NOT_FOUND);

        verify(this.jwtService, never()).generateAccessToken(any());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject login when password invalid")
    void should_ThrowException_When_PasswordInvalid() {
        final var loginRequest = LoginRequest.builder()
                .identifier("user@example.com")
                .password("wrong")
                .build();

        final var user = createUser();
        when(this.userRepository.findByUsernameOrEmail("user@example.com")).thenReturn(Optional.of(user));

        try (MockedStatic<BCrypt> bcrypt = org.mockito.Mockito.mockStatic(BCrypt.class)) {
            bcrypt.when(() -> BCrypt.checkpw("wrong", HASHED_PASSWORD)).thenReturn(false);

            final var thrown = catchThrowable(() -> this.authService.login(loginRequest));

            assertThat(thrown)
                    .isInstanceOf(RepeatWiseException.class)
                    .extracting("error")
                    .isEqualTo(RepeatWiseError.INVALID_CREDENTIALS);
        }

        verify(this.jwtService, never()).generateAccessToken(any());
        verify(this.refreshTokenService, never()).createRefreshToken(any(), any(), any(), any());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Logout revokes all user refresh tokens")
    void should_Logout_When_UserExists() {
        final var user = createUser();
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        this.authService.logout(USER_ID);

        verify(this.refreshTokenRepository).revokeAllTokensByUser(eq(user), any(LocalDateTime.class));
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Reject logout when user not found")
    void should_ThrowException_When_LogoutUserMissing() {
        when(this.userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        final var thrown = catchThrowable(() -> this.authService.logout(USER_ID));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.USER_NOT_FOUND);

        verify(this.refreshTokenRepository, never()).revokeAllTokensByUser(any(), any());
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Refresh access token successfully with valid refresh token")
    void should_RefreshAccessToken_When_TokenValid() {
        final var newRefreshToken = "new-refresh-token";
        final var refreshTokenEntity = RefreshToken.builder()
                .token(newRefreshToken)
                .user(createUser())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();

        when(this.refreshTokenService.validateAndRotateRefreshToken("old-token", null, "device", "127.0.0.1"))
                .thenReturn(newRefreshToken);
        when(this.refreshTokenService.validateRefreshToken(newRefreshToken)).thenReturn(refreshTokenEntity);
        when(this.jwtService.generateAccessToken(refreshTokenEntity.getUser())).thenReturn("new-access-token");
        when(this.jwtProperties.getAccessTokenExpirationMinutes()).thenReturn(20);

        final RefreshTokenResponse response = this.authService.refreshAccessToken("old-token", null, "device",
                "127.0.0.1");

        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getExpiresIn()).isEqualTo(1200);
        verify(this.refreshTokenService).validateRefreshToken(newRefreshToken);
    }

    @Test
    @org.junit.jupiter.api.DisplayName("Propagate exception when refresh token invalid")
    void should_ThrowException_When_RefreshTokenInvalid() {
        when(this.refreshTokenService.validateAndRotateRefreshToken("bad-token", null, null, null))
                .thenThrow(new RepeatWiseException(RepeatWiseError.INVALID_TOKEN));

        final var thrown = catchThrowable(() -> this.authService.refreshAccessToken("bad-token", null, null, null));

        assertThat(thrown)
                .isInstanceOf(RepeatWiseException.class)
                .extracting("error")
                .isEqualTo(RepeatWiseError.INVALID_TOKEN);

        verify(this.refreshTokenService, never()).validateRefreshToken(anyString());
        verify(this.jwtService, never()).generateAccessToken(any());
    }

    private static User createUser() {
        final var user = User.builder()
                .email("user@example.com")
                .username("new_user")
                .passwordHash(HASHED_PASSWORD)
                .name("Repeat Wise")
                .timezone("Asia/Ho_Chi_Minh")
                .language(Language.VI)
                .theme(Theme.SYSTEM)
                .build();
        user.setId(USER_ID);
        return user;
    }
}


