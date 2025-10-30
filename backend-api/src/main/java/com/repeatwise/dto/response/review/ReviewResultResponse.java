package com.repeatwise.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for review rating result
 *
 * Requirements:
 * - UC-024: Rate Card
 * - API: POST /api/review/sessions/{sessionId}/rate
 *
 * Response Format:
 * {
 *   "nextCard": {...},  // Next card or null
 *   "remaining": 119,
 *   "progress": {
 *     "completed": 1,
 *     "total": 120
 *   },
 *   "completed": false  // true if session complete
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResultResponse {

    /**
     * Next card to review (null if session complete)
     */
    private ReviewSessionResponse.CardInfo nextCard;

    /**
     * Remaining cards count
     */
    private Integer remaining;

    /**
     * Progress information
     */
    private Progress progress;

    /**
     * Whether session is completed
     */
    private Boolean completed;

    /**
     * Progress information
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Progress {
        /**
         * Number of completed reviews
         */
        private Integer completed;

        /**
         * Total cards in session
         */
        private Integer total;
    }
}

