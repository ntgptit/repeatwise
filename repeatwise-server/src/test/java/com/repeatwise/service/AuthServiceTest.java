package com.repeatwise.service;

import com.repeatwise.dto.AuthRequestDto;
import com.repeatwise.dto.AuthResponseDto;
import com.repeatwise.dto.UserDto;
import com.repeatwise.exception.AuthenticationException;
import com.repeatwise.model.User;
import com.repeatwise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private AuthRequestDto loginRequest;
    private AuthRequestDto registerRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        loginRequest = new AuthRequestDto();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new AuthRequestDto();
        registerRequest.setName("New User");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnAuthResponse() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");

        // Act
        AuthResponseDto response = authService.login("test@example.com", "password123");

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Login successful", response.getMessage());
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals(testUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void login_WithInvalidEmail_ShouldThrowAuthenticationException() {
        // Arrange
        when(userRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("invalid@example.com", "password123");
        });
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowAuthenticationException() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login("test@example.com", "wrongpassword");
        });
    }

    @Test
    void register_WithValidData_ShouldReturnAuthResponse() {
        // Arrange
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

        // Act
        AuthResponseDto response = authService.register("New User", "new@example.com", "password123");

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("Registration successful", response.getMessage());
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        assertNotNull(response.getUser());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowAuthenticationException() {
        // Arrange
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.register("New User", "existing@example.com", "password123");
        });
    }

    @Test
    void getCurrentUser_WithValidToken_ShouldReturnUserDto() {
        // Arrange
        String token = "Bearer valid-token";
        when(jwtService.extractEmail("valid-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        UserDto userDto = authService.getCurrentUser(token);

        // Assert
        assertNotNull(userDto);
        assertEquals(testUser.getEmail(), userDto.getEmail());
        assertEquals(testUser.getName(), userDto.getName());
    }

    @Test
    void getCurrentUser_WithInvalidToken_ShouldThrowAuthenticationException() {
        // Arrange
        String token = "Invalid-token";

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.getCurrentUser(token);
        });
    }
}
