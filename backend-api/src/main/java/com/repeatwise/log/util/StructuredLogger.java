package com.repeatwise.log.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repeatwise.log.LogEvent;
import com.repeatwise.log.LogLevel;
import com.repeatwise.log.context.LogContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Structured logging utility for outputting JSON-formatted logs.
 * Facilitates log parsing, searching, and analysis by log aggregation systems.
 *
 * Features:
 * - JSON-formatted log output
 * - Automatic MDC context inclusion
 * - Type-safe log field builders
 * - Integration with ELK, Splunk, CloudWatch, etc.
 *
 * Usage:
 *
 * <pre>
 * StructuredLogger.builder()
 *         .event(LogEvent.USER_GET_PROFILE)
 *         .level(LogLevel.INFO)
 *         .message("User profile retrieved")
 *         .field("userId", userId)
 *         .field("duration", durationMs)
 *         .log();
 * </pre>
 */
@Slf4j
public class StructuredLogger {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static boolean enableJsonLogging = true;

    /**
     * Enable or disable JSON logging globally.
     */
    public static void setJsonLoggingEnabled(boolean enabled) {
        enableJsonLogging = enabled;
    }

    /**
     * Create a new structured log builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create a builder for a specific logger.
     */
    public static Builder builder(Logger logger) {
        return new Builder(logger);
    }

    /**
     * Create a builder for a specific class.
     */
    public static Builder builder(Class<?> clazz) {
        return new Builder(LoggerFactory.getLogger(clazz));
    }

    /**
     * Quick log methods for common patterns.
     */
    public static void logEvent(LogEvent event, String message) {
        builder()
                .event(event)
                .level(event.getSuggestedLevel())
                .message(message)
                .log();
    }

    public static void logEvent(LogEvent event, String message, Map<String, Object> fields) {
        final var b = builder()
                .event(event)
                .level(event.getSuggestedLevel())
                .message(message);
        fields.forEach(b::field);
        b.log();
    }

    public static void logError(LogEvent event, String message, Throwable throwable) {
        builder()
                .event(event)
                .level(LogLevel.ERROR)
                .message(message)
                .exception(throwable)
                .log();
    }

    /**
     * Builder for creating structured log entries.
     */
    public static class Builder {
        private final Logger logger;
        private final Map<String, Object> fields = new HashMap<>();
        private LogLevel level = LogLevel.INFO;
        private LogEvent event;
        private String message;
        private Throwable exception;

        Builder() {
            this(log);
        }

        Builder(Logger logger) {
            this.logger = logger;
            // Add timestamp
            this.fields.put("@timestamp", Instant.now().toString());
        }

        public Builder level(LogLevel level) {
            this.level = level;
            this.fields.put("level", level.name());
            return this;
        }

        public Builder event(LogEvent event) {
            this.event = event;
            this.fields.put("event", event.name());
            this.fields.put("eventCategory", event.getCategory());
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            this.fields.put("message", message);
            return this;
        }

        public Builder field(String key, Object value) {
            if (value != null) {
                this.fields.put(key, value);
            }
            return this;
        }

        public Builder fields(Map<String, Object> additionalFields) {
            if (additionalFields != null) {
                additionalFields.forEach(this::field);
            }
            return this;
        }

        public Builder exception(Throwable throwable) {
            this.exception = throwable;
            if (throwable != null) {
                this.fields.put("exception", Map.of(
                        "type", throwable.getClass().getName(),
                        "message", throwable.getMessage() != null ? throwable.getMessage() : "",
                        "stackTrace", getStackTraceAsString(throwable)));
            }
            return this;
        }

        public Builder userId(Long userId) {
            return field("userId", userId);
        }

        public Builder requestId(String requestId) {
            return field("requestId", requestId);
        }

        public Builder duration(long durationMs) {
            return field("durationMs", durationMs);
        }

        public Builder statusCode(int statusCode) {
            return field("statusCode", statusCode);
        }

        public Builder endpoint(String endpoint) {
            return field("endpoint", endpoint);
        }

        public Builder method(String method) {
            return field("method", method);
        }

        /**
         * Include MDC context in the log.
         */
        public Builder withContext() {
            final var mdcContext = LogContext.getAll();
            if ((mdcContext != null) && !mdcContext.isEmpty()) {
                this.fields.put("context", mdcContext);
            }
            return this;
        }

        /**
         * Log the structured message.
         */
        public void log() {
            // Always include MDC context
            withContext();

            if (enableJsonLogging) {
                logJson();
            } else {
                logPlain();
            }
        }

        /**
         * Log as JSON format.
         */
        private void logJson() {
            try {
                final var jsonLog = objectMapper.writeValueAsString(this.fields);
                logAtLevel(jsonLog, this.exception);
            } catch (final JsonProcessingException e) {
                this.logger.error("Failed to serialize log to JSON: {}", this.fields, e);
                logPlain();
            }
        }

        /**
         * Log as plain text format.
         */
        private void logPlain() {
            final var sb = new StringBuilder();
            if (this.event != null) {
                sb.append("[").append(this.event).append("] ");
            }
            if (this.message != null) {
                sb.append(this.message);
            }
            if (!this.fields.isEmpty()) {
                sb.append(" | ");
                this.fields.forEach((k, v) -> {
                    if (!"@timestamp".equals(k) && !"event".equals(k) &&
                            !"eventCategory".equals(k) && !"level".equals(k) &&
                            !"message".equals(k) && !"context".equals(k)) {
                        sb.append(k).append("=").append(v).append(" ");
                    }
                });
            }
            logAtLevel(sb.toString().trim(), this.exception);
        }

        /**
         * Log at the specified level.
         */
        private void logAtLevel(String message, Throwable throwable) {
            switch (this.level) {
            case TRACE -> {
                if (throwable != null) {
                    this.logger.trace(message, throwable);
                } else {
                    this.logger.trace(message);
                }
            }
            case DEBUG -> {
                if (throwable != null) {
                    this.logger.debug(message, throwable);
                } else {
                    this.logger.debug(message);
                }
            }
            case INFO -> {
                if (throwable != null) {
                    this.logger.info(message, throwable);
                } else {
                    this.logger.info(message);
                }
            }
            case WARN -> {
                if (throwable != null) {
                    this.logger.warn(message, throwable);
                } else {
                    this.logger.warn(message);
                }
            }
            case ERROR -> {
                if (throwable != null) {
                    this.logger.error(message, throwable);
                } else {
                    this.logger.error(message);
                }
            }
            }
        }

        /**
         * Get stack trace as string (limited depth).
         */
        private String getStackTraceAsString(Throwable throwable) {
            if (throwable == null) {
                return "";
            }
            final var elements = throwable.getStackTrace();
            final var limit = Math.min(elements.length, 10); // Limit to 10 frames
            final var sb = new StringBuilder();
            for (var i = 0; i < limit; i++) {
                sb.append(elements[i].toString()).append("\n");
            }
            if (elements.length > limit) {
                sb.append("... ").append(elements.length - limit).append(" more");
            }
            return sb.toString();
        }
    }

    /**
     * Fluent API for quick structured logs.
     */
    public static class QuickLog {
        public static void info(LogEvent event, String message) {
            logEvent(event, message);
        }

        public static void warn(LogEvent event, String message) {
            builder().event(event).level(LogLevel.WARN).message(message).log();
        }

        public static void error(LogEvent event, String message) {
            builder().event(event).level(LogLevel.ERROR).message(message).log();
        }

        public static void error(LogEvent event, String message, Throwable throwable) {
            logError(event, message, throwable);
        }

        public static void debug(LogEvent event, String message) {
            builder().event(event).level(LogLevel.DEBUG).message(message).log();
        }
    }
}
