package com.repeatwise.repository;

import com.repeatwise.entity.Deck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Deck entity
 *
 * Requirements:
 * - UC-011: Create Deck
 * - UC-012: Rename Deck
 * - UC-013: Move Deck
 * - UC-014: Copy Deck
 * - UC-015: Delete Deck
 * - UC-010: View Folder Statistics (count decks in folder tree)
 *
 * @author RepeatWise Team
 */
@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {

    // ==================== Basic Queries ====================

    /**
     * Find deck by ID and user ID (with ownership check)
     * UC-012, UC-013, UC-014, UC-015
     */
    @Query("SELECT d FROM Deck d " +
           "WHERE d.id = :deckId " +
           "AND d.user.id = :userId " +
           "AND d.deletedAt IS NULL")
    Optional<Deck> findByIdAndUserId(
        @Param("deckId") UUID deckId,
        @Param("userId") UUID userId
    );

    /**
     * Find all decks in a folder
     * UC-010: View Folder Statistics
     */
    @Query("SELECT d FROM Deck d " +
           "WHERE d.folder.id = :folderId " +
           "AND d.deletedAt IS NULL " +
           "ORDER BY d.name ASC")
    List<Deck> findByFolderId(@Param("folderId") UUID folderId);

    /**
     * Find all decks in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     */
    @Query("SELECT d FROM Deck d " +
           "WHERE d.user.id = :userId " +
           "AND (d.folder.id = :folderId " +
           "     OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND d.deletedAt IS NULL")
    List<Deck> findByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Count decks in folder (non-recursive)
     * UC-011: Create Deck
     */
    @Query("SELECT COUNT(d) FROM Deck d " +
           "WHERE d.folder.id = :folderId " +
           "AND d.deletedAt IS NULL")
    Long countByFolderId(@Param("folderId") UUID folderId);

    /**
     * Count decks in folder and all descendants (recursive)
     * UC-010: View Folder Statistics
     */
    @Query("SELECT COUNT(d) FROM Deck d " +
           "WHERE d.user.id = :userId " +
           "AND (d.folder.id = :folderId " +
           "     OR d.folder.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND d.deletedAt IS NULL")
    Long countByFolderIdRecursive(
        @Param("userId") UUID userId,
        @Param("folderId") UUID folderId,
        @Param("folderPath") String folderPath
    );

    /**
     * Check if deck name exists in folder
     * UC-011: Create Deck - Unique name validation
     */
    @Query("SELECT COUNT(d) > 0 FROM Deck d " +
           "WHERE d.folder.id = :folderId " +
           "AND d.name = :name " +
           "AND d.deletedAt IS NULL")
    boolean existsByFolderIdAndName(
        @Param("folderId") UUID folderId,
        @Param("name") String name
    );

    /**
     * Check if deck name exists in folder (excluding specific deck)
     * UC-012: Rename Deck - Unique name validation
     */
    @Query("SELECT COUNT(d) > 0 FROM Deck d " +
           "WHERE d.folder.id = :folderId " +
           "AND d.name = :name " +
           "AND d.id <> :excludeDeckId " +
           "AND d.deletedAt IS NULL")
    boolean existsByFolderIdAndNameExcluding(
        @Param("folderId") UUID folderId,
        @Param("name") String name,
        @Param("excludeDeckId") UUID excludeDeckId
    );

    /**
     * Check if deck name exists at root level (folder_id = NULL)
     * UC-011: Create Deck - Unique name validation for root-level decks
     */
    @Query("SELECT COUNT(d) > 0 FROM Deck d " +
           "WHERE d.folder IS NULL " +
           "AND d.user.id = :userId " +
           "AND d.name = :name " +
           "AND d.deletedAt IS NULL")
    boolean existsByUserIdAndRootAndName(
        @Param("userId") UUID userId,
        @Param("name") String name
    );

    /**
     * Check if deck name exists at root level (excluding specific deck)
     * UC-012: Rename Deck - Unique name validation for root-level decks
     */
    @Query("SELECT COUNT(d) > 0 FROM Deck d " +
           "WHERE d.folder IS NULL " +
           "AND d.user.id = :userId " +
           "AND d.name = :name " +
           "AND d.id <> :excludeDeckId " +
           "AND d.deletedAt IS NULL")
    boolean existsByUserIdAndRootAndNameExcluding(
        @Param("userId") UUID userId,
        @Param("name") String name,
        @Param("excludeDeckId") UUID excludeDeckId
    );

    // ==================== Move Operations ====================

    /**
     * Find all decks in folder (for bulk operations)
     * UC-007: Move Folder - Move all decks when folder moves
     */
    @Query("SELECT d FROM Deck d " +
           "WHERE d.folder.id = :folderId " +
           "AND d.deletedAt IS NULL")
    List<Deck> findByFolderIdForMove(@Param("folderId") UUID folderId);

    // ==================== Delete Operations ====================

    /**
     * Soft delete all decks in folder
     * UC-009: Delete Folder - Cascade soft delete
     */
    @Query("UPDATE Deck d " +
           "SET d.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE d.folder.id = :folderId " +
           "AND d.deletedAt IS NULL")
    void softDeleteByFolderId(@Param("folderId") UUID folderId);

    /**
     * Soft delete all decks in folder and descendants
     * UC-009: Delete Folder - Cascade soft delete to descendants
     */
    @Query("UPDATE Deck d " +
           "SET d.deletedAt = CURRENT_TIMESTAMP " +
           "WHERE d.user.id = :userId " +
           "AND d.folder.path LIKE CONCAT(:folderPath, '/%') " +
           "AND d.deletedAt IS NULL")
    void softDeleteByFolderPathPrefix(
        @Param("userId") UUID userId,
        @Param("folderPath") String folderPath
    );

    // ==================== UC-014: Delete/Restore Deck ====================

    /**
     * Find deck by ID and user ID including deleted decks
     * UC-014: Restore Deck, Permanent Delete
     */
    @Query("SELECT d FROM Deck d " +
           "WHERE d.id = :deckId " +
           "AND d.user.id = :userId")
    Optional<Deck> findByIdAndUserIdIncludingDeleted(
        @Param("deckId") UUID deckId,
        @Param("userId") UUID userId
    );
}
