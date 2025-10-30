package com.repeatwise.log;

/**
 * Centralized log event codes for structured, greppable logging.
 * Keep messages inline; use this enum only to tag events consistently.
 * Each event can have associated metadata for better log analysis.
 */
public enum LogEvent {
    // General
    START,
    SUCCESS,
    FAIL,

    // User
    USER_GET_PROFILE,
    USER_UPDATE_PROFILE,
    USER_CHANGE_PASSWORD,
    USER_NOT_FOUND,

    // Auth
    AUTH_REGISTER_START,
    AUTH_REGISTER_SUCCESS,
    AUTH_LOGIN_START,
    AUTH_LOGIN_SUCCESS,
    AUTH_TOKEN_REFRESH,
    AUTH_TOKEN_REVOKE,

    // Folder lifecycle
    FOLDER_CREATE_START,
    FOLDER_CREATE_SUCCESS,
    FOLDER_NAME_REQUIRED,
    FOLDER_NAME_EXISTS,
    FOLDER_GET_PARENT_NOT_FOUND,
    FOLDER_GET_NOT_FOUND_OR_UNAUTHORIZED,
    FOLDER_MAX_DEPTH_EXCEEDED,
    FOLDER_UPDATE_START,
    FOLDER_UPDATE_SUCCESS,
    FOLDER_MOVE_START,
    FOLDER_MOVE_SUCCESS,
    FOLDER_MOVE_RENAME,
    FOLDER_MOVE_DESCENDANTS_UPDATED,
    FOLDER_COPY_START,
    FOLDER_COPY_SUCCESS,
    FOLDER_COPY_DEPTH_VALID,
    FOLDER_COPY_DEPTH_EXCEEDED,
    FOLDER_SOFT_DELETE_START,
    FOLDER_SOFT_DELETE_SUCCESS,
    FOLDER_RESTORE_START,
    FOLDER_RESTORE_SUCCESS,
    FOLDER_HARD_DELETE_START,
    FOLDER_HARD_DELETE_SUCCESS,
    FOLDER_HARD_DELETE_DESCENDANTS,
    FOLDER_HARD_DELETE_DESCENDANTS_DONE,
    FOLDER_STATS_GET_START,
    FOLDER_STATS_CACHE_HIT,
    FOLDER_STATS_CALCULATED,
    FOLDER_STATS_INVALIDATE_START,
    FOLDER_STATS_INVALIDATE_SUCCESS,

    // Deck
    DECK_CREATE_START,
    DECK_CREATE_SUCCESS,
    DECK_MOVE_START,
    DECK_MOVE_SUCCESS,
    DECK_COPY_START,
    DECK_COPY_SUCCESS,

    // Notifications
    NOTIF_SETTINGS_GET,
    NOTIF_SETTINGS_CREATE_DEFAULT,
    NOTIF_SETTINGS_UPDATE,
    NOTIF_TEST_SEND,
    NOTIF_SCHEDULE_START,
    NOTIF_SCHEDULE_DONE,
    NOTIF_SCHEDULE_ERROR,
    NOTIF_RETRY_START,
    NOTIF_RETRY_DONE,
    NOTIF_RETRY_ERROR,
    NOTIF_CLEANUP_START,
    NOTIF_CLEANUP_DONE,
    NOTIF_CLEANUP_ERROR,

    // Exceptions
    EX_RESOURCE_NOT_FOUND,
    EX_VALIDATION,
    EX_DUPLICATE_RESOURCE,
    EX_INVALID_CREDENTIALS,
    EX_INVALID_TOKEN,
    EX_FORBIDDEN,
    EX_ILLEGAL_ARGUMENT,
    EX_INTERNAL_SERVER;

    /**
     * Get a descriptive category for this log event.
     * Useful for log aggregation and filtering.
     */
    public String getCategory() {
        String name = this.name();
        if (name.startsWith("USER_")) return "USER";
        if (name.startsWith("AUTH_")) return "AUTH";
        if (name.startsWith("FOLDER_")) return "FOLDER";
        if (name.startsWith("DECK_")) return "DECK";
        if (name.startsWith("NOTIF_")) return "NOTIFICATION";
        if (name.startsWith("EX_")) return "EXCEPTION";
        return "GENERAL";
    }

    /**
     * Get severity level based on event type.
     */
    public LogLevel getSuggestedLevel() {
        String name = this.name();
        if (name.startsWith("EX_") || name.contains("ERROR")) {
            return LogLevel.ERROR;
        }
        if (name.contains("FAIL") || name.contains("NOT_FOUND")) {
            return LogLevel.WARN;
        }
        if (name.contains("START") || name.contains("DEBUG")) {
            return LogLevel.DEBUG;
        }
        return LogLevel.INFO;
    }

    /**
     * Check if this event should be logged at high priority.
     */
    public boolean isHighPriority() {
        return name().startsWith("EX_") ||
               name().contains("ERROR") ||
               name().contains("FAIL");
    }
}

