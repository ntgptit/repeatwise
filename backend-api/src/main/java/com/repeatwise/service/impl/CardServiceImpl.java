package com.repeatwise.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.card.CreateCardRequest;
import com.repeatwise.dto.request.card.UpdateCardRequest;
import com.repeatwise.dto.response.card.CardResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.CardMapper;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.service.CardService;
import com.repeatwise.util.TextUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CardService implementation hỗ trợ UC-018 tới UC-020.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final DeckRepository deckRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final CardMapper cardMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public CardResponse createCard(CreateCardRequest request, UUID userId) {
        log.debug("User {} requests card creation in deck {}", userId, request.getDeckId());

        final var deck = getDeckOrThrow(request.getDeckId(), userId);
        var card = buildCardForCreation(request, deck);

        card = this.cardRepository.save(card);
        initializePosition(card, deck.getUser());

        log.info("Card {} created in deck {} by user {}", card.getId(), deck.getId(), userId);
        return this.cardMapper.toResponse(card);
    }

    @Override
    @Transactional
    public CardResponse updateCard(UUID cardId, UpdateCardRequest request, UUID userId) {
        log.debug("User {} updates card {}", userId, cardId);

        final var card = getActiveCardOrThrow(cardId, userId);
        var changed = false;

        if (request.getFront() != null) {
            final var newFront = normalizeFront(request.getFront());
            if (!Objects.equals(newFront, card.getFront())) {
                card.setFront(newFront);
                changed = true;
            }
        }

        if (request.getBack() != null) {
            final var newBack = normalizeBack(request.getBack());
            if (!Objects.equals(newBack, card.getBack())) {
                card.setBack(newBack);
                changed = true;
            }
        }

        if (!changed) {
            log.debug("No changes detected for card {}", cardId);
            return this.cardMapper.toResponse(card);
        }

        final var now = LocalDateTime.now();
        card.setUpdatedAt(now);

        final var savedCard = this.cardRepository.save(card);
        log.info("Card {} updated by user {}", cardId, userId);
        return this.cardMapper.toResponse(savedCard);
    }

    @Override
    @Transactional
    public CardDeletionResult deleteCard(UUID cardId, UUID userId) {
        log.debug("User {} deletes card {}", userId, cardId);

        final var card = this.cardRepository.findActiveWithPositionsByIdAndUserId(cardId, userId)
                .orElseGet(() -> handleCardNotFoundOrDeleted(cardId, userId));

        final var now = LocalDateTime.now();
        markCardDeleted(card, now);
        this.cardRepository.save(card);

        final var locale = LocaleContextHolder.getLocale();
        final var message = this.messageSource.getMessage("success.card.deleted", null, locale);

        log.info("Card {} soft deleted by user {}", cardId, userId);
        return new CardDeletionResult(card.getId(), now, message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByDeck(UUID deckId, UUID userId) {
        log.debug("User {} requests cards for deck {}", userId, deckId);

        final var deck = getDeckOrThrow(deckId, userId);
        return this.cardRepository.findActiveByDeckIdAndUserId(deck.getId(), userId)
                .stream()
                .map(this.cardMapper::toResponse)
                .toList();
    }

    private Card buildCardForCreation(CreateCardRequest request, Deck deck) {
        final var card = this.cardMapper.toEntity(request);
        card.setDeck(deck);
        card.setFront(normalizeFront(request.getFront()));
        card.setBack(normalizeBack(request.getBack()));
        return card;
    }

    private void initializePosition(Card card, User owner) {
        final CardBoxPosition position = CardBoxPosition.createNew(card, owner);
        card.getCardBoxPositions().add(position);
        this.cardBoxPositionRepository.save(position);
    }

    private Deck getDeckOrThrow(UUID deckId, UUID userId) {
        return this.deckRepository.findByIdAndUserId(deckId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.DECK_NOT_FOUND, deckId));
    }

    private Card getActiveCardOrThrow(UUID cardId, UUID userId) {
        return this.cardRepository.findActiveByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new RepeatWiseException(RepeatWiseError.CARD_NOT_FOUND, cardId));
    }

    private Card handleCardNotFoundOrDeleted(UUID cardId, UUID userId) {
        if (this.cardRepository.findDeletedByIdAndUserId(cardId, userId).isPresent()) {
            throw new RepeatWiseException(RepeatWiseError.CARD_ALREADY_DELETED, cardId);
        }
        throw new RepeatWiseException(RepeatWiseError.CARD_NOT_FOUND, cardId);
    }

    private void markCardDeleted(Card card, LocalDateTime deletedAt) {
        card.setDeletedAt(deletedAt);
        card.setUpdatedAt(deletedAt);
        card.getCardBoxPositions()
                .forEach(position -> {
                    position.setDeletedAt(deletedAt);
                    position.setUpdatedAt(deletedAt);
                });
    }

    private String normalizeFront(String value) {
        return normalizeContent(value, RepeatWiseError.CARD_FRONT_REQUIRED);
    }

    private String normalizeBack(String value) {
        return normalizeContent(value, RepeatWiseError.CARD_BACK_REQUIRED);
    }

    private String normalizeContent(String value, RepeatWiseError error) {
        final var trimmed = TextUtils.trimToNull(value);
        if (trimmed == null) {
            throw new RepeatWiseException(error);
        }
        return trimmed;
    }
}

