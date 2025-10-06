package com.spacedlearning.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

/**
 * Validator implementation for timezone validation
 * Validates that timezone is one of the supported timezones
 */
public class TimezoneValidator implements ConstraintValidator<ValidTimezone, String> {

    private static final Set<String> SUPPORTED_TIMEZONES = Set.of(
        "Asia/Ho_Chi_Minh",
        "UTC",
        "America/New_York",
        "Europe/London"
    );

    @Override
    public void initialize(ValidTimezone constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String timezone, ConstraintValidatorContext context) {
        if (timezone == null || timezone.trim().isEmpty()) {
            return false;
        }

        return SUPPORTED_TIMEZONES.contains(timezone);
    }
}
