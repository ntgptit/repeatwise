package com.repeatwise.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4278617043588095499L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}