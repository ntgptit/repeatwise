package com.repeatwise.controller;

import com.repeatwise.dto.request.deck.CopyDeckRequest;
import com.repeatwise.dto.request.deck.CreateDeckRequest;
import com.repeatwise.dto.request.deck.MoveDeckRequest;
import com.repeatwise.dto.request.deck.UpdateDeckRequest;
import com.repeatwise.dto.response.deck.DeckResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IDeckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Deck Management
 *
 * Requirements:
 * - UC-011: Create Deck
 * - UC-012: Move Deck
 * - UC-013: Copy Deck
 * - UC-014: Delete Deck
 *
 * Endpoints:
 * - GET    /api/decks              - Get all decks for user
 * - GET    /api/decks/{id}         - Get deck details
 * - POST   /api/decks              - Create deck
 * - PUT    /api/decks/{id}         - Update deck (rename/description)
 * - DELETE /api/decks/{id}         - Soft delete deck
 * - POST   /api/decks/{id}/restore - Restore deck
 * - DELETE /api/decks/{id}/permanent - Hard delete deck
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
@Slf4j
public class DeckController {

    private final IDeckService deckService;

    // ==================== UC-011: Create Deck ====================

    /**
     * Create a new deck (UC-011)
     *
     * Requirements:
     * - UC-011: Create Deck
     * - BR-031: Deck naming (1-100 chars)
     * - BR-032: Deck can be at root level (folder_id nullable)
     * - BR-033: Name unique within same folder
     * - BR-034: Initial state (0 cards)
     *
     * Request Body:
     * {
     *   "name": "Academic Vocabulary",
     *   "description": "High-frequency academic words for IELTS writing",
     *   "folderId": "uuid-or-null"
     * }
     *
     * Response: 201 Created with DeckResponse
     * {
     *   "id": "uuid",
     *   "name": "Academic Vocabulary",
     *   "description": "High-frequency academic words...",
     *   "folderId": "uuid-or-null",
     *   "folderName": "Vocabulary",
     *   "cardCount": 0,
     *   "dueCards": 0,
     *   "newCards": 0,
     *   "createdAt": "2025-01-19T00:00:00Z",
     *   "updatedAt": "2025-01-19T00:00:00Z"
     * }
     *
     * Error Responses:
     * - 400 Bad Request: Invalid name (empty, too long)
     * - 404 Not Found: Folder not found
     * - 409 Conflict: Deck name already exists in folder
     *
     * @param request Create deck request
     * @return Created deck response with 201 Created status
     */
    @PostMapping
    public ResponseEntity<DeckResponse> createDeck(
            @Valid @RequestBody final CreateDeckRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/decks - Creating deck: name={}, folderId={}, userId={}",
            request.getName(), request.getFolderId(), userId);

        final DeckResponse response = deckService.createDeck(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all decks for current user
     *
     * @return List of all decks
     */
    @GetMapping
    public ResponseEntity<List<DeckResponse>> getAllDecks() {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/decks - Getting all decks: userId={}", userId);

        final List<DeckResponse> response = deckService.getAllDecks(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get deck by ID
     *
     * @param deckId Deck ID
     * @return Deck details response
     */
    @GetMapping("/{deckId}")
    public ResponseEntity<DeckResponse> getDeckById(@PathVariable final UUID deckId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/decks/{} - Getting deck details: userId={}", deckId, userId);

        final DeckResponse response = deckService.getDeckById(deckId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Update deck (rename and update description)
     *
     * @param deckId Deck ID to update
     * @param request Update deck request
     * @return Updated deck response
     */
    @PutMapping("/{deckId}")
    public ResponseEntity<DeckResponse> updateDeck(
            @PathVariable final UUID deckId,
            @Valid @RequestBody final UpdateDeckRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("PUT /api/decks/{} - Updating deck: userId={}", deckId, userId);

        final DeckResponse response = deckService.updateDeck(deckId, request, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Soft delete deck
     *
     * @param deckId Deck ID to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{deckId}")
    public ResponseEntity<Void> deleteDeck(@PathVariable final UUID deckId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("DELETE /api/decks/{} - Soft-deleting deck: userId={}", deckId, userId);

        deckService.deleteDeck(deckId, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Restore soft-deleted deck from trash
     *
     * @param deckId Deck ID to restore
     * @return Restored deck response
     */
    @PostMapping("/{deckId}/restore")
    public ResponseEntity<DeckResponse> restoreDeck(@PathVariable final UUID deckId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/decks/{}/restore - Restoring deck: userId={}", deckId, userId);

        final DeckResponse response = deckService.restoreDeck(deckId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Permanently delete deck from trash
     *
     * WARNING: This action is irreversible!
     *
     * @param deckId Deck ID to permanently delete
     * @return 204 No Content
     */
    @DeleteMapping("/{deckId}/permanent")
    public ResponseEntity<Void> permanentlyDeleteDeck(@PathVariable final UUID deckId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.warn("DELETE /api/decks/{}/permanent - Permanently deleting deck: userId={}",
            deckId, userId);

        deckService.permanentlyDeleteDeck(deckId, userId);

        return ResponseEntity.noContent().build();
    }

    // ==================== Helper Endpoints ====================

    /**
     * Get all decks in a specific folder
     *
     * @param folderId Folder ID
     * @return List of decks in folder
     */
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<DeckResponse>> getDecksByFolderId(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/decks/folder/{} - Getting decks in folder: userId={}", folderId, userId);

        final List<DeckResponse> response = deckService.getDecksByFolderId(folderId, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-012: Move Deck ====================

    /**
     * Move deck to a different folder (UC-012)
     *
     * Requirements:
     * - UC-012: Move Deck
     * - BR-040: Move validation
     * - BR-041: Name conflict handling
     * - BR-042: Statistics update
     * - BR-043: Review progress preservation
     *
     * Request Body:
     * {
     *   "newFolderId": "uuid" // nullable - null means move to root
     * }
     *
     * Response: 200 OK with DeckResponse
     * {
     *   "id": "uuid",
     *   "name": "Academic Vocabulary",
     *   "description": "High-frequency academic words...",
     *   "folderId": "new-folder-uuid",
     *   "folderName": "Grammar",
     *   "cardCount": 120,
     *   "dueCards": 20,
     *   "newCards": 5,
     *   "createdAt": "2025-01-19T00:00:00Z",
     *   "updatedAt": "2025-01-19T10:30:00Z"
     * }
     *
     * Use Cases:
     * 1. Move deck from "Vocabulary" to "Grammar" folder
     *    POST /api/decks/{deckId}/move
     *    Body: { "newFolderId": "grammar-folder-uuid" }
     *
     * 2. Move deck to root level (no folder)
     *    POST /api/decks/{deckId}/move
     *    Body: { "newFolderId": null }
     *
     * 3. Move deck from root to folder
     *    POST /api/decks/{deckId}/move
     *    Body: { "newFolderId": "vocabulary-folder-uuid" }
     *
     * Error Responses:
     * - 400 Bad Request: Moving to same folder
     * - 404 Not Found: Deck or destination folder not found
     * - 409 Conflict: Deck name already exists in destination
     *
     * Performance:
     * - Completes in < 200ms
     * - Single UPDATE query
     * - All review progress preserved automatically
     *
     * @param deckId  Deck ID to move
     * @param request Move deck request with newFolderId
     * @return Updated deck response with 200 OK status
     */
    @PostMapping("/{deckId}/move")
    public ResponseEntity<DeckResponse> moveDeck(
            @PathVariable final UUID deckId,
            @Valid @RequestBody final MoveDeckRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/decks/{}/move - Moving deck: newFolderId={}, userId={}",
            deckId, request.getNewFolderId(), userId);

        final DeckResponse response = deckService.moveDeck(deckId, request.getNewFolderId(), userId);

        log.info("Deck moved successfully: deckId={}, newFolderId={}, userId={}",
            deckId, request.getNewFolderId(), userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-013: Copy Deck ====================

    /**
     * Copy deck with all cards (UC-013)
     *
     * Requirements:
     * - UC-013: Copy Deck
     * - BR-048: Copy thresholds (< 100 cards = sync, >= 100 cards = async)
     * - BR-049: Card state reset (all cards start in Box 1, due tomorrow)
     * - BR-050: Naming convention (default: "[Original] (Copy)")
     *
     * Request Body:
     * {
     *   "newName": "Academic Vocabulary (Copy)",
     *   "destinationFolderId": "uuid-or-null"
     * }
     *
     * Response: 200 OK with DeckResponse
     * {
     *   "id": "new-deck-uuid",
     *   "name": "Academic Vocabulary (Copy)",
     *   "description": "Original description",
     *   "folderId": "destination-folder-uuid",
     *   "folderName": "Backups",
     *   "cardCount": 50,
     *   "dueCards": 50,  // All cards due (Box 1)
     *   "newCards": 50,  // All cards new (never reviewed)
     *   "createdAt": "2025-01-19T10:30:00Z",
     *   "updatedAt": "2025-01-19T10:30:00Z"
     * }
     *
     * Use Cases:
     * 1. Copy deck to same folder with new name
     *    POST /api/decks/{deckId}/copy
     *    Body: { "newName": "Academic Vocabulary (Copy)", "destinationFolderId": "same-folder-uuid" }
     *
     * 2. Copy deck to different folder (backup)
     *    POST /api/decks/{deckId}/copy
     *    Body: { "newName": "IELTS Vocabulary Backup", "destinationFolderId": "backups-folder-uuid" }
     *
     * 3. Copy deck to root level
     *    POST /api/decks/{deckId}/copy
     *    Body: { "newName": "Vocabulary Copy", "destinationFolderId": null }
     *
     * Error Responses:
     * - 400 Bad Request: Invalid name, deck too large (>= 100 cards)
     * - 404 Not Found: Source deck or destination folder not found
     * - 409 Conflict: Deck name already exists in destination
     *
     * Performance:
     * - Synchronous (< 100 cards): Completes in < 2 seconds
     * - Asynchronous (>= 100 cards): NOT IMPLEMENTED IN MVP
     *
     * Business Rules:
     * - BR-048: < 100 cards = synchronous copy
     * - BR-049: All copied cards reset to Box 1, due tomorrow
     * - BR-050: Default name: "[Original Name] (Copy)"
     * - Original deck and progress unchanged
     *
     * @param deckId  Source deck ID to copy
     * @param request Copy deck request (newName, destinationFolderId)
     * @return Newly created deck with copied cards
     */
    @PostMapping("/{deckId}/copy")
    public ResponseEntity<DeckResponse> copyDeck(
            @PathVariable final UUID deckId,
            @Valid @RequestBody final CopyDeckRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/decks/{}/copy - Copying deck: newName={}, destinationFolderId={}, userId={}",
            deckId, request.getNewName(), request.getDestinationFolderId(), userId);

        final DeckResponse response = deckService.copyDeck(deckId, request, userId);

        log.info("Deck copied successfully: sourceDeckId={}, newDeckId={}, cardCount={}, userId={}",
            deckId, response.getId(), response.getCardCount(), userId);

        return ResponseEntity.ok(response);
    }
}
