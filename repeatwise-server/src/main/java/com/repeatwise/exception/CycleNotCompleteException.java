package com.repeatwise.exception;

public class CycleNotCompleteException extends RuntimeException {

    private static final long serialVersionUID = -747331259192615566L;

    public CycleNotCompleteException(String message) {
        super(message);
    }

    public CycleNotCompleteException(String message, Throwable cause) {
        super(message, cause);
    }
}