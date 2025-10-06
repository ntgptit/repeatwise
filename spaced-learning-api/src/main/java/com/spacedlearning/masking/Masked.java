package com.spacedlearning.masking;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields that should be masked in responses
 * Used for sensitive data like email addresses, phone numbers, etc.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Masked {
    
    /**
     * The character to use for masking (default: '*')
     */
    char maskChar() default '*';
    
    /**
     * The number of characters to show at the beginning (default: 2)
     */
    int showStart() default 2;
    
    /**
     * The number of characters to show at the end (default: 2)
     */
    int showEnd() default 2;
    
    /**
     * Custom mask pattern (overrides showStart and showEnd)
     */
    String pattern() default "";
}
