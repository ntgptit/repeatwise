package com.repeatwise.service.impl;

import com.repeatwise.dto.request.review.GetDueCardsRequest;
import com.repeatwise.dto.request.review.ReviewSubmitRequest;
import com.repeatwise.dto.request.review.StartCramSessionRequest;
import com.repeatwise.dto.response.review.ReviewResultResponse;
import com.repeatwise.dto.response.review.ReviewSessionResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.CardRating;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.ReviewLog;
import com.repeatwise.entity.SrsSettings;
import com.repeatwise.entity.User;
import com.repeatwise.entity.enums.ForgottenCardAction;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.ReviewLogRepository;
import com.repeatwise.repository.SrsSettingsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IReviewService;
import com.repeatwise.util.SrsBoxIntervalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of Review Service
 *
 * Requirements:
 * - UC-023: Review Cards (SRS)
 * - UC-024: Rate Card
 * - UC-025: Undo Review
 * - UC-026: Skip Card
 * - UC-029: Cram Mode
 * - UC-030: Random Mode
 *
 * Business Rules:
 * - BR-REV-01: Due card = due_date <= today AND not soft-deleted
 * - BR-REV-02: Respect daily limits from SRS settings
 * - BR-REV-03: Order by due_date ASC, current_box ASC (normal mode)
 * - BR-REV-04: Batch size capped (e.g., 200)
 * - BR-CRAM-01: Cram mode ignores due_date
 * - BR-CRAM-02: Cram mode shuffles cards randomly
 * - BR-CRAM-03: Cram mode limit: 500 cards
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements IReviewService {

    private static final int BATCH_SIZE = 200;
    private static final int CRAM_LIMIT = 500;
    private static final int UNDO_WINDOW_SECONDS = 120; // 2 minutes

    private final CardRepository cardRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final DeckRepository deckRepository;
    private final FolderRepository folderRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final SrsSettingsRepository srsSettingsRepository;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    // ==================== UC-023: Start Review Session ====================

    @Override
    @Transactional
    public ReviewSessionResponse startReviewSession(final GetDueCardsRequest request, final UUID userId) {
        Objects.requireNonNull(request, "GetDueCardsRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Starting review session: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), userId);

        // Step 1: Get user and SRS settings
        final User user = getUser(userId);
        final SrsSettings settings = getSrsSettings(userId);

        // Step 2: Check daily limit
        checkDailyLimit(userId, settings);

        // Step 3: Query due cards based on scope
        final List<CardBoxPosition> dueCards = queryDueCards(request, userId, settings);

        // Step 4: Check if there are due cards
        if (dueCards.isEmpty()) {
            log.info("event={} No due cards found: userId={}", LogEvent.SUCCESS, userId);
            return ReviewSessionResponse.builder()
                .sessionId(UUID.randomUUID())
                .totalCards(0)
                .remaining(0)
                .build();
        }

        // Step 5: Create session
        final UUID sessionId = UUID.randomUUID();

        // Step 6: Get first card
        final CardBoxPosition firstPosition = dueCards.get(0);
        final ReviewSessionResponse.CardInfo firstCard = buildCardInfo(firstPosition);

        log.info("event={} Review session started: sessionId={}, totalCards={}, userId={}",
            LogEvent.SUCCESS, sessionId, dueCards.size(), userId);

        return ReviewSessionResponse.builder()
            .sessionId(sessionId)
            .sessionType("NORMAL")
            .totalCards(dueCards.size())
            .remaining(dueCards.size() - 1)
            .firstCard(firstCard)
            .build();
    }

    // ==================== UC-029: Start Cram Session ====================

    @Override
    @Transactional
    public ReviewSessionResponse startCramSession(final StartCramSessionRequest request, final UUID userId) {
        Objects.requireNonNull(request, "StartCramSessionRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Starting cram session: scopeType={}, scopeId={}, applyToSrs={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), request.getApplyToSrs(), userId);

        // Step 1: Get user and SRS settings (for daily limit check if applyToSrs)
        final User user = getUser(userId);
        final SrsSettings settings = getSrsSettings(userId);

        // Step 2: Check daily limit if applyToSrs is enabled
        if (Boolean.TRUE.equals(request.getApplyToSrs())) {
            checkDailyLimit(userId, settings);
        }

        // Step 3: Query all cards from scope (ignoring due_date)
        final List<CardBoxPosition> cards = queryCramCards(request, userId);

        // Step 4: Apply filters
        final List<CardBoxPosition> filteredCards = applyCramFilters(cards, request.getFilters());

        // Step 5: Limit to CRAM_LIMIT
        final List<CardBoxPosition> limitedCards = filteredCards.stream()
            .limit(CRAM_LIMIT)
            .collect(Collectors.toList());

        // Step 6: Check if there are cards
        if (limitedCards.isEmpty()) {
            log.info("event={} No cards available for cram session: userId={}", LogEvent.SUCCESS, userId);
            return ReviewSessionResponse.builder()
                .sessionId(UUID.randomUUID())
                .sessionType("CRAM")
                .applyToSrs(request.getApplyToSrs())
                .totalCards(0)
                .remaining(0)
                .build();
        }

        // Step 7: Create session
        final UUID sessionId = UUID.randomUUID();

        // Step 8: Get first card
        final CardBoxPosition firstPosition = limitedCards.get(0);
        final ReviewSessionResponse.CardInfo firstCard = buildCardInfo(firstPosition);

        log.info("event={} Cram session started: sessionId={}, totalCards={}, userId={}",
            LogEvent.SUCCESS, sessionId, limitedCards.size(), userId);

        return ReviewSessionResponse.builder()
            .sessionId(sessionId)
            .sessionType("CRAM")
            .applyToSrs(request.getApplyToSrs())
            .totalCards(limitedCards.size())
            .remaining(limitedCards.size() - 1)
            .firstCard(firstCard)
            .build();
    }

    // ==================== UC-030: Start Random Session ====================

    @Override
    @Transactional
    public ReviewSessionResponse startRandomSession(final GetDueCardsRequest request, final UUID userId) {
        Objects.requireNonNull(request, "GetDueCardsRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Starting random session: scopeType={}, scopeId={}, userId={}",
            LogEvent.START, request.getScopeType(), request.getScopeId(), userId);

        // Step 1: Get user and SRS settings
        final User user = getUser(userId);
        final SrsSettings settings = getSrsSettings(userId);

        // Step 2: Check daily limit
        checkDailyLimit(userId, settings);

        // Step 3: Query due cards randomized
        final List<CardBoxPosition> dueCards = queryDueCardsRandom(request, userId, settings);

        // Step 4: Check if there are due cards
        if (dueCards.isEmpty()) {
            log.info("event={} No due cards found for random session: userId={}", LogEvent.SUCCESS, userId);
            return ReviewSessionResponse.builder()
                .sessionId(UUID.randomUUID())
                .sessionType("RANDOM")
                .totalCards(0)
                .remaining(0)
                .build();
        }

        // Step 5: Create session
        final UUID sessionId = UUID.randomUUID();

        // Step 6: Get first card
        final CardBoxPosition firstPosition = dueCards.get(0);
        final ReviewSessionResponse.CardInfo firstCard = buildCardInfo(firstPosition);

        log.info("event={} Random session started: sessionId={}, totalCards={}, userId={}",
            LogEvent.SUCCESS, sessionId, dueCards.size(), userId);

        return ReviewSessionResponse.builder()
            .sessionId(sessionId)
            .sessionType("RANDOM")
            .totalCards(dueCards.size())
            .remaining(dueCards.size() - 1)
            .firstCard(firstCard)
            .build();
    }

    // ==================== UC-024: Rate Card ====================

    @Override
    @Transactional
    public ReviewResultResponse rateCard(final UUID sessionId, final ReviewSubmitRequest request, final UUID userId) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(request, "ReviewSubmitRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Rating card: sessionId={}, cardId={}, rating={}, userId={}",
            LogEvent.START, sessionId, request.getCardId(), request.getRating(), userId);

        // Step 1: Get user and SRS settings
        final User user = getUser(userId);
        final SrsSettings settings = getSrsSettings(userId);

        // Step 2: Get card box position
        final CardBoxPosition position = getCardBoxPosition(request.getCardId(), userId);

        // Step 3: Apply SRS algorithm
        final int previousBox = position.getCurrentBox();
        final int newBox = calculateNewBox(previousBox, request.getRating(), settings);
        final int intervalDays = SrsBoxIntervalUtil.getIntervalDays(newBox);
        final LocalDate newDueDate = SrsBoxIntervalUtil.calculateDueDate(newBox);

        // Step 4: Update card box position
        position.setCurrentBox(newBox);
        position.setIntervalDays(intervalDays);
        position.setDueDate(newDueDate);
        position.setLastReviewedAt(Instant.now());
        position.recordReview();

        if (request.getRating() == CardRating.AGAIN) {
            position.recordLapse();
        }

        // Step 5: Create review log
        final ReviewLog reviewLog = ReviewLog.create(
            position.getCard(),
            user,
            request.getRating(),
            previousBox,
            newBox,
            intervalDays
        );
        reviewLogRepository.save(reviewLog);

        // Step 6: Save position
        cardBoxPositionRepository.save(position);

        // Step 7: Get next card
        final List<CardBoxPosition> remainingCards = queryDueCardsForSession(userId, settings);
        final ReviewResultResponse.Progress progress = ReviewResultResponse.Progress.builder()
            .completed(1)
            .total(remainingCards.size() + 1)
            .build();

        if (remainingCards.isEmpty()) {
            log.info("event={} Session completed: sessionId={}, userId={}", LogEvent.SUCCESS, sessionId, userId);
            return ReviewResultResponse.builder()
                .nextCard(null)
                .remaining(0)
                .progress(progress)
                .completed(true)
                .build();
        }

        final CardBoxPosition nextPosition = remainingCards.get(0);
        final ReviewSessionResponse.CardInfo nextCard = buildCardInfo(nextPosition);

        log.info("event={} Card rated successfully: sessionId={}, cardId={}, userId={}",
            LogEvent.SUCCESS, sessionId, request.getCardId(), userId);

        return ReviewResultResponse.builder()
            .nextCard(nextCard)
            .remaining(remainingCards.size() - 1)
            .progress(progress)
            .completed(false)
            .build();
    }

    // ==================== UC-025: Undo Review ====================

    @Override
    @Transactional
    public ReviewSessionResponse.CardInfo undoReview(final UUID sessionId, final UUID userId) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Undoing review: sessionId={}, userId={}", LogEvent.START, sessionId, userId);

        // Step 1: Get last review log
        final ReviewLog lastLog = getLastReviewLog(sessionId, userId);

        // Step 2: Check undo window
        checkUndoWindow(lastLog);

        // Step 3: Get card box position
        final CardBoxPosition position = getCardBoxPosition(lastLog.getCard().getId(), userId);

        // Step 4: Restore previous state
        position.setCurrentBox(lastLog.getPreviousBox());
        final int intervalDays = SrsBoxIntervalUtil.getIntervalDays(lastLog.getPreviousBox());
        position.setIntervalDays(intervalDays);
        position.setDueDate(SrsBoxIntervalUtil.calculateDueDate(lastLog.getPreviousBox()));
        position.setReviewCount(Math.max(0, position.getReviewCount() - 1));

        // Step 5: Mark review log as undone (soft delete)
        // Note: For MVP, we'll keep the log but mark it
        reviewLogRepository.delete(lastLog);

        // Step 6: Save position
        cardBoxPositionRepository.save(position);

        log.info("event={} Review undone successfully: sessionId={}, cardId={}, userId={}",
            LogEvent.SUCCESS, sessionId, lastLog.getCard().getId(), userId);

        return buildCardInfo(position);
    }

    // ==================== UC-026: Skip Card ====================

    @Override
    public ReviewSessionResponse.CardInfo skipCard(final UUID sessionId, final UUID cardId, final UUID userId) {
        Objects.requireNonNull(sessionId, "Session ID cannot be null");
        Objects.requireNonNull(cardId, "Card ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Skipping card: sessionId={}, cardId={}, userId={}",
            LogEvent.START, sessionId, cardId, userId);

        // Step 1: Get user and SRS settings
        final User user = getUser(userId);
        final SrsSettings settings = getSrsSettings(userId);

        // Step 2: Get next card (skip current card)
        final List<CardBoxPosition> remainingCards = queryDueCardsForSession(userId, settings);
        final CardBoxPosition nextPosition = remainingCards.stream()
            .filter(pos -> !pos.getCard().getId().equals(cardId))
            .findFirst()
            .orElse(null);

        if (nextPosition == null) {
            log.info("event={} No more cards after skip: sessionId={}, userId={}", LogEvent.SUCCESS, sessionId, userId);
            return null;
        }

        log.info("event={} Card skipped: sessionId={}, cardId={}, userId={}",
            LogEvent.SUCCESS, sessionId, cardId, userId);

        return buildCardInfo(nextPosition);
    }

    // ==================== Helper Methods ====================

    private List<CardBoxPosition> queryDueCards(
            final GetDueCardsRequest request,
            final UUID userId,
            final SrsSettings settings) {

        final LocalDate today = LocalDate.now();
        final Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        final List<CardBoxPosition> positions;

        if (request.getScopeType() == GetDueCardsRequest.ScopeType.DECK) {
            // Validate deck ownership
            getDeckWithOwnershipCheck(request.getScopeId(), userId);
            positions = cardBoxPositionRepository.findDueCardsByUserIdAndDeckId(
                userId, request.getScopeId(), today, pageable);
        } else {
            // Folder scope - get all decks in folder recursively
            final Folder folder = getFolderWithOwnershipCheck(request.getScopeId(), userId);
            final List<Deck> decks = getDecksInFolderRecursive(folder, userId);
            
            if (decks.isEmpty()) {
                return Collections.emptyList();
            }

            final List<UUID> deckIds = decks.stream()
                .map(Deck::getId)
                .collect(Collectors.toList());

            positions = cardBoxPositionRepository.findDueCardsByUserIdAndDeckIds(
                userId, deckIds, today, pageable);
        }

        return positions;
    }

    private List<CardBoxPosition> queryDueCardsForSession(final UUID userId, final SrsSettings settings) {
        final LocalDate today = LocalDate.now();
        final Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        return cardBoxPositionRepository.findDueCardsByUserId(userId, today, pageable);
    }

    private List<CardBoxPosition> queryCramCards(final StartCramSessionRequest request, final UUID userId) {
        final Pageable pageable = PageRequest.of(0, CRAM_LIMIT);
        final List<CardBoxPosition> positions;

        if (request.getScopeType() == StartCramSessionRequest.ScopeType.DECK) {
            // Validate deck ownership
            getDeckWithOwnershipCheck(request.getScopeId(), userId);
            positions = cardBoxPositionRepository.findAllCardsByUserIdAndDeckId(
                userId, request.getScopeId(), pageable);
        } else {
            // Folder scope - get all decks in folder recursively
            final Folder folder = getFolderWithOwnershipCheck(request.getScopeId(), userId);
            final List<Deck> decks = getDecksInFolderRecursive(folder, userId);
            
            if (decks.isEmpty()) {
                return Collections.emptyList();
            }

            final List<UUID> deckIds = decks.stream()
                .map(Deck::getId)
                .collect(Collectors.toList());

            positions = cardBoxPositionRepository.findAllCardsByUserIdAndDeckIds(
                userId, deckIds, pageable);
        }

        return positions;
    }

    private List<CardBoxPosition> applyCramFilters(
            final List<CardBoxPosition> cards,
            final StartCramSessionRequest.Filters filters) {

        if (filters == null) {
            return cards;
        }

        return cards.stream()
            .filter(pos -> {
                // Box range filter
                if (filters.getBoxRange() != null) {
                    final int box = pos.getCurrentBox();
                    final Integer min = filters.getBoxRange().getMin();
                    final Integer max = filters.getBoxRange().getMax();
                    if (min != null && box < min) {
                        return false;
                    }
                    if (max != null && box > max) {
                        return false;
                    }
                }

                // Include learned filter
                if (!Boolean.TRUE.equals(filters.getIncludeLearned())) {
                    // Exclude high boxes (Box 5+)
                    if (pos.getCurrentBox() >= 5) {
                        return false;
                    }
                }

                return true;
            })
            .collect(Collectors.toList());
    }

    private List<CardBoxPosition> queryDueCardsRandom(
            final GetDueCardsRequest request,
            final UUID userId,
            final SrsSettings settings) {

        final LocalDate today = LocalDate.now();
        final Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        final List<CardBoxPosition> positions;

        if (request.getScopeType() == GetDueCardsRequest.ScopeType.DECK) {
            // Validate deck ownership
            getDeckWithOwnershipCheck(request.getScopeId(), userId);
            positions = cardBoxPositionRepository.findDueCardsByUserIdAndDeckIdRandom(
                userId, request.getScopeId(), today, pageable);
        } else {
            // Folder scope - get all decks in folder recursively
            final Folder folder = getFolderWithOwnershipCheck(request.getScopeId(), userId);
            final List<Deck> decks = getDecksInFolderRecursive(folder, userId);
            
            if (decks.isEmpty()) {
                return Collections.emptyList();
            }

            final List<UUID> deckIds = decks.stream()
                .map(Deck::getId)
                .collect(Collectors.toList());

            positions = cardBoxPositionRepository.findDueCardsByUserIdAndDeckIdsRandom(
                userId, deckIds, today, pageable);
        }

        return positions;
    }

    private int calculateNewBox(final int currentBox, final CardRating rating, final SrsSettings settings) {
        switch (rating) {
            case AGAIN:
                return handleAgainRating(currentBox, settings);
            case HARD:
                return currentBox; // Keep same box
            case GOOD:
                return Math.min(currentBox + 1, settings.getTotalBoxes());
            case EASY:
                return Math.min(currentBox + 2, settings.getTotalBoxes());
            default:
                throw new ValidationException("RATING_001", getMessage("error.reviewlog.rating.required"));
        }
    }

    private int handleAgainRating(final int currentBox, final SrsSettings settings) {
        final ForgottenCardAction action = settings.getForgottenCardAction();
        switch (action) {
            case MOVE_TO_BOX_1:
                return 1;
            case MOVE_DOWN_N_BOXES:
                return Math.max(1, currentBox - settings.getMoveDownBoxes());
            case REPEAT_IN_SESSION:
                return currentBox;
            default:
                return 1;
        }
    }

    private ReviewSessionResponse.CardInfo buildCardInfo(final CardBoxPosition position) {
        final Card card = position.getCard();
        return ReviewSessionResponse.CardInfo.builder()
            .id(card.getId())
            .deckId(card.getDeck().getId())
            .front(card.getFront())
            .back(card.getBack())
            .currentBox(position.getCurrentBox())
            .dueDate(position.getDueDate())
            .reviewCount(position.getReviewCount())
            .lapseCount(position.getLapseCount())
            .lastReviewedAt(position.getLastReviewedAt() != null
                ? position.getLastReviewedAt()
                : null)
            .build();
    }

    private void checkDailyLimit(final UUID userId, final SrsSettings settings) {
        final Instant startOfDay = Instant.now().atZone(java.time.ZoneId.systemDefault())
            .toLocalDate().atStartOfDay()
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant();
        final Instant endOfDay = startOfDay.plusSeconds(86400); // 24 hours later

        final long todayCount = reviewLogRepository.countReviewsToday(userId, startOfDay, endOfDay);

        if (todayCount >= settings.getMaxReviewsPerDay()) {
            throw new ValidationException(
                "REV_001",
                getMessage("error.review.daily.limit.reached", settings.getMaxReviewsPerDay())
            );
        }
    }

    private void checkUndoWindow(final ReviewLog reviewLog) {
        final long elapsedSeconds = (Instant.now().toEpochMilli() - reviewLog.getReviewedAt().toEpochMilli()) / 1000;
        if (elapsedSeconds > UNDO_WINDOW_SECONDS) {
            throw new ValidationException(
                "REV_002",
                getMessage("error.review.undo.window.expired", UNDO_WINDOW_SECONDS)
            );
        }
    }

    private ReviewLog getLastReviewLog(final UUID sessionId, final UUID userId) {
        // Note: In MVP, we track by userId only since sessionId is not stored
        // For production, we'd add sessionId to ReviewLog entity
        final List<ReviewLog> logs = reviewLogRepository.findLastReviewLogsByUserId(userId);
        return logs.stream()
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(
                "REV_003",
                getMessage("error.review.nothing.to.undo")
            ));
    }

    private CardBoxPosition getCardBoxPosition(final UUID cardId, final UUID userId) {
        return cardBoxPositionRepository.findByUserIdAndCardId(userId, cardId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "CARD_006",
                getMessage("error.card.not.found", cardId)
            ));
    }

    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return deckRepository.findByIdAndUserId(deckId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "DECK_002",
                getMessage("error.deck.not.found", deckId)
            ));
    }

    private Folder getFolderWithOwnershipCheck(final UUID folderId, final UUID userId) {
        return folderRepository.findByIdAndUserId(folderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "FOLDER_002",
                getMessage("error.folder.not.found", folderId)
            ));
    }

    private List<Deck> getDecksInFolderRecursive(final Folder folder, final UUID userId) {
        // Get all decks in folder and descendants
        final List<Deck> decks = new ArrayList<>();
        collectDecksRecursive(folder, userId, decks);
        return decks;
    }

    private void collectDecksRecursive(final Folder folder, final UUID userId, final List<Deck> decks) {
        // Get decks in this folder
        final List<Deck> folderDecks = deckRepository.findByFolderId(folder.getId());
        decks.addAll(folderDecks);

        // Get child folders and recurse
        final List<Folder> children = folderRepository.findChildrenByParentId(userId, folder.getId());
        for (final Folder child : children) {
            collectDecksRecursive(child, userId, decks);
        }
    }

    private User getUser(final UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "USER_001",
                getMessage("error.user.not.found", userId)
            ));
    }

    private SrsSettings getSrsSettings(final UUID userId) {
        return srsSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "SRS_001",
                getMessage("error.srs.settings.not.found")
            ));
    }

    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}

