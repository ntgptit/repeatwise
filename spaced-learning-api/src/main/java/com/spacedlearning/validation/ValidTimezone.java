package com.spacedlearning.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for timezone validation
 * Validates that timezone is one of the supported timezones
 */
@Documented
@Constraint(validatedBy = TimezoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimezone {
    
    String message() default "Timezone must be one of: Asia/Ho_Chi_Minh, UTC, America/New_York, Europe/London";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
