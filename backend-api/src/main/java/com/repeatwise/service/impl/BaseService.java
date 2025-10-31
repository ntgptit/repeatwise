package com.repeatwise.service.impl;

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
 * All service implementations should extend this class and pass MessageSource
 * to the constructor. Since services use @RequiredArgsConstructor from Lombok,
 * you need to exclude MessageSource from @RequiredArgsConstructor and inject it
 * separately, or manually write constructor to call super(messageSource).
 *
 * Example with manual constructor:
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Service
 *     @Slf4j
 *     public class MyServiceImpl extends BaseService implements IMyService {
 *         private final MyRepository repository;
 * 
 *         public MyServiceImpl(MyRepository repository, MessageSource messageSource) {
 *             super(messageSource);
 *             this.repository = repository;
 *         }
 *     }
 * }
 * </pre>
 *
 * Example with @RequiredArgsConstructor (excluding MessageSource):
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Service
 *     &#64;RequiredArgsConstructor
 *     @Slf4j
 *     public class MyServiceImpl extends BaseService implements IMyService {
 *         private final MyRepository repository;
 * 
 *         public MyServiceImpl(MyRepository repository, MessageSource messageSource) {
 *             super(messageSource);
 *             this.repository = repository;
 *         }
 *     }
 * }
 * </pre>
 *
 * @author RepeatWise Team
 */
public abstract class BaseService {

    protected final MessageSource messageSource;

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
     * Constructor for BaseService
     *
     * @param messageSource MessageSource bean for internationalization
     */
    protected BaseService(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

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
