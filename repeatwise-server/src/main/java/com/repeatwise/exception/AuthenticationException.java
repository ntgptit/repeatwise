package com.repeatwise.exception;

public class AuthenticationException extends RuntimeException {

    private static final long serialVersionUID = 1949276834865843082L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
