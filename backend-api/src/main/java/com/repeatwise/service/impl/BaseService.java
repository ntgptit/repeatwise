package com.repeatwise.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Base service class for common functionality
 *
 * Provides common methods used across all service implementations:
 * - getMessage(): Internationalized message retrieval
 *
 * Requirements:
 * - Coding Convention: Common service utilities
 * - MessageSource integration for i18n
 *
 * Usage:
 * All service implementations should extend this class.
 * MessageSource is injected via field injection to allow @RequiredArgsConstructor
 * in service implementations without needing to call super(messageSource).
 *
 * Note: Field injection is used here for MessageSource to enable @RequiredArgsConstructor
 * in subclasses. This is acceptable for this base class as MessageSource is a framework
 * infrastructure dependency that doesn't change.
 *
 * @author RepeatWise Team
 */
public abstract class BaseService {

    @Autowired
    protected MessageSource messageSource;

    // ==================== Validation Constants ====================

    /**
     * Common validation error messages for null checks
     */
    protected static final String MSG_USER_ID_CANNOT_BE_NULL = "User ID cannot be null";
    protected static final String MSG_DECK_ID_CANNOT_BE_NULL = "Deck ID cannot be null";
    protected static final String MSG_CARD_ID_CANNOT_BE_NULL = "Card ID cannot be null";
    protected static final String MSG_FOLDER_ID_CANNOT_BE_NULL = "Folder ID cannot be null";
    protected static final String MSG_REQUEST_CANNOT_BE_NULL = "Request cannot be null";
    protected static final String MSG_SESSION_ID_CANNOT_BE_NULL = "Session ID cannot be null";

    /**
     * Get internationalized message
     *
     * Requirements:
     * - Coding Convention: Use MessageSource for error messages
     * - Uses LocaleContextHolder to get current locale
     *
     * @param code Message code (key)
     * @param args Arguments for message placeholders
     * @return Internationalized message string
     */
    protected String getMessage(final String code, final Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
