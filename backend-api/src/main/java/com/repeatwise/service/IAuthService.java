package com.repeatwise.service;

import com.repeatwise.dto.request.auth.LoginRequest;
import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.UserResponse;

/**
 * Authentication Service Interface
 *
 * Requirements:
 * - UC-001: User Registration
 * - UC-002: User Login
 * - UC-003: User Logout
 *
 * @author RepeatWise Team
 */
public interface IAuthService {

    /**
     * Register new user account
     *
     * Business Logic:
     * 1. Validate username uniqueness (case-insensitive)
     * 2. Validate email uniqueness (case-insensitive)
     * 3. Hash password with bcrypt (cost 12)
     * 4. Create user with default settings
     * 5. Create default SRS settings
     * 6. Create default user stats
     * 7. Return user response
     *
     * @param request RegisterRequest containing username, email, password, name
     * @return UserResponse with user details
     * @throws DuplicateUsernameException if username already exists
     * @throws DuplicateEmailException if email already exists
     */
    UserResponse register(RegisterRequest request);

    /**
     * Login user with username or email
     *
     * Business Logic:
     * 1. Detect if input is email (contains @) or username
     * 2. Find user by username or email (case-insensitive)
     * 3. Verify password with bcrypt
     * 4. Return user response
     *
     * @param request LoginRequest containing usernameOrEmail and password
     * @return UserResponse with user details
     * @throws InvalidCredentialsException if username/email or password is invalid
     */
    UserResponse login(LoginRequest request);
}
