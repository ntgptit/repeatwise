package com.repeatwise.exception;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -2045337082252219162L;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
