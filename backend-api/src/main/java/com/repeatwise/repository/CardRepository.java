package com.repeatwise.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.Card;

/**
 * Repository interface for Card entity
 *
 * Requirements:
 * - UC-017: Create/Edit Card
 * - UC-018: Delete Card
 * - Provides CRUD operations and custom queries for cards
 *
 * Query Methods:
 * - Find cards by deck (with pagination)
 * - Find card by ID and deck (authorization check)
 * - Check for duplicate cards (by front text)
 * - Count cards in deck
 *
 * @author RepeatWise Team
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    /**
     * Find all cards in a deck (paginated)
     * Used in: UC-017 (list cards in deck)
     *
     * @param deckId Deck UUID
     * @param pageable Pagination parameters
     * @return Page of cards
     */
    Page<Card> findByDeckIdAndDeletedAtIsNull(UUID deckId, Pageable pageable);

    /**
     * Find card by ID and deck ID (authorization check)
     * Ensures user can only access cards in their own decks
     *
     * @param id Card UUID
     * @param deckId Deck UUID
     * @return Optional Card
     */
    Optional<Card> findByIdAndDeckIdAndDeletedAtIsNull(UUID id, UUID deckId);

    /**
     * Count total cards in a deck
     *
     * @param deckId Deck UUID
     * @return Number of cards
     */
    long countByDeckIdAndDeletedAtIsNull(UUID deckId);

    /**
     * Check if card with same front text exists in deck (case-insensitive)
     * Used for duplicate warning in UC-017
     *
     * @param deckId Deck UUID
     * @param front Front text (trimmed and lowercased)
     * @return true if duplicate exists
     */
    @Query("SELECT COUNT(c) > 0 FROM Card c WHERE c.deck.id = :deckId " +
           "AND LOWER(TRIM(c.front)) = LOWER(TRIM(:front)) " +
           "AND c.deletedAt IS NULL")
    boolean existsByDeckIdAndFrontIgnoreCase(@Param("deckId") UUID deckId, @Param("front") String front);

    /**
     * Find all cards in a deck (for export/stats)
     *
     * @param deckId Deck UUID
     * @return List of cards
     */
    List<Card> findByDeckIdAndDeletedAtIsNull(UUID deckId);

    /**
     * Soft delete all cards in a deck
     * UC-017: Delete Deck - Cascade soft delete to cards
     *
     * @param deckId Deck UUID
     */
    @Modifying
    @Query("UPDATE Card c SET c.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE c.deck.id = :deckId AND c.deletedAt IS NULL")
    void softDeleteByDeckId(@Param("deckId") UUID deckId);

    /**
     * Restore all soft-deleted cards in a deck
     * UC-017: Restore Deck - Restore cards along with deck
     *
     * @param deckId Deck UUID
     */
    @Modifying
    @Query("UPDATE Card c SET c.deletedAt = NULL " +
           "WHERE c.deck.id = :deckId AND c.deletedAt IS NOT NULL")
    void restoreByDeckId(@Param("deckId") UUID deckId);

    /**
     * Soft delete all cards in decks within folder and descendants
     * UC-009: Delete Folder - Cascade soft delete to cards
     *
     * @param userId User UUID
     * @param folderPath Folder path prefix
     */
    @Modifying
    @Query("UPDATE Card c SET c.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE c.deck.user.id = :userId " +
           "AND c.deck.folder.path LIKE CONCAT(:folderPath, '/%') " +
           "AND c.deletedAt IS NULL")
    void softDeleteByFolderPathPrefix(
        @Param("userId") UUID userId,
        @Param("folderPath") String folderPath
    );

    /**
     * Restore all soft-deleted cards in decks within folder and descendants
     * UC-009: Restore Folder - Restore cards along with folder
     *
     * @param userId User UUID
     * @param folderPath Folder path prefix
     */
    @Modifying
    @Query("UPDATE Card c SET c.deletedAt = NULL " +
           "WHERE c.deck.user.id = :userId " +
           "AND c.deck.folder.path LIKE CONCAT(:folderPath, '/%') " +
           "AND c.deletedAt IS NOT NULL")
    void restoreByFolderPathPrefix(
        @Param("userId") UUID userId,
        @Param("folderPath") String folderPath
    );

    /**
     * Hard delete all cards in decks within folder and descendants (permanent delete)
     * UC-009: Permanent Delete Folder - Hard delete cards
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     */
    @Modifying
    @Query("DELETE FROM Card c " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%'))")
    void hardDeleteByFolderId(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count total cards in decks within folder and descendants (recursive)
     * UC-010: View Folder Statistics - Count cards in folder tree
     *
     * @param userId User UUID
     * @param folderId Folder UUID
     * @param folderPath Folder path prefix
     * @return Number of cards
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND c.deletedAt IS NULL")
    Long countByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );
}
