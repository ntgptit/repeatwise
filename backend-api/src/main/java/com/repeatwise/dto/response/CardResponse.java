package com.repeatwise.dto.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for card data
 *
 * Requirements:
 * - UC-017: Create/Edit Card
 * - API: GET /api/decks/{deckId}/cards
 * - API: POST /api/decks/{deckId}/cards (response)
 * - API: PUT /api/cards/{cardId} (response)
 *
 * Fields:
 * - id: Card UUID
 * - deckId: Parent deck UUID
 * - front: Question/front side text
 * - back: Answer/back side text
 * - createdAt: Creation timestamp
 * - updatedAt: Last update timestamp
 *
 * Note: SRS-related fields (currentBox, dueDate) will be added when review features are implemented
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

    private Instant createdAt;

    private Instant updatedAt;
}
