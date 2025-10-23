package com.repeatwise.log;

import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing Mapped Diagnostic Context (MDC) in logging.
 * Provides thread-safe context for tracking requests, users, and operations.
 *
 * Usage:
 * <pre>
 * // Set request context
 * LogContext.setRequestId(UUID.randomUUID().toString());
 * LogContext.setUserId(userId);
 *
 * // Use in logs - MDC values automatically included
 * log.info("Processing request"); // Will include requestId and userId
 *
 * // Clear context when done
 * LogContext.clear();
 * </pre>
 */
public class LogContext {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String SESSION_ID = "sessionId";
    private static final String CORRELATION_ID = "correlationId";
    private static final String OPERATION = "operation";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";

    /**
     * Set request ID for tracking a single request.
     */
    public static void setRequestId(String requestId) {
        MDC.put(REQUEST_ID, requestId);
    }

    /**
     * Get current request ID.
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID);
    }

    /**
     * Generate and set a new request ID.
     */
    public static String generateRequestId() {
        String requestId = UUID.randomUUID().toString();
        setRequestId(requestId);
        return requestId;
    }

    /**
     * Set user ID for tracking user-specific operations.
     */
    public static void setUserId(Long userId) {
        if (userId != null) {
            MDC.put(USER_ID, userId.toString());
        }
    }

    /**
     * Get current user ID.
     */
    public static String getUserId() {
        return MDC.get(USER_ID);
    }

    /**
     * Set session ID for tracking user sessions.
     */
    public static void setSessionId(String sessionId) {
        MDC.put(SESSION_ID, sessionId);
    }

    /**
     * Get current session ID.
     */
    public static String getSessionId() {
        return MDC.get(SESSION_ID);
    }

    /**
     * Set correlation ID for tracking distributed operations.
     */
    public static void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
    }

    /**
     * Get current correlation ID.
     */
    public static String getCorrelationId() {
        return MDC.get(CORRELATION_ID);
    }

    /**
     * Set current operation name.
     */
    public static void setOperation(String operation) {
        MDC.put(OPERATION, operation);
    }

    /**
     * Get current operation.
     */
    public static String getOperation() {
        return MDC.get(OPERATION);
    }

    /**
     * Set client IP address.
     */
    public static void setClientIp(String clientIp) {
        MDC.put(CLIENT_IP, clientIp);
    }

    /**
     * Get client IP address.
     */
    public static String getClientIp() {
        return MDC.get(CLIENT_IP);
    }

    /**
     * Set user agent.
     */
    public static void setUserAgent(String userAgent) {
        MDC.put(USER_AGENT, userAgent);
    }

    /**
     * Get user agent.
     */
    public static String getUserAgent() {
        return MDC.get(USER_AGENT);
    }

    /**
     * Set custom context value.
     */
    public static void set(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Get custom context value.
     */
    public static String get(String key) {
        return MDC.get(key);
    }

    /**
     * Remove a specific context value.
     */
    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * Get all current context values.
     */
    public static Map<String, String> getAll() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * Clear all context values.
     * IMPORTANT: Call this at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Clear user-related context (keep request tracking).
     */
    public static void clearUserContext() {
        MDC.remove(USER_ID);
        MDC.remove(SESSION_ID);
    }

    /**
     * Execute code with temporary context.
     */
    public static <T> T withContext(Map<String, String> context, ContextSupplier<T> supplier) {
        Map<String, String> original = MDC.getCopyOfContextMap();
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            return supplier.get();
        } finally {
            MDC.clear();
            if (original != null) {
                original.forEach(MDC::put);
            }
        }
    }

    /**
     * Execute code with temporary context (no return value).
     */
    public static void withContext(Map<String, String> context, ContextRunnable runnable) {
        Map<String, String> original = MDC.getCopyOfContextMap();
        try {
            if (context != null) {
                context.forEach(MDC::put);
            }
            runnable.run();
        } finally {
            MDC.clear();
            if (original != null) {
                original.forEach(MDC::put);
            }
        }
    }

    @FunctionalInterface
    public interface ContextSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface ContextRunnable {
        void run();
    }
}
