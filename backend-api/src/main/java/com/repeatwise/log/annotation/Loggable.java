package com.repeatwise.log.annotation;

import com.repeatwise.log.LogEvent;
import com.repeatwise.log.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable automatic logging for methods.
 * When applied, logs method entry, exit, and exceptions.
 *
 * Usage:
 * <pre>
 * {@code @Loggable(event = LogEvent.USER_GET_PROFILE, level = LogLevel.INFO)}
 * public UserDTO getProfile(Long userId) {
 *     // method implementation
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {

    /**
     * The log event to be associated with this method.
     */
    LogEvent event() default LogEvent.START;

    /**
     * The log level for this method.
     */
    LogLevel level() default LogLevel.INFO;

    /**
     * Whether to log method parameters (default: true).
     * Set to false for sensitive data.
     */
    boolean logArgs() default true;

    /**
     * Whether to log the return value (default: true).
     * Set to false for sensitive data.
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time (default: false).
     */
    boolean logExecutionTime() default false;

    /**
     * Custom message to log. If empty, uses default format.
     */
    String message() default "";
}
