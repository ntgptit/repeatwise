package com.repeatwise.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for review session
 *
 * Requirements:
 * - UC-023: Review Cards with SRS
 * - API: POST /api/review/sessions
 *
 * Response Format:
 * {
 *   "sessionId": "uuid",
 *   "totalCards": 120,
 *   "firstCard": {
 *     "id": "uuid",
 *     "front": "Card front text",
 *     "currentBox": 1,
 *     "dueDate": "2025-01-31"
 *   }
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSessionResponse {

    /**
     * Session unique identifier
     */
    private UUID sessionId;

    /**
     * Session type: NORMAL, CRAM, or RANDOM
     */
    private String sessionType;

    /**
     * Whether ratings apply to SRS (for cram mode)
     */
    private Boolean applyToSrs;

    /**
     * Total cards in session
     */
    private Integer totalCards;

    /**
     * First card to review
     */
    private CardInfo firstCard;

    /**
     * Remaining cards count
     */
    private Integer remaining;

    /**
     * Card information with SRS fields
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CardInfo {
        private UUID id;
        private UUID deckId;
        private String front;
        private String back;
        private Integer currentBox;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate dueDate;
        private Integer reviewCount;
        private Integer lapseCount;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private java.time.Instant lastReviewedAt;
    }
}

