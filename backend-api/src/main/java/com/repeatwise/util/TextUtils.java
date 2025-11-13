package com.repeatwise.util;

import java.util.function.Supplier;

/**
 * Utility helpers for working with text values.
 */
public final class TextUtils {

    private TextUtils() {
        // Utility class
    }

    /**
     * Trim a string and return {@code null} when the result is empty.
     */
    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        final var trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Trim a string and return an empty string when the result is empty or {@code null}.
     */
    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Trim a string and ensure it is not blank. Throws the supplied exception when blank.
     */
    public static String trimAndRequireNonBlank(String value, Supplier<? extends RuntimeException> exceptionSupplier) {
        final var trimmed = trimToNull(value);
        if (trimmed == null) {
            throw exceptionSupplier.get();
        }
        return trimmed;
    }
}

