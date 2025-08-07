package com.repeatwise.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.regex.Pattern;

public final class ValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Validates if the given string is a valid email address
     */
    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates if the given string is not blank and within the specified length
     */
    public static boolean isValidString(String value, int maxLength) {
        return StringUtils.isNotBlank(value) && value.length() <= maxLength;
    }
    
    /**
     * Validates if the given number is within the specified range
     */
    public static boolean isValidNumber(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates if the given string can be parsed as a number
     */
    public static boolean isNumeric(String value) {
        return NumberUtils.isCreatable(value);
    }
    
    /**
     * Validates if the given string is a valid UUID
     */
    public static boolean isValidUuid(String uuid) {
        return StringUtils.isNotBlank(uuid) &&
               uuid.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
    
    /**
     * Sanitizes input string by removing leading/trailing whitespace
     */
    public static String sanitizeInput(String input) {
        return StringUtils.trimToNull(input);
    }
    
    /**
     * Validates if the given string contains only alphanumeric characters and spaces
     */
    public static boolean isAlphanumericWithSpaces(String value) {
        return StringUtils.isNotBlank(value) && value.matches("^[a-zA-Z0-9\\s]+$");
    }
    
    /**
     * Validates if the given string contains only letters and spaces
     */
    public static boolean isLettersWithSpaces(String value) {
        return StringUtils.isNotBlank(value) && value.matches("^[a-zA-Z\\s]+$");
    }
} 