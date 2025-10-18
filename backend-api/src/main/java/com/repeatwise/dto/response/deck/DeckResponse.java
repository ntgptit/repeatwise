package com.repeatwise.dto.response.deck;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for deck details
 *
 * Requirements:
 * - UC-011: Create Deck - Return created deck
 * - UC-012: Move Deck - Return updated deck
 * - API Endpoints: GET /api/decks/{id}
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeckResponse {

    /**
     * Deck unique identifier
     */
    private UUID id;

    /**
     * Deck name
     */
    private String name;

    /**
     * Deck description (optional)
     */
    private String description;

    /**
     * Parent folder ID (null if root-level deck)
     */
    private UUID folderId;

    /**
     * Parent folder name (null if root-level deck)
     */
    private String folderName;

    /**
     * Total number of cards in deck
     */
    @Builder.Default
    private Integer cardCount = 0;

    /**
     * Number of cards due for review
     */
    @Builder.Default
    private Integer dueCards = 0;

    /**
     * Number of new cards (never studied)
     */
    @Builder.Default
    private Integer newCards = 0;

    /**
     * Creation timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    /**
     * Last update timestamp
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}
