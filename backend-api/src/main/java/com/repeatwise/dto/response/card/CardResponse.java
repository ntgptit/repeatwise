package com.repeatwise.dto.response.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for card data
 *
 * Requirements:
 * - UC-017: Create/Edit Card
 * - UC-019: Review Cards with SRS
 * - API: GET /api/decks/{deckId}/cards
 * - API: POST /api/decks/{deckId}/cards (response)
 * - API: PUT /api/cards/{cardId} (response)
 * - API: GET /api/review/sessions (review session)
 *
 * Fields:
 * - id: Card UUID
 * - deckId: Parent deck UUID
 * - front: Question/front side text
 * - back: Answer/back side text
 * - createdAt: Creation timestamp
 * - updatedAt: Last update timestamp
 * - currentBox: Current SRS box (1-7, nullable for non-review contexts)
 * - dueDate: Next review due date (nullable for non-review contexts)
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {

    private UUID id;

    private UUID deckId;

    private String front;

    private String back;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;

    /**
     * Current SRS box (1-7)
     * Only present in review session responses
     */
    private Integer currentBox;

    /**
     * Next review due date
     * Only present in review session responses
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /**
     * Total review count
     * Only present in review session responses
     */
    private Integer reviewCount;

    /**
     * Lapse count (times forgotten)
     * Only present in review session responses
     */
    private Integer lapseCount;

    /**
     * Last reviewed timestamp
     * Only present in review session responses
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant lastReviewedAt;
}

