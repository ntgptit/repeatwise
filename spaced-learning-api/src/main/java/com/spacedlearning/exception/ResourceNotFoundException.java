package com.spacedlearning.exception;

/**
 * Custom exception for resource not found errors
 * Used when a requested resource doesn't exist
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier));
    }

    public ResourceNotFoundException(String resourceType, String identifier, Throwable cause) {
        super(String.format("%s with identifier '%s' not found", resourceType, identifier), cause);
    }
}
