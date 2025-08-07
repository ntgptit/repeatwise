package com.repeatwise.service.impl;

import com.repeatwise.dto.UserDto;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.mapper.UserMapper;
import com.repeatwise.model.User;
import com.repeatwise.repository.SetCycleRepository;
import com.repeatwise.repository.SetRepository;
import com.repeatwise.repository.SetReviewRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.UserService;
import com.repeatwise.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SetRepository setRepository;
    private final SetCycleRepository setCycleRepository;
    private final SetReviewRepository setReviewRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating new user with email: {}", userDto.getEmail());
        
        // Validate email uniqueness
        if (existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }
        
        // Validate username uniqueness
        if (userDto.getUsername() != null && existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    public Optional<UserDto> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmailAndNotDeleted(email)
                .map(userMapper::toDto);
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsernameAndNotDeleted(username)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto updateUser(UUID id, UserDto userDto) {
        ServiceUtils.logOperationStart("user update", id);
        
        User existingUser = ServiceUtils.findEntityOrThrow(
                () -> userRepository.findById(id), 
                "User", 
                id
        );
        
        // Check email uniqueness if email is being changed
        if (!existingUser.getEmail().equalsIgnoreCase(userDto.getEmail()) && 
            existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }
        
        // Check username uniqueness if username is being changed
        if (userDto.getUsername() != null && 
            !userDto.getUsername().equals(existingUser.getUsername()) && 
            existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        
        // Update fields
        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        
        User updatedUser = userRepository.save(existingUser);
        ServiceUtils.logOperationSuccess("user update", updatedUser.getId());
        
        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        ServiceUtils.logOperationStart("user deletion", id);
        
        User user = ServiceUtils.findEntityOrThrow(
                () -> userRepository.findById(id), 
                "User", 
                id
        );
        
        userRepository.delete(user);
        ServiceUtils.logOperationSuccess("user deletion", id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public List<UserDto> findAllActive() {
        log.debug("Finding all active users");
        return userRepository.findAllActive()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserStatistics getUserStatistics(UUID userId) {
        log.debug("Getting statistics for user ID: {}", userId);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
        // Count sets
        long totalSets = setRepository.countByUserId(userId);
        long activeSets = setRepository.findActiveSetsByUserId(userId).size();
        long masteredSets = setRepository.findMasteredSetsByUserId(userId).size();
        
        // Count cycles
        long totalCycles = setCycleRepository.findCyclesByUserId(userId).size();
        
        // Count reviews
        long totalReviews = setReviewRepository.findReviewsByUserId(userId).size();
        
        // Calculate average score
        double avgScore = setReviewRepository.findReviewsByUserId(userId)
                .stream()
                .mapToInt(review -> review.getScore())
                .average()
                .orElse(0.0);
        
        return new UserStatistics(
                totalSets,
                activeSets,
                masteredSets,
                totalCycles,
                totalReviews,
                avgScore
        );
    }
} 
