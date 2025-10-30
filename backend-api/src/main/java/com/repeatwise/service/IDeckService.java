package com.repeatwise.service;

import java.util.List;
import java.util.UUID;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckDeleteResponse;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.exception.DuplicateResourceException;
import com.repeatwise.exception.ResourceNotFoundException;

/**
 * Deck Service Interface
 *
 * Requirements:
 * - UC-011: Create Deck
 * - UC-012: Move Deck
 * - UC-013: Copy Deck
 * - UC-014: Delete Deck
 *
 * Business Rules:
 * - BR-031 to BR-034: Deck management rules
 *
 * @author RepeatWise Team
 */
public interface IDeckService {

    // ==================== UC-011: Create Deck ====================

    /**
     * Copy deck with all cards
     *
     * Requirements:
     * - UC-013: Copy Deck
     * - BR-048: Copy thresholds (< 100 cards = sync, >= 100 cards = async)
     * - BR-049: Card state reset (all cards start in Box 1, due tomorrow)
     * - BR-050: Naming convention (default: "[Original Name] (Copy)")
     *
     * Steps (Synchronous for < 100 cards):
     * 1. Validate source deck exists and belongs to user
     * 2. Validate destination folder exists and belongs to user (if not null)
     * 3. Validate deck size (< 100 cards for synchronous copy)
     * 4. Validate new name is unique in destination folder
     * 5. Create new deck with new name
     * 6. Copy all cards from source to new deck
     * 7. Initialize SRS state for all copied cards (Box 1, due tomorrow)
     * 8. Return new deck details
     *
     * Validation:
     * - Source deck must exist and belong to user
     * - Destination folder must exist and belong to user (if not null)
     * - New name must be unique in destination folder
     * - Deck size < 100 cards (for synchronous copy in MVP)
     *
     * Performance:
     * - Synchronous: < 100 cards, completes in < 2 seconds
     * - Asynchronous: >= 100 cards (NOT IMPLEMENTED IN MVP)
     *
     * SRS State Initialization:
     * - All copied cards reset to Box 1
     * - interval_days = 1
     * - due_date = tomorrow (CURRENT_DATE + 1)
     * - review_count = 0, lapse_count = 0
     * - last_reviewed_at = NULL
     *
     * @param deckId  Source deck ID to copy
     * @param request Copy deck request (newName, destinationFolderId)
     * @param userId  Current user ID
     * @return Newly created deck with copied cards
     * @throws ResourceNotFoundException  if source deck or destination folder not found
     * @throws DuplicateResourceException if new name exists in destination folder
     * @throws ValidationException        if deck too large (>= 100 cards in MVP)
     */
    DeckResponse copyDeck(UUID deckId, CopyDeckRequest request, UUID userId);

    /**
     * Create a new deck
     *
     * Requirements:
     * - UC-011: Create Deck
     * - BR-031: Deck naming (1-100 chars)
     * - BR-032: Deck can be at root level (folder_id nullable)
     * - BR-033: Name unique within same folder
     * - BR-034: Initial state (0 cards)
     *
     * Validation:
     * - Name must be 1-100 chars, trimmed
     * - Folder must exist and belong to user (if not null)
     * - Name must be unique within folder (or root level)
     *
     * @param request CreateDeckRequest
     * @param userId  Current user ID
     * @return Created deck details
     * @throws DuplicateResourceException if name exists in same folder
     * @throws ResourceNotFoundException  if folder not found
     * @throws ValidationException        if validation fails
     */
    DeckResponse createDeck(CreateDeckRequest request, UUID userId);

    /**
     * Soft-delete deck
     *
     * Requirements:
     * - UC-017: Delete Deck
     * - Cascade soft-delete to all cards
     *
     * @param deckId Deck ID
     * @param userId Current user ID
     * @return Deck deletion response with deletedAt timestamp
     * @throws ResourceNotFoundException if deck not found
     */
    DeckDeleteResponse deleteDeck(UUID deckId, UUID userId);

    /**
     * Get all decks for user
     *
     * Requirements:
     * - UC-011: List all decks
     *
     * @param userId Current user ID
     * @return List of all user's decks
     */
    List<DeckResponse> getAllDecks(UUID userId);

    /**
     * Get deck by ID
     *
     * Requirements:
     * - UC-011: View deck details
     *
     * @param deckId Deck ID
     * @param userId Current user ID
     * @return Deck details
     * @throws ResourceNotFoundException if deck not found or not owned by user
     */
    DeckResponse getDeckById(UUID deckId, UUID userId);

    /**
     * Get all decks in a folder
     *
     * Requirements:
     * - UC-011: List decks in folder
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     * @return List of decks in folder
     * @throws ResourceNotFoundException if folder not found
     */
    List<DeckResponse> getDecksByFolderId(UUID folderId, UUID userId);

    /**
     * Move deck to a different folder
     *
     * Requirements:
     * - UC-012: Move Deck
     * - BR-040: Move validation
     * - BR-041: Name conflict handling
     * - BR-042: Statistics update
     * - BR-043: Review progress preservation
     *
     * Steps:
     * 1. Validate deck exists and belongs to user
     * 2. Validate destination folder exists and belongs to user (if not null)
     * 3. Validate not moving to same folder
     * 4. Validate no name conflict in destination
     * 5. Update deck.folder_id
     * 6. Invalidate folder statistics for both old and new folders
     * 7. Preserve all review progress (card_box_position unchanged)
     *
     * Validation:
     * - Deck must exist and belong to user
     * - Destination folder must exist and belong to user (if not null)
     * - Cannot move to same folder
     * - Deck name must be unique in destination folder
     * - newFolderId can be NULL (move to root level)
     *
     * @param deckId      Deck ID to move
     * @param newFolderId Target folder ID (nullable - null = move to root)
     * @param userId      Current user ID
     * @return Updated deck details
     * @throws ResourceNotFoundException  if deck or folder not found
     * @throws ValidationException        if moving to same location
     * @throws DuplicateResourceException if name exists in destination
     */
    DeckResponse moveDeck(UUID deckId, UUID newFolderId, UUID userId);

    /**
     * Permanently delete deck (hard delete)
     *
     * Requirements:
     * - UC-014: Permanent delete from trash
     *
     * @param deckId Deck ID
     * @param userId Current user ID
     * @throws ResourceNotFoundException if deck not found
     * @throws ValidationException       if deck not in trash
     */
    void permanentlyDeleteDeck(UUID deckId, UUID userId);

    // ==================== UC-012: Move Deck ====================

    /**
     * Restore soft-deleted deck
     *
     * Requirements:
     * - UC-014: Undo delete
     *
     * @param deckId Deck ID
     * @param userId Current user ID
     * @return Restored deck
     * @throws ResourceNotFoundException if deck not found
     * @throws ValidationException       if deck not deleted
     */
    DeckResponse restoreDeck(UUID deckId, UUID userId);

    // ==================== UC-013: Copy Deck ====================

    /**
     * Update deck (rename and change description)
     *
     * Requirements:
     * - UC-011: Update deck details
     * - BR-033: Name unique within folder
     *
     * Validation:
     * - Name must be unique in folder (excluding current deck)
     * - Name must be 1-100 chars
     *
     * @param deckId  Deck ID
     * @param request UpdateDeckRequest
     * @param userId  Current user ID
     * @return Updated deck
     * @throws DuplicateResourceException if name exists in folder
     * @throws ResourceNotFoundException  if deck not found
     */
    DeckResponse updateDeck(UUID deckId, UpdateDeckRequest request, UUID userId);
}
