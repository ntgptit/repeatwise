package com.spacedlearning.masking;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for masking sensitive data in DTOs
 * Automatically masks fields annotated with @Masked
 */
@Service
@Slf4j
public class DataMaskingService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^(.{2}).*(.{2}@.*)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\d{3}).*(\\d{4})$");

    /**
     * Masks sensitive data in the given object
     * 
     * @param obj the object to mask
     * @return the object with masked sensitive fields
     */
    public <T> T maskSensitiveData(T obj) {
        if (obj == null) {
            return null;
        }

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Masked.class)) {
                    maskField(obj, field);
                }
            }
        } catch (Exception e) {
            log.warn("Error masking sensitive data for object: {}", obj.getClass().getSimpleName(), e);
        }

        return obj;
    }

    private void maskField(Object obj, Field field) throws IllegalAccessException {
        try {
            // Use MethodHandle for safer field access
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
            MethodHandle getter = lookup.unreflectGetter(field);
            MethodHandle setter = lookup.unreflectSetter(field);
            
            Object value = getter.invoke(obj);

            if (value instanceof String stringValue && !stringValue.isEmpty()) {
                Masked masked = field.getAnnotation(Masked.class);
                String maskedValue = maskString(stringValue, masked);
                setter.invoke(obj, maskedValue);
            }
        } catch (Throwable e) {
            // Log warning and continue - field might not be accessible
            log.warn("Cannot access field {} for masking: {}", field.getName(), e.getMessage());
        }
    }

    private String maskString(String value, Masked masked) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Use custom pattern if provided
        if (!masked.pattern().isEmpty()) {
            return applyCustomPattern(value, masked.pattern(), masked.maskChar());
        }

        // Apply default masking based on field type
        if (isEmail(value)) {
            return maskEmail(value, masked);
        } else if (isPhone(value)) {
            return maskPhone(value, masked);
        } else {
            return maskGeneric(value, masked);
        }
    }

    private String maskEmail(String email, Masked masked) {
        var matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = matcher.group(2);
            int maskLength = email.length() - start.length() - end.length();
            String mask = String.valueOf(masked.maskChar()).repeat(Math.max(0, maskLength));
            return start + mask + end;
        }
        return maskGeneric(email, masked);
    }

    private String maskPhone(String phone, Masked masked) {
        var matcher = PHONE_PATTERN.matcher(phone);
        if (matcher.matches()) {
            String start = matcher.group(1);
            String end = matcher.group(2);
            int maskLength = phone.length() - start.length() - end.length();
            String mask = String.valueOf(masked.maskChar()).repeat(Math.max(0, maskLength));
            return start + mask + end;
        }
        return maskGeneric(phone, masked);
    }

    private String maskGeneric(String value, Masked masked) {
        if (value.length() <= masked.showStart() + masked.showEnd()) {
            return String.valueOf(masked.maskChar()).repeat(value.length());
        }

        String start = value.substring(0, masked.showStart());
        String end = value.substring(value.length() - masked.showEnd());
        int maskLength = value.length() - masked.showStart() - masked.showEnd();
        String mask = String.valueOf(masked.maskChar()).repeat(maskLength);

        return start + mask + end;
    }

    private String applyCustomPattern(String value, String pattern, char maskChar) {
        // Simple pattern implementation - can be extended
        return value.replaceAll(pattern, String.valueOf(maskChar));
    }

    private boolean isEmail(String value) {
        return value.contains("@") && value.contains(".");
    }

    private boolean isPhone(String value) {
        return value.matches("^\\+?[0-9\\s\\-\\(\\)]+$");
    }
}
