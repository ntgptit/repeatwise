package com.repeatwise.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable performance monitoring for methods.
 * Logs execution time and can trigger warnings for slow operations.
 *
 * Usage:
 * <pre>
 * {@code @PerformanceLog(warnThresholdMs = 1000, errorThresholdMs = 5000)}
 * public void processLargeDataset() {
 *     // method implementation
 * }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceLog {

    /**
     * Operation name for performance logging.
     * If empty, uses method name.
     */
    String operation() default "";

    /**
     * Warn threshold in milliseconds.
     * If execution time exceeds this, log at WARN level.
     */
    long warnThresholdMs() default 1000;

    /**
     * Error threshold in milliseconds.
     * If execution time exceeds this, log at ERROR level.
     */
    long errorThresholdMs() default 5000;

    /**
     * Whether to log method arguments (default: false for performance).
     */
    boolean logArgs() default false;

    /**
     * Whether to always log regardless of threshold (default: false).
     * If false, only logs when thresholds are exceeded.
     */
    boolean alwaysLog() default false;
}
