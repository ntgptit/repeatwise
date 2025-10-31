package com.repeatwise.service.impl;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.User;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.log.LogEvent;
import com.repeatwise.mapper.CardMapper;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.ICardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Card Service
 *
 * Requirements:
 * - UC-018: Create Card
 * - UC-019: Update Card
 * - UC-020: Delete Card
 *
 * Business Rules:
 * - BR-CARD-01: Front and Back are required, maximum 5000 characters each
 * - BR-CARD-02: Card belongs to exactly one deck
 * - BR-CARD-03: New cards start in box 1 with status NEW
 * - BR-CARD-04: Editing does not reset SRS state by default
 * - BR-SRS-01: New cards are due immediately (due_date = current_date)
 *
 * Coding Standards:
 * - Method max 30 lines
 * - Use Apache Commons for string operations
 * - Use MessageSource for error messages
 * - Early return & Guard clauses
 * - Final for immutable variables
 * - Logging with context
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl extends BaseService implements ICardService {

    // ==================== Dependencies ====================

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final CardMapper cardMapper;

    // ==================== UC-018: Create Card ====================

    /**
     * Create a new card in a deck
     *
     * Requirements:
     * - UC-018: Create Card
     * - BR-CARD-01: Front and Back required, max 5000 chars
     * - BR-CARD-03: New cards start in box 1, due_date = today
     *
     * Steps:
     * 1. Validate request and parameters
     * 2. Get deck with ownership check
     * 3. Get user
     * 4. Validate card content
     * 5. Build card entity
     * 6. Save card
     * 7. Initialize CardBoxPosition (Box 1, due today)
     * 8. Return response
     */
    @Transactional
    @Override
    public CardResponse createCard(final UUID deckId, final CreateCardRequest request, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(request, "CreateCardRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Creating card: deckId={}, userId={}", LogEvent.START, deckId, userId);

        // Step 1: Get deck with ownership check
        final var deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Get user
        final var user = getUser(userId);

        // Step 3: Validate card content
        validateCardContent(request.getFront(), request.getBack());

        // Step 4: Build card entity
        final var card = buildCard(request, deck);

        // Step 5: Save card
        final var savedCard = this.cardRepository.save(card);

        // Step 6: Initialize CardBoxPosition (Box 1, due today)
        initializeCardBoxPosition(savedCard, user);

        log.info("event={} Card created successfully: cardId={}, deckId={}, userId={}",
                LogEvent.SUCCESS, savedCard.getId(), deckId, userId);

        return this.cardMapper.toResponse(savedCard);
    }

    // ==================== UC-019: Update Card ====================

    /**
     * Update an existing card
     *
     * Requirements:
     * - UC-019: Update Card
     * - BR-CARD-04: Editing does not reset SRS state
     * - Only updates front and back content
     *
     * Steps:
     * 1. Validate parameters
     * 2. Get card with ownership check
     * 3. Validate card content
     * 4. Update card fields
     * 5. Save card
     * 6. Return response
     */
    @Transactional
    @Override
    public CardResponse updateCard(final UUID cardId, final UpdateCardRequest request, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(cardId, "Card ID cannot be null");
        Objects.requireNonNull(request, "UpdateCardRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Updating card: cardId={}, userId={}", LogEvent.START, cardId, userId);

        // Step 1: Get card with ownership check
        final var card = getCardWithOwnershipCheck(cardId, userId);

        // Step 2: Validate card content
        validateCardContent(request.getFront(), request.getBack());

        // Step 3: Update card fields (preserves SRS state - UC-027)
        // Note: Only updates front/back, CardBoxPosition remains unchanged
        updateCardFields(card, request);

        // Step 4: Save card
        final var savedCard = this.cardRepository.save(card);

        // Step 5: Build response with SRS fields if available
        final var response = buildCardResponseWithSrs(savedCard, userId);

        log.info("event={} Card updated successfully: cardId={}, userId={}", LogEvent.SUCCESS, cardId, userId);

        return response;
    }

    // ==================== UC-020: Delete Card ====================

    /**
     * Delete a card (soft delete)
     *
     * Requirements:
     * - UC-020: Delete Card
     * - BR-DEL-01: Cards use soft delete mechanism
     * - BR-DEL-02: Soft-deleted cards excluded from all normal queries
     *
     * Steps:
     * 1. Validate parameters
     * 2. Get card with ownership check
     * 3. Validate card is not already deleted
     * 4. Soft delete card
     * 5. Save card
     */
    @Transactional
    @Override
    public void deleteCard(final UUID cardId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(cardId, "Card ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Deleting card: cardId={}, userId={}", LogEvent.START, cardId, userId);

        // Step 1: Get card with ownership check
        final var card = getCardWithOwnershipCheck(cardId, userId);

        // Step 2: Validate card is not already deleted
        if (card.isDeleted()) {
            log.warn("event={} Card already deleted: cardId={}, userId={}", LogEvent.EX_VALIDATION, cardId, userId);
            throw new ResourceNotFoundException(
                    "CARD_010",
                    getMessage("error.card.delete.already.deleted"));
        }

        // Step 3: Soft delete card
        card.softDelete();

        // Step 4: Save card
        this.cardRepository.save(card);

        log.info("event={} Card deleted successfully: cardId={}, userId={}", LogEvent.SUCCESS, cardId, userId);
    }

    // ==================== Get Card ====================

    @Override
    public CardResponse getCard(final UUID cardId, final UUID userId) {
        Objects.requireNonNull(cardId, "Card ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("event={} Getting card: cardId={}, userId={}", LogEvent.START, cardId, userId);

        final var card = getCardWithOwnershipCheck(cardId, userId);

        return this.cardMapper.toResponse(card);
    }

    @Override
    public Page<CardResponse> getCardsByDeck(final UUID deckId, final UUID userId, final Pageable pageable) {
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(pageable, "Pageable cannot be null");

        log.info("event={} Getting cards by deck: deckId={}, userId={}", LogEvent.START, deckId, userId);

        // Validate deck ownership
        getDeckWithOwnershipCheck(deckId, userId);

        // Get cards
        final var cards = this.cardRepository.findByDeckIdAndDeletedAtIsNull(deckId, pageable);

        return cards.map(this.cardMapper::toResponse);
    }

    // ==================== Helper Methods ====================

    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> {
                    log.warn("event={} Deck not found or access denied: deckId={}, userId={}",
                            LogEvent.EX_RESOURCE_NOT_FOUND, deckId, userId);
                    return new ResourceNotFoundException(
                            "DECK_002",
                            getMessage("error.deck.not.found", deckId));
                });
    }

    private Card getCardWithOwnershipCheck(final UUID cardId, final UUID userId) {
        return this.cardRepository.findById(cardId)
                .filter(card -> !card.isDeleted())
                .filter(card -> (card.getDeck() != null) && (card.getDeck().getUser() != null)
                        && card.getDeck().getUser().getId().equals(userId))
                .orElseThrow(() -> {
                    log.warn("event={} Card not found or access denied: cardId={}, userId={}",
                            LogEvent.EX_RESOURCE_NOT_FOUND, cardId, userId);
                    return new ResourceNotFoundException(
                            "CARD_001",
                            getMessage("error.card.not.found", cardId));
                });
    }

    private User getUser(final UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("event={} User not found: userId={}", LogEvent.EX_RESOURCE_NOT_FOUND, userId);
                    return new ResourceNotFoundException(
                            "USER_001",
                            getMessage("error.user.not.found", userId));
                });
    }

    private void validateCardContent(final String front, final String back) {
        if (StringUtils.isBlank(front)) {
            throw new com.repeatwise.exception.ValidationException(
                    "CARD_002",
                    getMessage("error.card.front.required"));
        }
        if (StringUtils.isBlank(back)) {
            throw new com.repeatwise.exception.ValidationException(
                    "CARD_003",
                    getMessage("error.card.back.required"));
        }
        if (front.length() > 5000) {
            throw new com.repeatwise.exception.ValidationException(
                    "CARD_004",
                    getMessage("error.card.front.size"));
        }
        if (back.length() > 5000) {
            throw new com.repeatwise.exception.ValidationException(
                    "CARD_005",
                    getMessage("error.card.back.size"));
        }
    }

    private Card buildCard(final CreateCardRequest request, final Deck deck) {
        final var card = this.cardMapper.toEntity(request);
        card.setDeck(deck);
        return card;
    }

    private void updateCardFields(final Card card, final UpdateCardRequest request) {
        this.cardMapper.updateEntity(request, card);
    }

    private void initializeCardBoxPosition(final Card card, final User user) {
        final var position = CardBoxPosition.createDefault(card, user);
        this.cardBoxPositionRepository.save(position);
    }

    private CardResponse buildCardResponseWithSrs(final Card card, final UUID userId) {
        final var response = this.cardMapper.toResponse(card);

        // Try to include SRS fields if CardBoxPosition exists
        this.cardBoxPositionRepository.findByUserIdAndCardId(userId, card.getId())
                .ifPresent(position -> {
                    response.setCurrentBox(position.getCurrentBox());
                    response.setDueDate(position.getDueDate());
                    response.setReviewCount(position.getReviewCount());
                    response.setLapseCount(position.getLapseCount());
                    response.setLastReviewedAt(position.getLastReviewedAt());
                });

        return response;
    }

}
