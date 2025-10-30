package com.repeatwise.service.impl;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckDeleteResponse;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.entity.Card;
import com.repeatwise.entity.Deck;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.User;
import com.repeatwise.exception.DuplicateResourceException;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.mapper.DeckMapper;
import com.repeatwise.repository.CardRepository;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IDeckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.time.Instant;

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
    private final CardRepository cardRepository;
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

        log.info("event={} Creating deck: name={}, folderId={}, userId={}",
            LogEvent.DECK_CREATE_START, request.getName(), request.getFolderId(), userId);

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

        log.info("event={} Deck created successfully: deckId={}, name={}, userId={}",
            LogEvent.DECK_CREATE_SUCCESS, savedDeck.getId(), savedDeck.getName(), userId);

        return deckMapper.toResponse(savedDeck);
    }

    @Override
    public List<DeckResponse> getAllDecks(final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting all decks: userId={}", LogEvent.START, userId);

        final List<Deck> decks = deckRepository.findAll().stream()
            .filter(deck -> deck.getUser() != null && deck.getUser().getId().equals(userId))
            .filter(deck -> !deck.isDeleted())
            .toList();

        return deckMapper.toResponseList(decks);
    }

    @Override
    public DeckResponse getDeckById(final UUID deckId, final UUID userId) {
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting deck: deckId={}, userId={}", LogEvent.START, deckId, userId);

        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);

        return deckMapper.toResponse(deck);
    }

    @Override
    public List<DeckResponse> getDecksByFolderId(final UUID folderId, final UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Getting decks by folder: folderId={}, userId={}", LogEvent.START, folderId, userId);

        final List<Deck> decks;
        if (folderId == null) {
            // Root level decks
            decks = deckRepository.findAll().stream()
                .filter(deck -> deck.getUser().getId().equals(userId) && deck.getFolder() == null)
                .toList();
        } else {
            // Validate folder exists and belongs to user
            getFolder(folderId, userId);
            decks = deckRepository.findByFolderId(folderId);
        }

        return deckMapper.toResponseList(decks);
    }

    @Override
    public DeckResponse updateDeck(final UUID deckId, final UpdateDeckRequest request, final UUID userId) {
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(request, "UpdateDeckRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Updating deck: deckId={}, userId={}", LogEvent.START, deckId, userId);

        // Step 1: Get deck with ownership check
        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Validate name
        validateDeckName(request.getName());

        // Step 3: Validate name uniqueness (excluding current deck)
        final String trimmedName = StringUtils.trim(request.getName());
        validateNameUniquenessForUpdate(trimmedName, deck.getFolder(), userId, deckId);

        // Step 4: Update deck fields
        updateDeckFields(deck, request);

        // Step 5: Save deck
        final Deck savedDeck = deckRepository.save(deck);

        log.info("event={} Deck updated successfully: deckId={}, name={}, userId={}",
            LogEvent.SUCCESS, deckId, savedDeck.getName(), userId);

        return deckMapper.toResponse(savedDeck);
    }

    // ==================== UC-014: Delete Deck ====================

    /**
     * Soft delete deck
     *
     * Requirements:
     * - UC-017: Delete Deck
     * - BR-052: Soft delete policy (deleted_at timestamp)
     * - BR-053: Cascade deletion to all cards
     *
     * Steps:
     * 1. Validate deck exists and belongs to user
     * 2. Get card count for logging
     * 3. Soft delete deck (set deleted_at)
     * 4. Cascade soft delete to all cards
     * 5. Save deck
     * 6. Return response with deletedAt timestamp
     *
     * Performance: < 200ms per UC-017
     */
    @Transactional
    @Override
    public DeckDeleteResponse deleteDeck(final UUID deckId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Deleting deck: deckId={}, userId={}", deckId, userId);

        // Step 1: Get deck with ownership check
        final Deck deck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 2: Validate deck is not already deleted
        if (deck.isDeleted()) {
            log.warn("event={} Deck already deleted: deckId={}, userId={}", LogEvent.EX_VALIDATION, deckId, userId);
            throw new ValidationException(
                "DECK_010",
                getMessage("error.deck.already.deleted")
            );
        }

        // Step 3: Get card count and deck name for logging
        final int cardCount = deck.getCardCount();
        final String deckName = deck.getName();

        // Step 4: Soft delete deck
        deck.softDelete();
        final Instant deletedAt = deck.getDeletedAt();

        // Step 5: Cascade soft delete to all cards
        softDeleteAllCardsInDeck(deckId);

        // Step 6: Save deck
        deckRepository.save(deck);

        log.info("Deck deleted successfully: deckId={}, name={}, cardCount={}, userId={}",
            deckId, deckName, cardCount, userId);

        // Step 7: Return response
        return DeckDeleteResponse.builder()
            .message(getMessage("deck.delete.success"))
            .deletedAt(deletedAt)
            .build();
    }

    /**
     * Restore soft-deleted deck
     *
     * Requirements:
     * - UC-014: Restore from Trash (A5)
     * - BR-055: Restoration within 30 days
     *
     * Steps:
     * 1. Get deck by ID (include deleted)
     * 2. Validate deck is actually deleted
     * 3. Restore deck (set deleted_at to null)
     * 4. Restore all cards in deck
     * 5. Return restored deck response
     *
     * Note: This method must fetch deleted decks,
     * so we can't use findByIdAndUserId which filters deleted
     */
    @Transactional
    @Override
    public DeckResponse restoreDeck(final UUID deckId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Restoring deck: deckId={}, userId={}", deckId, userId);

        // Step 1: Get deck (including deleted) with ownership check
        final Deck deck = getDeckIncludingDeletedWithOwnershipCheck(deckId, userId);

        // Step 2: Validate deck is actually deleted
        validateDeckIsDeleted(deck);

        // Step 3: Restore deck
        deck.restore();

        // Step 4: Restore all cards in deck
        restoreAllCardsInDeck(deckId);

        // Step 5: Save deck
        final Deck restoredDeck = deckRepository.save(deck);

        log.info("Deck restored successfully: deckId={}, name={}, userId={}",
            deckId, restoredDeck.getName(), userId);

        return deckMapper.toResponse(restoredDeck);
    }

    /**
     * Permanently delete deck (hard delete)
     *
     * Requirements:
     * - UC-014: Permanent Deletion (A6)
     * - BR-056: Delete after 30 days, no recovery
     *
     * Steps:
     * 1. Get deck by ID (include deleted)
     * 2. Validate deck is in trash
     * 3. Hard delete all cards (permanently)
     * 4. Hard delete deck
     * 5. Log success
     *
     * Performance: < 500ms per UC-014
     */
    @Transactional
    @Override
    public void permanentlyDeleteDeck(final UUID deckId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("Permanently deleting deck: deckId={}, userId={}", deckId, userId);

        // Step 1: Get deck (including deleted) with ownership check
        final Deck deck = getDeckIncludingDeletedWithOwnershipCheck(deckId, userId);

        // Step 2: Validate deck is in trash
        validateDeckIsDeleted(deck);

        final String deckName = deck.getName();
        final int cardCount = deck.getCardCount();

        // Step 3: Hard delete all cards (cascade delete via JPA)
        // Cards will be deleted automatically due to CascadeType.ALL and orphanRemoval

        // Step 4: Hard delete deck
        deckRepository.delete(deck);

        log.info("Deck permanently deleted: deckId={}, name={}, cardCount={}, userId={}",
            deckId, deckName, cardCount, userId);
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

        log.info("event={} Moving deck: deckId={}, newFolderId={}, userId={}", LogEvent.DECK_MOVE_START, deckId, newFolderId, userId);

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

        log.info("event={} Deck moved successfully: deckId={}, from folderId={}, to folderId={}, userId={}",
            LogEvent.DECK_MOVE_SUCCESS, deckId, oldFolderId, newFolderId, userId);

        // Step 8: Return response
        return deckMapper.toResponse(savedDeck);
    }

    // ==================== UC-013: Copy Deck ====================

    /**
     * Copy deck with all cards (Synchronous for <= 1000 cards)
     *
     * Requirements:
     * - UC-016: Copy Deck
     * - BR-DECK-COPY-01: Sync copy if <= 1000 cards
     * - BR-DECK-COPY-02: Async copy if 1001–10,000 cards
     * - BR-DECK-COPY-03: Reject > 10,000 cards
     * - BR-DECK-COPY-04: Name conflict resolved with suffix
     *
     * Steps:
     * 1. Validate request and parameters
     * 2. Get source deck with ownership check
     * 3. Validate deck size (<= 1000 cards for sync in MVP, reject > 10000)
     * 4. Get destination folder (if specified)
     * 5. Validate new name uniqueness in destination
     * 6. Create new deck
     * 7. Copy all cards
     * 8. Return new deck response
     *
     * Performance:
     * - <= 1000 cards: < 2 seconds (synchronous)
     * - Batch insert cards for efficiency
     *
     * Note: Async copy (1001-10000 cards) NOT implemented in MVP
     */
    @Transactional
    @Override
    public DeckResponse copyDeck(final UUID deckId, final CopyDeckRequest request, final UUID userId) {
        // Step 1: Guard clauses - validate parameters
        Objects.requireNonNull(deckId, "Deck ID cannot be null");
        Objects.requireNonNull(request, "CopyDeckRequest cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        log.info("event={} Copying deck: deckId={}, newName={}, destinationFolderId={}, userId={}",
            LogEvent.DECK_COPY_START, deckId, request.getNewName(), request.getDestinationFolderId(), userId);

        // Step 2: Get source deck with ownership check
        final Deck sourceDeck = getDeckWithOwnershipCheck(deckId, userId);

        // Step 3: Validate deck size (<= 1000 cards for synchronous copy, 1001-10000 for async, reject > 10000)
        validateDeckSizeForCopy(sourceDeck);

        // Step 4: Get destination folder (if specified)
        final Folder destinationFolder = getFolder(request.getDestinationFolderId(), userId);

        // Step 5: Validate new name uniqueness and resolve conflicts
        final String trimmedNewName = StringUtils.trim(request.getNewName());
        final String finalName = resolveNameConflict(trimmedNewName, destinationFolder, userId);

        // Step 6: Create new deck
        final User user = getUser(userId);
        final Deck newDeck = createNewDeckFromSource(sourceDeck, finalName, destinationFolder, user);

        // Step 7: Copy all cards from source to new deck
        copyCardsToNewDeck(sourceDeck, newDeck, user);

        // Step 8: Save new deck
        final Deck savedDeck = deckRepository.save(newDeck);

        log.info("event={} Deck copied successfully: sourceDeckId={}, newDeckId={}, cardCount={}, userId={}",
            LogEvent.DECK_COPY_SUCCESS, deckId, savedDeck.getId(), savedDeck.getCardCount(), userId);

        return deckMapper.toResponse(savedDeck);
    }

    // ==================== Helper Methods (Private) ====================

    /**
     * Validate deck name is not blank after trim
     */
    private void validateDeckName(final String name) {
        if (StringUtils.isBlank(name)) {
            log.error("event={} Deck creation failed: name is blank", LogEvent.EX_VALIDATION);
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
                log.error("event={} User not found: userId={}", LogEvent.USER_NOT_FOUND, userId);
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
                log.error("event={} Folder not found: folderId={}, userId={}", LogEvent.FOLDER_GET_NOT_FOUND_OR_UNAUTHORIZED, folderId, userId);
                return new ResourceNotFoundException(
                    "FOLDER_002",
                    getMessage("error.folder.not.found", folderId)
                );
            });
    }

    /**
     * Validate deck name is unique within folder or root level (excluding current deck)
     * Used for update operations
     */
    private void validateNameUniquenessForUpdate(final String name, final Folder folder, final UUID userId, final UUID excludeDeckId) {
        final String trimmedName = StringUtils.trim(name);

        final boolean nameExists = isNameExistsInFolderExcluding(trimmedName, folder, userId, excludeDeckId);

        if (nameExists) {
            log.warn("event={} Deck name already exists: name={}, folderId={}, userId={}, excludeDeckId={}", LogEvent.EX_DUPLICATE_RESOURCE, trimmedName,
                folder != null ? folder.getId() : null,
                userId, excludeDeckId);

            throw new DuplicateResourceException(
                "DECK_002",
                getMessage("error.deck.name.exists", trimmedName)
            );
        }
    }

    /**
     * Check if deck name exists in folder or root level (excluding specific deck)
     */
    private boolean isNameExistsInFolderExcluding(final String name, final Folder folder, final UUID userId, final UUID excludeDeckId) {
        if (folder == null) {
            // Root level
            return deckRepository.existsByUserIdAndRootAndNameExcluding(userId, name, excludeDeckId);
        }

        // Folder level
        return deckRepository.existsByFolderIdAndNameExcluding(folder.getId(), name, excludeDeckId);
    }

    /**
     * Update deck fields from request
     */
    private void updateDeckFields(final Deck deck, final UpdateDeckRequest request) {
        final String trimmedName = StringUtils.trim(request.getName());
        final String trimmedDescription = StringUtils.trim(request.getDescription());

        deck.setName(trimmedName);
        if (trimmedDescription != null) {
            deck.setDescription(trimmedDescription);
        }
    }

    /**
     * Resolve name conflict by appending "(copy)" suffix if needed
     * UC-016: BR-DECK-COPY-04 - Name conflict resolution
     */
    private String resolveNameConflict(final String name, final Folder folder, final UUID userId) {
        if (!isNameExistsInFolder(name, folder, userId)) {
            return name;
        }

        // Try with "(copy)" suffix
        String newName = name + " (copy)";
        int counter = 1;

        while (isNameExistsInFolder(newName, folder, userId)) {
            counter++;
            newName = name + " (copy " + counter + ")";
        }

        log.debug("event={} Resolved name conflict: original={}, resolved={}", LogEvent.SUCCESS, name, newName);
        return newName;
    }

    /**
     * Validate deck name is unique within folder or root level
     * Used for create operations
     */
    private void validateNameUniqueness(final String name, final Folder folder, final UUID userId) {
        final String trimmedName = StringUtils.trim(name);

        final boolean nameExists = isNameExistsInFolder(trimmedName, folder, userId);

        if (nameExists) {
            log.warn("event={} Deck name already exists: name={}, folderId={}, userId={}", LogEvent.EX_DUPLICATE_RESOURCE, trimmedName,
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
                log.error("event={} Deck not found or access denied: deckId={}, userId={}", LogEvent.EX_FORBIDDEN, deckId, userId);
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
            log.warn("event={} Deck move failed: already at root level - deckName={}", LogEvent.EX_VALIDATION, deckName);
            throw new ValidationException(
                "DECK_004",
                getMessage("error.deck.move.same.folder", deckName)
            );
        }

        // Both same folder ID
        if (oldFolderId != null && oldFolderId.equals(newFolderId)) {
            log.warn("event={} Deck move failed: already in folder - deckName={}, folderId={}", LogEvent.EX_VALIDATION, deckName, oldFolderId);
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
            log.warn("event={} Deck move failed: name conflict - deckName={}, destination={}", LogEvent.EX_DUPLICATE_RESOURCE, deckName, locationName);

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
     * UC-016: BR-DECK-COPY-01 - <= 1000 cards for synchronous copy
     */
    private void validateDeckSizeForCopy(final Deck deck) {
        final long cardCount = deck.getCardCount();

        if (cardCount > 10000) {
            log.warn("event={} Deck too large to copy: deckId={}, cardCount={}, maximum=10000", LogEvent.EX_VALIDATION,
                deck.getId(), cardCount);

            throw new ValidationException(
                "DECK_007",
                getMessage("error.deck.copy.too.large", cardCount, 10000)
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

        log.debug("event={} Copied {} cards to new deck", LogEvent.DECK_COPY_SUCCESS, sourceDeck.getCards().size());
    }

    /**
     * Soft delete all cards in deck
     * UC-014: BR-053 - Cascade deletion
     */
    private void softDeleteAllCardsInDeck(final UUID deckId) {
        // Use CardRepository to soft delete all cards
        // This is more efficient than loading all cards into memory
        cardRepository.softDeleteByDeckId(deckId);

        log.debug("event={} Soft deleted all cards in deck: deckId={}", LogEvent.SUCCESS, deckId);
    }

    /**
     * Get deck including deleted (for restore/permanent delete)
     * UC-014: Restore and permanent delete operations
     */
    private Deck getDeckIncludingDeletedWithOwnershipCheck(final UUID deckId, final UUID userId) {
        return deckRepository.findByIdAndUserIdIncludingDeleted(deckId, userId)
            .orElseThrow(() -> {
                log.error("event={} Deck not found: deckId={}, userId={}", LogEvent.EX_RESOURCE_NOT_FOUND, deckId, userId);
                return new ResourceNotFoundException(
                    "DECK_008",
                    getMessage("error.deck.delete.not.found", deckId)
                );
            });
    }

    /**
     * Validate deck is actually deleted (in trash)
     * UC-014: Ensure deck can be restored or permanently deleted
     */
    private void validateDeckIsDeleted(final Deck deck) {
        if (!deck.isDeleted()) {
            log.warn("event={} Deck is not deleted: deckId={}, deckName={}", LogEvent.EX_VALIDATION, deck.getId(), deck.getName());
            throw new ValidationException(
                "DECK_009",
                getMessage("error.deck.restore.not.deleted")
            );
        }
    }

    /**
     * Restore all cards in deck
     * UC-014: BR-055 - Restore with review progress
     */
    private void restoreAllCardsInDeck(final UUID deckId) {
        // Use CardRepository to restore all cards
        cardRepository.restoreByDeckId(deckId);

        log.debug("event={} Restored all cards in deck: deckId={}", LogEvent.SUCCESS, deckId);
    }

    /**
     * Get internationalized message
     */
    private String getMessage(final String code, final Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}



