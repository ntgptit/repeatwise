package com.repeatwise.dto.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.repeatwise.entity.enums.CardRating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for review log (history)
 *
 * Requirements:
 * - UC-025: Undo Review
 * - API: GET /api/review/logs (if implemented)
 *
 * Used for review history display and undo functionality
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLogResponse {

    /**
     * Review log unique identifier
     */
    private UUID id;

    /**
     * Card ID
     */
    private UUID cardId;

    /**
     * Card front text (for display)
     */
    private String cardFront;

    /**
     * Card back text (for display)
     */
    private String cardBack;

    /**
     * User ID
     */
    private UUID userId;

    /**
     * User rating
     */
    private CardRating rating;

    /**
     * Box before review
     */
    private Integer previousBox;

    /**
     * Box after review
     */
    private Integer newBox;

    /**
     * Interval days assigned
     */
    private Integer intervalDays;

    /**
     * Review timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant reviewedAt;
}

