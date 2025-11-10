package com.repeatwise.dto.response.error;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
    private Map<String, String> validationErrors;

    /**
     * Create error response for general exceptions
     */
    public static ErrorResponse of(int status, String error, String message, String errorCode, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .errorCode(errorCode)
                .path(path)
                .build();
    }

    /**
     * Create error response for validation errors
     */
    public static ErrorResponse ofValidation(int status, String message, Map<String, String> validationErrors,
            String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error("Validation Error")
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }
}
