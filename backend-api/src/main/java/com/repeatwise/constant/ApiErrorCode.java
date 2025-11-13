package com.repeatwise.constant;

/**
 * Defines API-level error codes returned in error responses.
 */
public final class ApiErrorCode {

    private ApiErrorCode() {
        // Utility class
    }

    public static final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String REFRESH_TOKEN_NOT_FOUND = "REFRESH_TOKEN_NOT_FOUND";
    public static final String REFRESH_TOKEN_EXPIRED = "REFRESH_TOKEN_EXPIRED";
    public static final String REFRESH_TOKEN_REVOKED = "REFRESH_TOKEN_REVOKED";
    public static final String REFRESH_TOKEN_MISSING = "REFRESH_TOKEN_MISSING";
    public static final String TOKEN_REUSE_DETECTED = "TOKEN_REUSE_DETECTED";
    public static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String TYPE_MISMATCH = "TYPE_MISMATCH";
    public static final String ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";
    public static final String ILLEGAL_ARGUMENT = "ILLEGAL_ARGUMENT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    public static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    public static final String USERNAME_ALREADY_EXISTS = "USERNAME_ALREADY_EXISTS";
    public static final String INCORRECT_PASSWORD = "INCORRECT_PASSWORD";
    public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";

    public static final String FOLDER_NOT_FOUND = "FOLDER_NOT_FOUND";
    public static final String FOLDER_NAME_ALREADY_EXISTS = "FOLDER_NAME_ALREADY_EXISTS";
    public static final String MAX_FOLDER_DEPTH_EXCEEDED = "MAX_FOLDER_DEPTH_EXCEEDED";
    public static final String CIRCULAR_FOLDER_REFERENCE = "CIRCULAR_FOLDER_REFERENCE";
    public static final String FOLDER_TOO_LARGE = "FOLDER_TOO_LARGE";

    public static final String DECK_NAME_REQUIRED = "DECK_NAME_REQUIRED";
    public static final String DECK_NOT_FOUND = "DECK_NOT_FOUND";
    public static final String DECK_NAME_ALREADY_EXISTS = "DECK_NAME_ALREADY_EXISTS";
    public static final String DECK_ALREADY_IN_LOCATION = "DECK_ALREADY_IN_LOCATION";
    public static final String DECK_TOO_LARGE = "DECK_TOO_LARGE";

    public static final String CARD_NOT_FOUND = "CARD_NOT_FOUND";
    public static final String CARD_NOT_DUE_FOR_REVIEW = "CARD_NOT_DUE_FOR_REVIEW";
    public static final String CARD_FRONT_REQUIRED = "CARD_FRONT_REQUIRED";
    public static final String CARD_BACK_REQUIRED = "CARD_BACK_REQUIRED";
    public static final String CARD_ALREADY_DELETED = "CARD_ALREADY_DELETED";

    public static final String DAILY_REVIEW_LIMIT_EXCEEDED = "DAILY_REVIEW_LIMIT_EXCEEDED";
    public static final String SRS_SETTINGS_NOT_FOUND = "SRS_SETTINGS_NOT_FOUND";
}

