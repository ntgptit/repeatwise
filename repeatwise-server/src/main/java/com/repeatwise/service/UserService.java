package com.repeatwise.service;

import com.repeatwise.dto.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    /**
     * Create a new user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Find user by ID
     */
    Optional<UserDto> findById(UUID id);

    /**
     * Find user by email
     */
    Optional<UserDto> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<UserDto> findByUsername(String username);

    /**
     * Update user profile
     */
    UserDto updateUser(UUID id, UserDto userDto);

    /**
     * Delete user (soft delete)
     */
    void deleteUser(UUID id);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Find all active users
     */
    List<UserDto> findAllActive();

    /**
     * Get user statistics
     */
    UserStatistics getUserStatistics(UUID userId);

    /**
     * User statistics data class
     */
    record UserStatistics(
        long totalSets,
        long activeSets,
        long masteredSets,
        long totalCycles,
        long totalReviews,
        double averageScore
    ) {}
} 
