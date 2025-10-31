package com.repeatwise.repository;

import com.repeatwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * User Repository
 *
 * Requirements:
 * - UC-001: User Registration
 * - UC-002: User Login
 * - Database access for users table
 *
 * @author RepeatWise Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username (case-sensitive)
     *
     * UC-002: Username is case-sensitive for login
     * - Case-sensitive comparison
     *
     * @param username User username
     * @return Optional of User
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by username (case-insensitive)
     *
     * Business Rule BR-001: Username uniqueness
     * - Case-insensitive comparison
     *
     * @param username User username
     * @return Optional of User
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by email (case-insensitive)
     *
     * Business Rule BR-002: Email uniqueness
     * - Case-insensitive comparison
     *
     * @param email User email
     * @return Optional of User
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if username exists (case-insensitive)
     *
     * Business Rule BR-001: Username uniqueness
     *
     * @param username User username
     * @return true if username exists
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Check if email exists (case-insensitive)
     *
     * Business Rule BR-002: Email uniqueness
     *
     * @param email User email
     * @return true if email exists
     */
    boolean existsByEmailIgnoreCase(String email);
}
