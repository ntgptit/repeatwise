package com.repeatwise.security;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.repeatwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom UserDetailsService implementation.
 * Loads user from database by user ID (extracted from JWT token).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username (in our case, username is user ID as string).
     * This method is called by Spring Security to load user details.
     *
     * @param username User ID as string (UUID)
     * @return UserDetails (User entity implements UserDetails)
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", username);

        try {
            // Parse user ID from string
            final var userId = UUID.fromString(username);

            // Find user by ID
            return userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", userId);
                        return new UsernameNotFoundException("User not found with ID: " + userId);
                    });
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user ID format: {}", username);
            throw new UsernameNotFoundException("Invalid user ID format: " + username);
        }
    }
}
