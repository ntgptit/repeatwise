package com.repeatwise.service;

import com.repeatwise.dto.AuthResponseDto;
import com.repeatwise.dto.UserDto;
import com.repeatwise.exception.AuthenticationException;
import com.repeatwise.exception.UserNotFoundException;
import com.repeatwise.model.User;
import com.repeatwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto login(String emailOrUsername, String password) {
        // Try to find user by email first, then by username
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                        .orElseThrow(() -> new AuthenticationException("Invalid email/username or password")));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid email/username or password");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserDto.fromUser(user))
                .message("Login successful")
                .success(true)
                .build();
    }

    @Override
    public AuthResponseDto register(String name, String username, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("Email already exists");
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new AuthenticationException("Username already exists");
        }

        User user = User.builder()
                .name(name)
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();


        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponseDto.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(UserDto.fromUser(savedUser))
                .message("Registration successful")
                .success(true)
                .build();
    }

    @Override
    public void logout(String token) {
        // In a real application, you might want to blacklist the token
        // For now, we'll just validate it to ensure it's a valid token
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            jwtService.validateToken(actualToken);
        }
    }

    @Override
    public UserDto getCurrentUser(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AuthenticationException("Invalid token");
        }

        String actualToken = token.substring(7);
        String email = jwtService.extractEmail(actualToken);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserDto.fromUser(user);
    }
}
