package com.repeatwise.service.impl;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.User;
import com.repeatwise.exception.DuplicateResourceException;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.mapper.DeckMapper;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IDeckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of Deck Service
 *
 * Requirements:
 * - UC-011: Create Deck
 * - UC-012: Move Deck
 * - UC-013: Copy Deck
 * - UC-014: Delete Deck
 *
 * Business Rules:
 * - BR-031: Deck naming (1-100 chars, trim whitespace)
 * - BR-032: Deck can be at root level (folder_id nullable)
 * - BR-033: Name unique within same folder
 * - BR-034: Initial state (0 cards)
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
public class DeckServiceImpl implements IDeckService {

    // ==================== Dependencies ====================

    private final DeckRepository deckRepository;
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final DeckMapper deckMapper;
    private final MessageSource messageSource;

    // ==================== UC-011: Create Deck ====================

    /**
     * Create a new deck
     *
     * Requirements:
     * - UC-011: Create Deck
     * - BR-031: Deck naming
     * - BR-032: Deck can be at root level
     * - BR-033: Unique name within folder
     * - BR-034: Initial state
     *
     * Steps:
     * 1. Validate request (not null, name not blank)
     * 2. Get user (verify exists)
     * 3. Get folder (if specified)
     * 4. Validate name uniqueness
     * 5. Build deck entity
     * 6. Save deck
     * 7. Return response
     */
    @Transactional
    @Override
    public DeckResponse createDeck(final CreateDeckRequest request, final UUID userId) {
        // Guard clause: Validate request
        Objects.requireNonNull(request, "CreateDeckRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Creating deck: name={}, folderId={}, userId={}",
            request.getName(), request.getFolderId(), userId);

        // Validate name
        validateDeckName(request.getName());

        // Get user
        final User user = getUser(userId);

        // Get folder (if specified)
        final Folder folder = getFolder(request.getFolderId(), userId);

        // Validate name uniqueness
        validateNameUniqueness(request.getName(), folder, userId);

        // Build deck
        final Deck deck = buildDeck(request, user, folder);

        // Save deck
        final Deck savedDeck = deckRepository.save(deck);

        log.info("Deck created successfully: deckId={}, name={}, userId={}",
            savedDeck.getId(), savedDeck.getName(), userId);

        return deckMapper.toResponse(savedDeck);
    }

    @Override
    public List<DeckResponse> getAllDecks(final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DeckResponse getDeckById(final UUID deckId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<DeckResponse> getDecksByFolderId(final UUID folderId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DeckResponse updateDeck(final UUID deckId, final UpdateDeckRequest request, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteDeck(final UUID deckId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DeckResponse restoreDeck(final UUID deckId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void permanentlyDeleteDeck(final UUID deckId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ==================== UC-012: Move Deck ====================

    /**
     * Move deck to a different folder
     *
     * Requirements:
     * - UC-012: Move Deck
     * - BR-040: Move validation
     * - BR-041: Name conflict handling
     * - BR-042: Statistics update (handled by FolderStatsService)
     * - BR-043: Review progress preservation (automatic - no changes to cards)
     *
     * Steps:
     * 1. Validate deck exists and belongs to user
     * 2. Get current folder (old location)
     * 3. Get destination folder (new location) - validate if not null
     * 4. Validate not moving to same folder
     * 5. Validate no name conflict in destination
     * 6. Update deck.folder_id
     * 7. Save deck
     * 8. Return updated response
     *
     * Performance:
     * - Single UPDATE query (< 200ms per UC-012)
     * - No changes to cards (review progress preserved)
     */
    @Transactional
    @Override
    public DeckResponse moveDeck(final UUID deckId, final UUID newFolderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Moving deck: deckId={}, newFolderId={}, userId={}", deckId, newFolderId, userId);

        // Step 1: Get deck and validate ownership
        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Get current folder (old location)
        final Folder oldFolder = deck.getFolder();
        final UUID oldFolderId = oldFolder != null ? oldFolder.getId() : null;

        // Step 3: Get destination folder (validate if not null)
        final Folder newFolder = getFolder(newFolderId, userId);

        // Step 4: Validate not moving to same folder
        validateNotMovingToSameFolder(oldFolderId, newFolderId, deck.getName());

        // Step 5: Validate no name conflict in destination
        validateNameConflictInDestination(deck.getName(), newFolder, userId);

        // Step 6: Move deck (update folder_id)
        moveDeckToNewFolder(deck, newFolder);

        // Step 7: Save deck
        final Deck savedDeck = deckRepository.save(deck);

        log.info("Deck moved successfully: deckId={}, from folderId={}, to folderId={}, userId={}",
            deckId, oldFolderId, newFolderId, userId);

        // Step 8: Return response
        return deckMapper.toResponse(savedDeck);
    }

    // ==================== UC-013: Copy Deck ====================

    /**
     * Copy deck with all cards (Synchronous for < 100 cards)
     *
     * Requirements:
     * - UC-013: Copy Deck
     * - BR-048: Copy thresholds (< 100 cards synchronous)
     * - BR-049: Card state reset (all cards start in Box 1)
     * - BR-050: Naming convention
     *
     * Steps:
     * 1. Validate request and parameters
     * 2. Get source deck with ownership check
     * 3. Validate deck size (< 100 cards for sync in MVP)
     * 4. Get destination folder (if specified)
     * 5. Validate new name uniqueness in destination
     * 6. Create new deck
     * 7. Copy all cards
     * 8. Return new deck response
     *
     * Performance:
     * - < 100 cards: < 2 seconds (synchronous)
     * - Batch insert cards for efficiency
     *
     * Note: Async copy (>= 100 cards) NOT implemented in MVP
     */
    @Transactional
    @Override
    public DeckResponse copyDeck(final UUID deckId, final CopyDeckRequest request, final UUID userId) {
        // Step 1: Guard clauses - validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(request, "CopyDeckRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Copying deck: deckId={}, newName={}, destinationFolderId={}, userId={}",
            deckId, request.getNewName(), request.getDestinationFolderId(), userId);

        // Step 2: Get source deck with ownership check
        final Deck sourceDeck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 3: Validate deck size (< 100 cards for synchronous copy)
        validateDeckSizeForCopy(sourceDeck);

        // Step 4: Get destination folder (if specified)
        final Folder destinationFolder = getFolder(request.getDestinationFolderId(), userId);

        // Step 5: Validate new name uniqueness
        final String trimmedNewName = StringUtils.trim(request.getNewName());
        validateNameUniqueness(trimmedNewName, destinationFolder, userId);

        // Step 6: Create new deck
        final User user = getUser(userId);
        final Deck newDeck = createNewDeckFromSource(sourceDeck, trimmedNewName, destinationFolder, user);

        // Step 7: Copy all cards from source to new deck
        copyCardsToNewDeck(sourceDeck, newDeck, user);

        // Step 8: Save new deck
        final Deck savedDeck = deckRepository.save(newDeck);

        log.info("Deck copied successfully: sourceDeckId={}, newDeckId={}, cardCount={}, userId={}",
            deckId, savedDeck.getId(), savedDeck.getCardCount(), userId);

        return deckMapper.toResponse(savedDeck);
    }

    // ==================== Helper Methods (Private) ====================

    /**
     * Validate deck name is not blank after trim
     */
    private void validateDeckName(final String name) {
        if (StringUtils.isBlank(name)) {
            log.error("Deck creation failed: name is blank");
            throw new ValidationException(
                "DECK_001",
                getMessage("error.deck.name.required")
            );
        }
    }

    /**
     * Get user by ID
     */
    private User getUser(final UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("User not found: userId={}", userId);
                return new ResourceNotFoundException(
                    "USER_001",
                    getMessage("error.user.not.found", userId)
                );
            });
    }

    /**
     * Get folder by ID (if not null)
     * Returns null if folderId is null (root-level deck)
     */
    private Folder getFolder(final UUID folderId, final UUID userId) {
        if (folderId == null) {
            return null;
        }

        return folderRepository.findByIdAndUserId(folderId, userId)
            .orElseThrow(() -> {
                log.error("Folder not found: folderId={}, userId={}", folderId, userId);
                return new ResourceNotFoundException(
                    "FOLDER_002",
                    getMessage("error.folder.not.found", folderId)
                );
            });
    }

    /**
     * Validate deck name is unique within folder or root level
     */
    private void validateNameUniqueness(final String name, final Folder folder, final UUID userId) {
        final String trimmedName = StringUtils.trim(name);

        final boolean nameExists = isNameExistsInFolder(trimmedName, folder, userId);

        if (nameExists) {
            log.warn("Deck name already exists: name={}, folderId={}, userId={}",
                trimmedName,
                folder != null ? folder.getId() : null,
                userId);

            throw new DuplicateResourceException(
                "DECK_002",
                getMessage("error.deck.name.exists", trimmedName)
            );
        }
    }

    /**
     * Check if deck name exists in folder or root level
     */
    private boolean isNameExistsInFolder(final String name, final Folder folder, final UUID userId) {
        if (folder == null) {
            // Root level
            return deckRepository.existsByUserIdAndRootAndName(userId, name);
        }

        // Folder level
        return deckRepository.existsByFolderIdAndName(folder.getId(), name);
    }

    /**
     * Build deck entity from request
     */
    private Deck buildDeck(final CreateDeckRequest request, final User user, final Folder folder) {
        final String trimmedName = StringUtils.trim(request.getName());
        final String trimmedDescription = StringUtils.trim(request.getDescription());

        return Deck.builder()
            .name(trimmedName)
            .description(trimmedDescription)
            .user(user)
            .folder(folder)
            .build();
    }

    /**
     * Get deck with ownership check
     * Used in move, update, delete operations
     */
    private Deck getDeckWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return deckRepository.findByIdAndUserId(deckId, userId)
            .orElseThrow(() -> {
                log.error("Deck not found or access denied: deckId={}, userId={}", deckId, userId);
                return new ResourceNotFoundException(
                    "DECK_003",
                    getMessage("error.deck.not.found", deckId)
                );
            });
    }

    /**
     * Validate not moving to same folder
     * UC-012: BR-040
     */
    private void validateNotMovingToSameFolder(final UUID oldFolderId, final UUID newFolderId, final String deckName) {
        // Both null = root level
        if (oldFolderId == null && newFolderId == null) {
            log.warn("Deck move failed: already at root level - deckName={}", deckName);
            throw new ValidationException(
                "DECK_004",
                getMessage("error.deck.move.same.folder", deckName)
            );
        }

        // Both same folder ID
        if (oldFolderId != null && oldFolderId.equals(newFolderId)) {
            log.warn("Deck move failed: already in folder - deckName={}, folderId={}", deckName, oldFolderId);
            throw new ValidationException(
                "DECK_005",
                getMessage("error.deck.move.same.folder", deckName)
            );
        }
    }

    /**
     * Validate no name conflict in destination folder
     * UC-012: BR-041
     */
    private void validateNameConflictInDestination(final String deckName, final Folder newFolder, final UUID userId) {
        final boolean nameExists = isNameExistsInFolder(deckName, newFolder, userId);

        if (nameExists) {
            final String locationName = newFolder != null ? newFolder.getName() : "Root";
            log.warn("Deck move failed: name conflict - deckName={}, destination={}",
                deckName, locationName);

            throw new DuplicateResourceException(
                "DECK_006",
                getMessage("error.deck.move.name.conflict", deckName, locationName)
            );
        }
    }

    /**
     * Move deck to new folder (update relationship)
     * Uses entity business method for proper bidirectional update
     */
    private void moveDeckToNewFolder(final Deck deck, final Folder newFolder) {
        // Use entity business method for proper relationship management
        deck.moveTo(newFolder);
    }

    /**
     * Validate deck size for copy operation
     * UC-013: BR-048 - < 100 cards for synchronous copy
     */
    private void validateDeckSizeForCopy(final Deck deck) {
        final long cardCount = deck.getCardCount();

        if (cardCount >= 100) {
            log.warn("Deck too large for synchronous copy: deckId={}, cardCount={}, maximum=99",
                deck.getId(), cardCount);

            throw new ValidationException(
                "DECK_007",
                getMessage("error.deck.copy.too.large", cardCount, 99)
            );
        }
    }

    /**
     * Create new deck from source deck
     * UC-013: Step 6
     */
    private Deck createNewDeckFromSource(final Deck sourceDeck,
                                         final String newName,
                                         final Folder destinationFolder,
                                         final User user) {
        return Deck.builder()
            .name(newName)
            .description(sourceDeck.getDescription())
            .user(user)
            .folder(destinationFolder)
            .build();
    }

    /**
     * Copy all cards from source deck to new deck
     * UC-013: Step 7 - BR-049: Reset card progress
     */
    private void copyCardsToNewDeck(final Deck sourceDeck, final Deck newDeck, final User user) {
        // Note: In MVP, cards will be copied without SRS state
        // SRS state (card_box_position) will be implemented in later phase
        // For now, we just copy the card content (front/back)

        sourceDeck.getCards().forEach(sourceCard -> {
            final Card newCard = Card.builder()
                .front(sourceCard.getFront())
                .back(sourceCard.getBack())
                .user(user)
                .deck(newDeck)
                .build();

            newDeck.addCard(newCard);
        });

        log.debug("Copied {} cards to new deck", sourceDeck.getCards().size());
    }

    /**
     * Get internationalized message
     */
    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
