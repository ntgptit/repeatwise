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
    private List<String> details;
}
