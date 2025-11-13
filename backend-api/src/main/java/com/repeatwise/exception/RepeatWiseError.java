package com.repeatwise.exception;

import org.springframework.http.HttpStatus;

import com.repeatwise.constant.ApiErrorCode;

import lombok.Getter;

/**
 * Centralized definition of RepeatWise business errors.
 */
@Getter
public enum RepeatWiseError {

    /** User attempts to access a forbidden resource. */
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, ApiErrorCode.UNAUTHORIZED_ACCESS, "error.auth.forbidden"),
    /** Login failed because credentials are incorrect. */
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_CREDENTIALS, "error.user.invalid.credentials"),
    /** JWT token is invalid (malformed, bad signature, etc.). */
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ApiErrorCode.INVALID_TOKEN, "error.auth.token.invalid"),
    /** JWT token has expired. */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ApiErrorCode.TOKEN_EXPIRED, "error.auth.token.expired"),
    /** Refresh token is missing from the request. */
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, ApiErrorCode.REFRESH_TOKEN_MISSING,
            "error.auth.refresh.token.missing"),
    /** Refresh token cannot be found or verified. */
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, ApiErrorCode.REFRESH_TOKEN_NOT_FOUND,
            "error.auth.refresh.token.missing"),
    /** Refresh token has expired. */
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ApiErrorCode.REFRESH_TOKEN_EXPIRED,
            "error.auth.refresh.token.expired"),
    /** Refresh token has been revoked. */
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, ApiErrorCode.REFRESH_TOKEN_REVOKED,
            "error.auth.refresh.token.revoked"),
    /** Refresh token reuse detected, indicating a possible attack. */
    TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, ApiErrorCode.TOKEN_REUSE_DETECTED, "error.auth.token.reuse"),

    /** User cannot be found by the given identifier. */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ApiErrorCode.USER_NOT_FOUND, "error.user.not.found"),
    /** Email address is already registered. */
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ApiErrorCode.EMAIL_ALREADY_EXISTS, "error.user.email.already.exists"),
    /** Username is already taken. */
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ApiErrorCode.USERNAME_ALREADY_EXISTS,
            "error.user.username.already.exists"),

    /** Card cannot be found. */
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, ApiErrorCode.CARD_NOT_FOUND, "error.card.not.found"),
    /** Card front content is required. */
    CARD_FRONT_REQUIRED(HttpStatus.BAD_REQUEST, ApiErrorCode.CARD_FRONT_REQUIRED, "error.card.front.required"),
    /** Card back content is required. */
    CARD_BACK_REQUIRED(HttpStatus.BAD_REQUEST, ApiErrorCode.CARD_BACK_REQUIRED, "error.card.back.required"),
    /** Card has already been deleted. */
    CARD_ALREADY_DELETED(HttpStatus.GONE, ApiErrorCode.CARD_ALREADY_DELETED, "error.card.delete.already.deleted"),

    /** Deck cannot be found. */
    DECK_NOT_FOUND(HttpStatus.NOT_FOUND, ApiErrorCode.DECK_NOT_FOUND, "error.deck.not.found"),
    /** Deck name is required. */
    DECK_NAME_REQUIRED(HttpStatus.BAD_REQUEST, ApiErrorCode.DECK_NAME_REQUIRED, "error.deck.name.required"),
    /** Deck name already exists within the scope. */
    DECK_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ApiErrorCode.DECK_NAME_ALREADY_EXISTS, "error.deck.name.exists"),
    /** Deck is already located in the requested folder/root. */
    DECK_ALREADY_IN_LOCATION(HttpStatus.BAD_REQUEST, ApiErrorCode.DECK_ALREADY_IN_LOCATION,
            "error.deck.move.same.folder"),
    /** Deck is too large to copy synchronously. */
    DECK_TOO_LARGE(HttpStatus.BAD_REQUEST, ApiErrorCode.DECK_TOO_LARGE, "error.deck.too.large"),

    /** Folder cannot be found. */
    FOLDER_NOT_FOUND(HttpStatus.NOT_FOUND, ApiErrorCode.FOLDER_NOT_FOUND, "error.folder.not.found"),
    /** Folder name already exists at the same location. */
    FOLDER_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ApiErrorCode.FOLDER_NAME_ALREADY_EXISTS,
            "error.folder.name.exists"),
    /** Attempt to move a folder into itself or its descendant. */
    CIRCULAR_FOLDER_REFERENCE(HttpStatus.BAD_REQUEST, ApiErrorCode.CIRCULAR_FOLDER_REFERENCE,
            "error.folder.circular.reference"),
    /** Folder hierarchy exceeds the configured depth limit. */
    MAX_FOLDER_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED, "error.folder.max.depth"),
    /** Folder copy operation exceeds allowed item limit. */
    FOLDER_TOO_LARGE(HttpStatus.BAD_REQUEST, ApiErrorCode.FOLDER_TOO_LARGE, "error.folder.too.large"),

    /** Card is not yet due for review. */
    CARD_NOT_DUE_FOR_REVIEW(HttpStatus.BAD_REQUEST, ApiErrorCode.CARD_NOT_DUE_FOR_REVIEW, "error.review.card.not.due"),
    /** Daily review limit has been reached. */
    DAILY_REVIEW_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, ApiErrorCode.DAILY_REVIEW_LIMIT_EXCEEDED,
            "error.review.daily.limit.reached"),
    /** User does not have configured SRS settings. */
    SRS_SETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, ApiErrorCode.SRS_SETTINGS_NOT_FOUND, "error.srs.settings.not.found"),

    /** New password and confirmation do not match. */
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, ApiErrorCode.PASSWORD_MISMATCH, "error.user.password.mismatch"),
    /** Current password is incorrect when changing password. */
    INCORRECT_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, ApiErrorCode.INCORRECT_PASSWORD,
            "error.user.password.current.incorrect"),
    /** New password is the same as the current password. */
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, ApiErrorCode.PASSWORD_MISMATCH, "error.user.password.same.as.current");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String messageKey;

    RepeatWiseError(HttpStatus httpStatus, String errorCode, String messageKey) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.messageKey = messageKey;
    }
}
