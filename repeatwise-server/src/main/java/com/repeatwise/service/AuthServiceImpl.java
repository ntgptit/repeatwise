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
    public AuthResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
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
    public AuthResponseDto register(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthenticationException("Email already exists");
        }

        String username = email.split("@")[0];
        // Check if username already exists, if so, append a number
        int counter = 1;
        String finalUsername = username;
        while (userRepository.existsByUsernameIgnoreCase(finalUsername)) {
            finalUsername = username + counter;
            counter++;
        }

        User user = User.builder()
                .name(name)
                .username(finalUsername)
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
