package com.repeatwise.dto.response.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response DTO for card import result
 *
 * Requirements:
 * - UC-021: Import Cards
 *
 * Response Format:
 * {
 *   "imported": 4800,
 *   "skipped": 100,
 *   "failed": 100,
 *   "duplicatePolicy": "SKIP",
 *   "errorReportUrl": "/api/imports/reports/abc-uuid.csv"
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportResultResponse {

    /**
     * Number of cards successfully imported
     */
    private Integer imported;

    /**
     * Number of cards skipped (duplicates)
     */
    private Integer skipped;

    /**
     * Number of cards failed (validation errors)
     */
    private Integer failed;

    /**
     * Duplicate policy used
     */
    private String duplicatePolicy;

    /**
     * URL to download error report (if any errors)
     */
    private String errorReportUrl;

    /**
     * List of error messages (if any)
     */
    private List<ImportError> errors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportError {
        private Integer row;
        private String field;
        private String message;
    }
}

