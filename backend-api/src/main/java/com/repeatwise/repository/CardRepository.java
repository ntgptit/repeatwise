package com.repeatwise.repository;

import com.repeatwise.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Card entity
 *
 * Requirements:
 * - UC-016: Create Card
 * - UC-017: Edit Card
 * - UC-018: Delete Card
 * - UC-019: Import Cards (CSV/Excel)
 * - UC-010: View Folder Statistics (count cards in folder tree)
 *
 * @author RepeatWise Team
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    // ==================== Basic Queries ====================

    /**
     * Find card by ID and user ID (with ownership check)
     * UC-017, UC-018
     */
    @Query("SELECT c FROM Card c " +
           "WHERE c.id = :cardId " +
           "AND c.deck.user.id = :userId " +
           "AND c.deletedAt IS NULL")
    Optional<Card> findByIdAndUserId(
        @Param("cardId") UUID cardId,
        @Param("userId") UUID userId
    );

    /**
     * Find all cards in a deck
     * UC-016, UC-017
     */
    @Query("SELECT c FROM Card c " +
           "WHERE c.deck.id = :deckId " +
           "AND c.deletedAt IS NULL " +
           "ORDER BY c.createdAt DESC")
    List<Card> findByDeckId(@Param("deckId") UUID deckId);

    /**
     * Count cards in deck
     * UC-016: Create Card
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "WHERE c.deck.id = :deckId " +
           "AND c.deletedAt IS NULL")
    Long countByDeckId(@Param("deckId") UUID deckId);

    // ==================== UC-010: Folder Statistics Queries ====================

    /**
     * Count total cards in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId " +
           "     OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND c.deletedAt IS NULL")
    Long countByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count due cards in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     * Due cards = cards with due_date <= today
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "JOIN c.cardBoxPosition cbp " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId " +
           "     OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.dueDate <= :today " +
           "AND c.deletedAt IS NULL")
    Long countDueCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath,
        @Param("today") LocalDate today
    );

    /**
     * Count new cards in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     * New cards = cards with review_count = 0 (never reviewed)
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "JOIN c.cardBoxPosition cbp " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId " +
           "     OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.reviewCount = 0 " +
           "AND c.deletedAt IS NULL")
    Long countNewCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count mature cards in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     * Mature cards = cards with current_box >= 5 (boxes 5, 6, 7)
     */
    @Query("SELECT COUNT(c) FROM Card c " +
           "JOIN c.cardBoxPosition cbp " +
           "WHERE c.deck.user.id = :userId " +
           "AND (c.deck.folder.id = :folderId " +
           "     OR c.deck.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND cbp.currentBox >= 5 " +
           "AND c.deletedAt IS NULL")
    Long countMatureCardsByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    // ==================== Delete Operations ====================

    /**
     * Soft delete all cards in deck
     * UC-015: Delete Deck - Cascade soft delete
     */
    @Query("UPDATE Card c " +
           "SET c.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE c.deck.id = :deckId " +
           "AND c.deletedAt IS NULL")
    void softDeleteByDeckId(@Param("deckId") UUID deckId);

    /**
     * Soft delete all cards in folder and descendants
     * UC-009: Delete Folder - Cascade soft delete to cards
     */
    @Query("UPDATE Card c " +
           "SET c.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE c.deck.user.id = :userId " +
           "AND c.deck.folder.path LIKE CONCAT(:folderPath, '/%') " +
           "AND c.deletedAt IS NULL")
    void softDeleteByFolderPathPrefix(
        @Param("userId") UUID userId,
        @Param("folderPath") String folderPath
    );

    // ==================== Duplicate Check ====================

    /**
     * Check if duplicate card exists in deck (same front text)
     * UC-016: Create Card - Duplicate validation
     * UC-019: Import Cards - Duplicate detection
     */
    @Query("SELECT COUNT(c) > 0 FROM Card c " +
           "WHERE c.deck.id = :deckId " +
           "AND LOWER(c.front) = LOWER(:front) " +
           "AND c.deletedAt IS NULL")
    boolean existsByDeckIdAndFront(
        @Param("deckId") UUID deckId,
        @Param("front") String front
    );
}
