package com.spacedlearning.validation;

import com.spacedlearning.dto.auth.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for password confirmation validation
 * Validates that password and confirmPassword fields match
 */
public class PasswordMatchValidator implements ConstraintValidator<ValidPasswordMatch, RegisterRequest> {

    @Override
    public void initialize(ValidPasswordMatch constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let @NotNull handle null validation
        }

        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (password == null || confirmPassword == null) {
            return true; // Let @NotBlank handle null/empty validation
        }

        boolean isValid = password.equals(confirmPassword);
        
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password and confirm password do not match")
                   .addPropertyNode("confirmPassword")
                   .addConstraintViolation();
        }

        return isValid;
    }
}

