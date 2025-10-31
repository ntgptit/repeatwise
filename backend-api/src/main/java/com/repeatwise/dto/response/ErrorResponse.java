package com.repeatwise.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Standard Error Response DTO
 *
 * Requirements:
 * - API Response Specs: Standard error format
 * - Coding Convention: Consistent error handling
 * - UC-001: Details can be List<String> or List<Map<String, String>> for validation errors
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    /**
     * Error details
     * Can be List<String> for general errors or List<Map<String, String>> for validation errors
     * UC-001: Validation errors format: [{"field": "email", "message": "Invalid email format"}]
     */
    private List<?> details;
}
