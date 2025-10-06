package com.spacedlearning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for password confirmation
 * Validates that password and confirmPassword fields match
 */
@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPasswordMatch {
    
    String message() default "Password and confirm password do not match";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}

