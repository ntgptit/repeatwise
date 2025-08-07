package com.repeatwise.exception;

public class DailyLimitExceededException extends RuntimeException {

    private static final long serialVersionUID = -6570271653902134588L;

    public DailyLimitExceededException(String message) {
        super(message);
    }

    public DailyLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}