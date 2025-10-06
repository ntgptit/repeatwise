package com.spacedlearning.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;

/**
 * Repository interface for User entity
 * Provides data access methods for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     * @param email user email
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email ignoring case
     * @param email user email
     * @return Optional containing user if found
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email
     * @param email user email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by email ignoring case
     * @param email user email
     * @return true if user exists
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find users by status
     * @param status user status
     * @return list of users with specified status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find users by status with pagination
     * @param status user status
     * @param pageable pagination information
     * @return page of users with specified status
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find users by preferred language
     * @param language preferred language
     * @return list of users with specified language
     */
    List<User> findByPreferredLanguage(User.PreferredLanguage language);

    /**
     * Find users by timezone
     * @param timezone user timezone
     * @return list of users with specified timezone
     */
    List<User> findByTimezone(String timezone);

    /**
     * Find active users
     * @return list of active users
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    /**
     * Find active users with pagination
     * @param pageable pagination information
     * @return page of active users
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    Page<User> findActiveUsers(Pageable pageable);

    /**
     * Find users created after specified date
     * @param date creation date
     * @return list of users created after date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findUsersCreatedAfter(@Param("date") java.time.LocalDateTime date);

    /**
     * Find users created between dates
     * @param startDate start date
     * @param endDate end date
     * @return list of users created between dates
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findUsersCreatedBetween(
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Count users by status
     * @param status user status
     * @return count of users with specified status
     */
    long countByStatus(UserStatus status);

    /**
     * Count active users
     * @return count of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    long countActiveUsers();

    /**
     * Find users with learning sets
     * @return list of users who have learning sets
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.learningSets ls WHERE ls IS NOT NULL")
    List<User> findUsersWithLearningSets();

    /**
     * Find users without learning sets
     * @return list of users who don't have learning sets
     */
    @Query("SELECT u FROM User u WHERE u.learningSets IS EMPTY")
    List<User> findUsersWithoutLearningSets();

    /**
     * Find users by full name containing text (case insensitive)
     * @param name name to search for
     * @return list of users with matching names
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByFullNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find users by full name containing text with pagination
     * @param name name to search for
     * @param pageable pagination information
     * @return page of users with matching names
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByFullNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find users with reminder schedules for today
     * @param today today's date
     * @return list of users with reminders today
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.reminderSchedules rs WHERE rs.scheduledDate = :today")
    List<User> findUsersWithRemindersToday(@Param("today") java.time.LocalDate today);

    /**
     * Find users with reminder schedules for specific date
     * @param date reminder date
     * @return list of users with reminders on date
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.reminderSchedules rs WHERE rs.scheduledDate = :date")
    List<User> findUsersWithRemindersOnDate(@Param("date") java.time.LocalDate date);
}