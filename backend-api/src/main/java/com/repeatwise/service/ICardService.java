package com.repeatwise.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.repeatwise.dto.request.CreateCardRequest;
import com.repeatwise.dto.request.UpdateCardRequest;
import com.repeatwise.dto.response.CardResponse;

/**
 * Card service interface
 *
 * Requirements:
 * - UC-017: Create/Edit Card
 * - UC-018: Delete Card
 *
 * Business Logic:
 * - Create card with validation
 * - Update card content (preserves SRS state)
 * - Delete card (soft delete)
 * - List cards in deck (paginated)
 * - Duplicate detection (warning only, not blocking)
 *
 * @author RepeatWise Team
 */
public interface ICardService {

    /**
     * Create a new card in a deck
     * UC-017: Create Card
     *
     * Business Rules:
     * - BR-CARD-001: Front and back cannot be empty
     * - BR-CARD-002: Max 5000 characters per side
     * - BR-037: Initialize SRS state (Box 1, due tomorrow)
     * - Duplicate warning logged but not blocking
     *
     * @param deckId Deck UUID
     * @param request CreateCardRequest DTO
     * @param userId Current user UUID
     * @return CardResponse DTO
     */
    CardResponse createCard(UUID deckId, CreateCardRequest request, UUID userId);

    /**
     * Update an existing card
     * UC-017: Edit Card
     *
     * Business Rules:
     * - BR-CARD-005: Editing preserves SRS state
     * - Only updates front and back text
     * - Validates ownership (user owns deck)
     *
     * @param cardId Card UUID
     * @param request UpdateCardRequest DTO
     * @param userId Current user UUID
     * @return CardResponse DTO
     */
    CardResponse updateCard(UUID cardId, UpdateCardRequest request, UUID userId);

    /**
     * Delete a card (soft delete)
     * UC-018: Delete Card
     *
     * Business Rules:
     * - BR-CARD-004: Soft delete removes from review schedule
     * - Validates ownership
     *
     * @param cardId Card UUID
     * @param userId Current user UUID
     */
    void deleteCard(UUID cardId, UUID userId);

    /**
     * Get a single card by ID
     * Validates ownership
     *
     * @param cardId Card UUID
     * @param userId Current user UUID
     * @return CardResponse DTO
     */
    CardResponse getCard(UUID cardId, UUID userId);

    /**
     * Get all cards in a deck (paginated)
     * UC-017: List cards
     *
     * @param deckId Deck UUID
     * @param userId Current user UUID
     * @param pageable Pagination parameters
     * @return Page of CardResponse DTOs
     */
    Page<CardResponse> getCardsByDeck(UUID deckId, UUID userId, Pageable pageable);
}
