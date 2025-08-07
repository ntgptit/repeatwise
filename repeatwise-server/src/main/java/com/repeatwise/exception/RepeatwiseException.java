package com.repeatwise.exception;

public class RepeatwiseException extends RuntimeException {

    private static final long serialVersionUID = 3173034506097701401L;

    public RepeatwiseException(String message) {
        super(message);
    }

    public RepeatwiseException(String message, Throwable cause) {
        super(message, cause);
    }
}