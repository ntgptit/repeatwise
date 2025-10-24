package com.repeatwise.log.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for sanitizing sensitive data in log messages.
 * Prevents accidental logging of passwords, tokens, credit cards, etc.
 *
 * Usage:
 * <pre>
 * String sanitized = LogSanitizer.sanitize(userInput);
 * log.info("User data: {}", sanitized);
 * </pre>
 */
public class LogSanitizer {

    private static final String MASK = "***REDACTED***";
    private static final List<SensitivePattern> SENSITIVE_PATTERNS = new ArrayList<>();

    static {
        // Password patterns
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("(password|pwd|passwd)[\"']?\\s*[:=]\\s*[\"']?([^\\s,;\"'\\]\\}]+)",
                Pattern.CASE_INSENSITIVE),
            1, 2
        ));

        // Token patterns (JWT, Bearer tokens)
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("(token|authorization|auth)[\"']?\\s*[:=]\\s*[\"']?([\\w\\-\\.]+)",
                Pattern.CASE_INSENSITIVE),
            1, 2
        ));

        // API Key patterns
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("(api[_-]?key|apikey|access[_-]?key)[\"']?\\s*[:=]\\s*[\"']?([\\w\\-]+)",
                Pattern.CASE_INSENSITIVE),
            1, 2
        ));

        // Credit card patterns (13-19 digits)
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("\\b(\\d{13,19})\\b"),
            0, 1
        ));

        // SSN patterns (XXX-XX-XXXX)
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("\\b(\\d{3}-\\d{2}-\\d{4})\\b"),
            0, 1
        ));

        // Email patterns (partial masking)
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"),
            0, 1
        ));

        // Phone number patterns
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("\\b(\\+?\\d{1,3}[-.\\s]?)?\\(?(\\d{3})\\)?[-.\\s]?(\\d{3})[-.\\s]?(\\d{4})\\b"),
            0, 0
        ));

        // Secret patterns
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("(secret|private[_-]?key)[\"']?\\s*[:=]\\s*[\"']?([^\\s,;\"'\\]\\}]+)",
                Pattern.CASE_INSENSITIVE),
            1, 2
        ));

        // Connection strings with passwords
        SENSITIVE_PATTERNS.add(new SensitivePattern(
            Pattern.compile("(jdbc:[^;]+;[^;]*password=)([^;\\s]+)",
                Pattern.CASE_INSENSITIVE),
            1, 2
        ));
    }

    /**
     * Sanitize a string by replacing sensitive data with masks.
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;
        for (SensitivePattern pattern : SENSITIVE_PATTERNS) {
            result = pattern.sanitize(result);
        }
        return result;
    }

    /**
     * Sanitize multiple strings.
     */
    public static String[] sanitize(String... inputs) {
        if (inputs == null) {
            return null;
        }
        String[] sanitized = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            sanitized[i] = sanitize(inputs[i]);
        }
        return sanitized;
    }

    /**
     * Mask an email address, showing only first 2 chars and domain.
     * Example: user@example.com -> us***@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return MASK;
        }
        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].length() < 2) {
            return MASK;
        }
        return parts[0].substring(0, 2) + "***@" + parts[1];
    }

    /**
     * Mask a credit card, showing only last 4 digits.
     * Example: 1234567890123456 -> ************3456
     */
    public static String maskCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return MASK;
        }
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() < 13 || cleaned.length() > 19) {
            return MASK;
        }
        return "*".repeat(cleaned.length() - 4) + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * Mask a phone number, showing only last 4 digits.
     */
    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return MASK;
        }
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.length() < 10) {
            return MASK;
        }
        return "***-***-" + cleaned.substring(cleaned.length() - 4);
    }

    /**
     * Completely mask a token or password.
     */
    public static String maskSecret(String secret) {
        return MASK;
    }

    /**
     * Check if a string contains potentially sensitive data.
     */
    public static boolean containsSensitiveData(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        for (SensitivePattern pattern : SENSITIVE_PATTERNS) {
            if (pattern.pattern.matcher(input).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Internal class representing a sensitive data pattern.
     */
    private static class SensitivePattern {
        private final Pattern pattern;
        private final int keepGroupIndex;  // Group to keep visible (0 = mask all)
        private final int maskGroupIndex;  // Group to mask

        SensitivePattern(Pattern pattern, int keepGroupIndex, int maskGroupIndex) {
            this.pattern = pattern;
            this.keepGroupIndex = keepGroupIndex;
            this.maskGroupIndex = maskGroupIndex;
        }

        String sanitize(String input) {
            Matcher matcher = pattern.matcher(input);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                if (keepGroupIndex == 0 && maskGroupIndex == 0) {
                    // Mask entire match
                    matcher.appendReplacement(sb, MASK);
                } else if (keepGroupIndex == 0 && maskGroupIndex > 0) {
                    // Mask specific group
                    matcher.appendReplacement(sb, MASK);
                } else {
                    // Keep some parts, mask others
                    String replacement = matcher.group(keepGroupIndex) + "=" + MASK;
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    /**
     * Builder for custom sanitization rules.
     */
    public static class Builder {
        private final List<SensitivePattern> customPatterns = new ArrayList<>();

        public Builder addPattern(String regex) {
            customPatterns.add(new SensitivePattern(
                Pattern.compile(regex), 0, 0
            ));
            return this;
        }

        public Builder addPattern(Pattern pattern) {
            customPatterns.add(new SensitivePattern(pattern, 0, 0));
            return this;
        }

        public String sanitize(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }

            String result = LogSanitizer.sanitize(input);
            for (SensitivePattern pattern : customPatterns) {
                result = pattern.sanitize(result);
            }
            return result;
        }
    }

    /**
     * Create a custom sanitizer with additional patterns.
     */
    public static Builder builder() {
        return new Builder();
    }
}
