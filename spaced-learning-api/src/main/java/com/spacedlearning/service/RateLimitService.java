package com.spacedlearning.service;

/**
 * Service interface for rate limiting operations
 * Handles rate limiting for various operations like registration, login, etc.
 */
public interface RateLimitService {

    /**
     * Check if registration is allowed for the given IP address
     * @param ipAddress client IP address
     * @return true if registration is allowed, false if rate limited
     */
    boolean isRegistrationAllowed(String ipAddress);

    /**
     * Record a registration attempt for the given IP address
     * @param ipAddress client IP address
     */
    void recordRegistrationAttempt(String ipAddress);

    /**
     * Check if login is allowed for the given IP address
     * @param ipAddress client IP address
     * @return true if login is allowed, false if rate limited
     */
    boolean isLoginAllowed(String ipAddress);

    /**
     * Record a login attempt for the given IP address
     * @param ipAddress client IP address
     */
    void recordLoginAttempt(String ipAddress);

    /**
     * Get remaining attempts for registration for the given IP address
     * @param ipAddress client IP address
     * @return number of remaining attempts
     */
    int getRemainingRegistrationAttempts(String ipAddress);

    /**
     * Get remaining attempts for login for the given IP address
     * @param ipAddress client IP address
     * @return number of remaining attempts
     */
    int getRemainingLoginAttempts(String ipAddress);
}

