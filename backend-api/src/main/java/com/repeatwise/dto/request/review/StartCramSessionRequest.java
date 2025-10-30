package com.repeatwise.dto.request.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for starting a cram session
 *
 * Requirements:
 * - UC-029: Cram Mode
 *
 * Request Body:
 * {
 *   "scopeType": "DECK",
 *   "scopeId": "uuid",
 *   "applyToSrs": false,
 *   "filters": {
 *     "boxRange": { "min": 1, "max": 3 },
 *     "includeLearned": true
 *   }
 * }
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartCramSessionRequest {

    /**
     * Scope type: DECK or FOLDER
     */
    @NotNull(message = "{error.review.scopetype.required}")
    private ScopeType scopeType;

    /**
     * Scope ID (deck ID or folder ID)
     */
    @NotNull(message = "{error.review.scopeid.required}")
    private UUID scopeId;

    /**
     * Whether to apply ratings to SRS (default: false)
     */
    @Builder.Default
    private Boolean applyToSrs = false;

    /**
     * Optional filters
     */
    private Filters filters;

    public enum ScopeType {
        DECK,
        FOLDER
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Filters {
        /**
         * Box range filter (min and max box numbers)
         */
        private BoxRange boxRange;

        /**
         * Whether to include learned cards (high boxes)
         */
        @Builder.Default
        private Boolean includeLearned = true;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class BoxRange {
            private Integer min;
            private Integer max;
        }
    }
}

