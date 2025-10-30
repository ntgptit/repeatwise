package com.repeatwise.controller;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.ICardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Card Management
 *
 * Requirements:
 * - UC-018: Create Card
 * - UC-019: Update Card
 * - UC-020: Delete Card
 *
 * Endpoints:
 * - GET    /api/decks/{deckId}/cards      - Get all cards in deck (paginated)
 * - POST   /api/decks/{deckId}/cards      - Create card
 * - GET    /api/cards/{cardId}            - Get card details
 * - PATCH  /api/cards/{cardId}            - Update card
 * - DELETE /api/cards/{cardId}            - Delete card (soft delete)
 *
 * @author RepeatWise Team
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final ICardService cardService;

    // ==================== UC-018: Create Card ====================

    /**
     * Create a new card in a deck
     * UC-018: Create Card
     *
     * Requirements:
     * - UC-018: Create Card
     * - BR-CARD-01: Front and Back are required, maximum 5000 characters each
     * - BR-CARD-03: New cards start in box 1 with status NEW
     * - BR-SRS-01: New cards are due immediately (due_date = current_date)
     *
     * Request Body:
     * {
     *   "front": "What is a closure in JavaScript?",
     *   "back": "A closure is the combination of a function..."
     * }
     *
     * Response: 201 Created with CardResponse
     *
     * Error Responses:
     * - 400 Bad Request: Validation errors (empty front/back, exceeds max length)
     * - 404 Not Found: Deck not found
     * - 403 Forbidden: Access denied
     *
     * @param deckId Deck UUID
     * @param request Create card request
     * @return Created card response with 201 Created status
     */
    @PostMapping("/api/decks/{deckId}/cards")
    public ResponseEntity<CardResponse> createCard(
            @PathVariable final UUID deckId,
            @Valid @RequestBody final CreateCardRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} POST /api/decks/{}/cards - Creating card: userId={}",
            LogEvent.START, deckId, userId);

        final CardResponse response = cardService.createCard(deckId, request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== UC-019: Update Card ====================

    /**
     * Update an existing card
     * UC-019: Update Card
     * UC-027: Edit Card During Review (same endpoint)
     *
     * Requirements:
     * - UC-019: Update Card
     * - UC-027: Edit Card During Review
     * - BR-CARD-04: Editing does not reset SRS state by default
     * - Only updates front and back content
     *
     * Request Body:
     * {
     *   "front": "What is a closure in JavaScript? (Updated)",
     *   "back": "A closure is a function that has access to variables..."
     * }
     *
     * Response: 200 OK with updated CardResponse
     *
     * Error Responses:
     * - 400 Bad Request: Validation errors
     * - 404 Not Found: Card not found
     * - 403 Forbidden: Access denied
     *
     * @param cardId Card UUID
     * @param request Update card request
     * @return Updated card response
     */
    @PatchMapping("/api/cards/{cardId}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable final UUID cardId,
            @Valid @RequestBody final UpdateCardRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} PATCH /api/cards/{} - Updating card: userId={}",
            LogEvent.START, cardId, userId);

        final CardResponse response = cardService.updateCard(cardId, request, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-020: Delete Card ====================

    /**
     * Delete a card (soft delete)
     * UC-020: Delete Card
     *
     * Requirements:
     * - UC-020: Delete Card
     * - BR-DEL-01: Cards use soft delete mechanism (deleted_at timestamp)
     * - BR-DEL-02: Soft-deleted cards excluded from all normal queries
     * - BR-DEL-03: Permanent deletion occurs 30 days after soft delete
     *
     * Response: 200 OK with success message
     *
     * Error Responses:
     * - 404 Not Found: Card not found
     * - 410 Gone: Card already deleted
     * - 403 Forbidden: Access denied
     *
     * @param cardId Card UUID to delete
     * @return 200 OK
     */
    @DeleteMapping("/api/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable final UUID cardId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} DELETE /api/cards/{} - Deleting card: userId={}",
            LogEvent.START, cardId, userId);

        cardService.deleteCard(cardId, userId);

        return ResponseEntity.ok().build();
    }

    // ==================== Get Card ====================

    /**
     * Get a single card by ID
     *
     * @param cardId Card UUID
     * @return Card details response
     */
    @GetMapping("/api/cards/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable final UUID cardId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/cards/{} - Getting card: userId={}",
            LogEvent.START, cardId, userId);

        final CardResponse response = cardService.getCard(cardId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all cards in a deck (paginated)
     *
     * @param deckId Deck UUID
     * @param pageable Pagination parameters
     * @return Page of card responses
     */
    @GetMapping("/api/decks/{deckId}/cards")
    public ResponseEntity<Page<CardResponse>> getCardsByDeck(
            @PathVariable final UUID deckId,
            @PageableDefault(size = 100) final Pageable pageable) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("event={} GET /api/decks/{}/cards - Getting cards: userId={}",
            LogEvent.START, deckId, userId);

        final Page<CardResponse> response = cardService.getCardsByDeck(deckId, userId, pageable);

        return ResponseEntity.ok(response);
    }
}

