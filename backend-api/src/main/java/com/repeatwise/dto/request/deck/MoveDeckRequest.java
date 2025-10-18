package com.repeatwise.dto.request.deck;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for moving a deck to a different folder
 *
 * Requirements:
 * - UC-012: Move Deck
 * - API: POST /api/decks/{deckId}/move
 *
 * Business Rules:
 * - BR-040: Move validation
 * - Destination folder must exist and belong to user
 * - Cannot move to same folder
 * - Deck ownership verified before move
 * - newFolderId can be NULL (move to root level)
 *
 * - BR-041: Name conflict handling
 * - If duplicate name in destination: error (default)
 *
 * - BR-042: Statistics update
 * - Old folder stats decremented
 * - New folder stats incremented
 *
 * - BR-043: Review progress preservation
 * - All review data preserved (card_box_position unchanged)
 *
 * Request Example:
 * {
 * "newFolderId": "uuid" // nullable - null means move to root
 * }
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveDeckRequest {

    /**
     * Target folder ID
     * - If null: move to root level (no folder)
     * - If not null: move to specified folder
     *
     * Validation:
     * - Must exist and belong to user
     * - Cannot be same as current folder
     */
    private UUID newFolderId;
}
