package com.spacedlearning.service.impl;

import com.spacedlearning.service.RateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of RateLimitService using in-memory storage
 * For production, consider using Redis or database-based rate limiting
 */
@Service
@Slf4j
public class RateLimitServiceImpl implements RateLimitService {

    private final ConcurrentHashMap<String, RateLimitInfo> registrationAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RateLimitInfo> loginAttempts = new ConcurrentHashMap<>();

    @Value("${app.rate-limit.registration.max-attempts:5}")
    private int maxRegistrationAttempts;

    @Value("${app.rate-limit.registration.window-hours:1}")
    private int registrationWindowHours;

    @Value("${app.rate-limit.login.max-attempts:10}")
    private int maxLoginAttempts;

    @Value("${app.rate-limit.login.window-hours:1}")
    private int loginWindowHours;

    @Override
    public boolean isRegistrationAllowed(String ipAddress) {
        RateLimitInfo info = registrationAttempts.get(ipAddress);
        if (info == null) {
            return true;
        }

        // Check if window has expired
        if (info.isExpired(registrationWindowHours)) {
            registrationAttempts.remove(ipAddress);
            return true;
        }

        return info.getAttempts() < maxRegistrationAttempts;
    }

    @Override
    public void recordRegistrationAttempt(String ipAddress) {
        RateLimitInfo info = registrationAttempts.computeIfAbsent(ipAddress, k -> new RateLimitInfo());
        info.incrementAttempts();
        log.debug("Recorded registration attempt for IP: {}, total attempts: {}", ipAddress, info.getAttempts());
    }

    @Override
    public boolean isLoginAllowed(String ipAddress) {
        RateLimitInfo info = loginAttempts.get(ipAddress);
        if (info == null) {
            return true;
        }

        // Check if window has expired
        if (info.isExpired(loginWindowHours)) {
            loginAttempts.remove(ipAddress);
            return true;
        }

        return info.getAttempts() < maxLoginAttempts;
    }

    @Override
    public void recordLoginAttempt(String ipAddress) {
        RateLimitInfo info = loginAttempts.computeIfAbsent(ipAddress, k -> new RateLimitInfo());
        info.incrementAttempts();
        log.debug("Recorded login attempt for IP: {}, total attempts: {}", ipAddress, info.getAttempts());
    }

    @Override
    public int getRemainingRegistrationAttempts(String ipAddress) {
        RateLimitInfo info = registrationAttempts.get(ipAddress);
        if (info == null || info.isExpired(registrationWindowHours)) {
            return maxRegistrationAttempts;
        }
        return Math.max(0, maxRegistrationAttempts - info.getAttempts());
    }

    @Override
    public int getRemainingLoginAttempts(String ipAddress) {
        RateLimitInfo info = loginAttempts.get(ipAddress);
        if (info == null || info.isExpired(loginWindowHours)) {
            return maxLoginAttempts;
        }
        return Math.max(0, maxLoginAttempts - info.getAttempts());
    }

    /**
     * Internal class to track rate limit information
     */
    private static class RateLimitInfo {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private final LocalDateTime firstAttempt = LocalDateTime.now();

        public void incrementAttempts() {
            attempts.incrementAndGet();
        }

        public int getAttempts() {
            return attempts.get();
        }

        public boolean isExpired(int windowHours) {
            return LocalDateTime.now().isAfter(firstAttempt.plusHours(windowHours));
        }
    }
}

