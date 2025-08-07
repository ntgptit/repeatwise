package com.repeatwise.util;

public final class Constants {
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
    
    // API Constants
    public static final String API_BASE_PATH = "/api/v1";
    public static final String AUTH_BASE_PATH = "/api/v1/auth";
    public static final String PUBLIC_BASE_PATH = "/api/v1/public";
    
    // Security Constants
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    public static final String JWT_SECRET_KEY = "jwt.secret";
    public static final String JWT_EXPIRATION_KEY = "jwt.expiration";
    public static final String JWT_REFRESH_EXPIRATION_KEY = "jwt.refresh-expiration";
    
    // Pagination Constants
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
    
    // Cache Constants
    public static final String CACHE_USERS = "users";
    public static final String CACHE_SETS = "sets";
    public static final String CACHE_CYCLES = "cycles";
    
    // Validation Constants
    public static final int MAX_USERNAME_LENGTH = 64;
    public static final int MAX_EMAIL_LENGTH = 128;
    public static final int MAX_SET_NAME_LENGTH = 128;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;
    
    // Business Logic Constants
    public static final int MIN_WORD_COUNT = 1;
    public static final int MAX_WORD_COUNT = 10000;
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 100;
    public static final int MIN_CYCLE_NUMBER = 1;
    public static final int MAX_REVIEW_NUMBER = 5;
    
    // Status Constants
    public static final String STATUS_NOT_STARTED = "not_started";
    public static final String STATUS_LEARNING = "learning";
    public static final String STATUS_REVIEWING = "reviewing";
    public static final String STATUS_MASTERED = "mastered";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_FINISHED = "finished";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_SENT = "sent";
    public static final String STATUS_SKIPPED = "skipped";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_RESCHEDULED = "rescheduled";
    public static final String STATUS_CANCELLED = "cancelled";
} 