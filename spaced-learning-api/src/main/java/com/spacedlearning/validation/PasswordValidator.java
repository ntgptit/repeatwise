package com.spacedlearning.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for password strength validation
 * Validates that password contains at least one uppercase letter, one lowercase letter,
 * one digit, and one special character
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Check minimum length
        if (password.length() < 8) {
            return false;
        }

        // Check pattern: at least one lowercase, one uppercase, one digit, one special character
        return password.matches(PASSWORD_PATTERN);
    }
}
