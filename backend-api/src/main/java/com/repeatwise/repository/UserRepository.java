package com.repeatwise.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.User;

/**
 * Repository for User entity with Spring Data JPA.
 * Provides database access methods for user management and authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive).
     * Used for login and email uniqueness validation.
     *
     * @param email User's email address
     * @return Optional containing user if found
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Find user by username (case-sensitive).
     * Used for login and username uniqueness validation.
     *
     * @param username User's username
     * @return Optional containing user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by identifier (username or email).
     * Used for flexible login (user can login with either username or email).
     *
     * @param identifier Username or email
     * @return Optional containing user if found
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR LOWER(u.email) = LOWER(:identifier)")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Check if email already exists in database (case-insensitive).
     * Used for validation during registration.
     *
     * @param email Email to check
     * @return true if email exists
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Check if username already exists in database (case-sensitive).
     * Used for validation during registration.
     *
     * @param username Username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);
}
