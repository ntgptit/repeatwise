package com.repeatwise.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.Deck;

/**
 * Repository for Deck entity
 */
@Repository
public interface DeckRepository extends JpaRepository<Deck, UUID> {

    /**
     * Find all active decks for a user (excluding soft-deleted)
     */
    @Query("SELECT d FROM Deck d WHERE d.user.id = :userId AND d.deletedAt IS NULL ORDER BY d.name")
    List<Deck> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Find active deck by ID and user ID
     */
    @Query("SELECT d FROM Deck d WHERE d.id = :deckId AND d.user.id = :userId AND d.deletedAt IS NULL")
    Optional<Deck> findByIdAndUserId(@Param("deckId") UUID deckId, @Param("userId") UUID userId);

    /**
     * Find all decks in a folder
     */
    @Query("SELECT d FROM Deck d WHERE d.user.id = :userId AND d.folder.id = :folderId AND d.deletedAt IS NULL ORDER BY d.name")
    List<Deck> findByUserIdAndFolderId(@Param("userId") UUID userId, @Param("folderId") UUID folderId);

    /**
     * Find all root-level decks (not in any folder)
     */
    @Query("SELECT d FROM Deck d WHERE d.user.id = :userId AND d.folder IS NULL AND d.deletedAt IS NULL ORDER BY d.name")
    List<Deck> findRootDecksByUserId(@Param("userId") UUID userId);

    boolean existsByUserIdAndFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(UUID userId, String name);

    boolean existsByUserIdAndFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(UUID userId, UUID folderId, String name);

    boolean existsByUserIdAndFolderIsNullAndIdNotAndNameIgnoreCaseAndDeletedAtIsNull(UUID userId, UUID id, String name);

    boolean existsByUserIdAndFolderIdAndIdNotAndNameIgnoreCaseAndDeletedAtIsNull(UUID userId, UUID folderId, UUID id, String name);

    /**
     * Count total decks for a user (active only)
     */
    @Query("SELECT COUNT(d) FROM Deck d WHERE d.user.id = :userId AND d.deletedAt IS NULL")
    long countByUserId(@Param("userId") UUID userId);

    /**
     * Count decks in a folder
     */
    @Query("SELECT COUNT(d) FROM Deck d WHERE d.folder.id = :folderId AND d.deletedAt IS NULL")
    long countByFolderId(@Param("folderId") UUID folderId);

    /**
     * Count decks in multiple folders (for folder subtree operations)
     */
    @Query("SELECT COUNT(d) FROM Deck d WHERE d.folder.id IN :folderIds AND d.deletedAt IS NULL")
    long countByFolderIds(@Param("folderIds") List<UUID> folderIds);

    /**
     * Get active deck IDs within specified folders.
     */
    @Query("""
            SELECT d.id FROM Deck d
            WHERE d.user.id = :userId
              AND d.deletedAt IS NULL
              AND d.folder.id IN :folderIds
            """)
    List<UUID> findActiveDeckIdsByUserIdAndFolderIds(@Param("userId") UUID userId,
            @Param("folderIds") List<UUID> folderIds);

    /**
     * Count active decks in specified folders.
     */
    @Query("""
            SELECT COUNT(d) FROM Deck d
            WHERE d.user.id = :userId
              AND d.deletedAt IS NULL
              AND d.folder.id IN :folderIds
            """)
    long countActiveDecksByUserIdAndFolderIds(@Param("userId") UUID userId,
            @Param("folderIds") List<UUID> folderIds);

    /**
     * Soft delete all decks in a folder
     */
    @Modifying
    @Query("UPDATE Deck d SET d.deletedAt = :deletedAt WHERE d.folder.id = :folderId AND d.deletedAt IS NULL")
    int softDeleteByFolderId(@Param("folderId") UUID folderId, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Soft delete all decks in multiple folders (for folder subtree deletion)
     */
    @Modifying
    @Query("UPDATE Deck d SET d.deletedAt = :deletedAt WHERE d.folder.id IN :folderIds AND d.deletedAt IS NULL")
    int softDeleteByFolderIds(@Param("folderIds") List<UUID> folderIds, @Param("deletedAt") LocalDateTime deletedAt);

    /**
     * Restore soft-deleted decks in a folder
     */
    @Modifying
    @Query("UPDATE Deck d SET d.deletedAt = NULL WHERE d.folder.id = :folderId AND d.deletedAt IS NOT NULL")
    int restoreByFolderId(@Param("folderId") UUID folderId);

    /**
     * Restore soft-deleted decks in multiple folders (for folder subtree restoration)
     */
    @Modifying
    @Query("UPDATE Deck d SET d.deletedAt = NULL WHERE d.folder.id IN :folderIds AND d.deletedAt IS NOT NULL")
    int restoreByFolderIds(@Param("folderIds") List<UUID> folderIds);

    /**
     * Find decks deleted by user (in trash)
     */
    @Query("SELECT d FROM Deck d WHERE d.user.id = :userId AND d.deletedAt IS NOT NULL ORDER BY d.deletedAt DESC")
    List<Deck> findDeletedByUserId(@Param("userId") UUID userId);

    /**
     * Find soft-deleted deck by ID and user ID
     */
    @Query("SELECT d FROM Deck d WHERE d.id = :deckId AND d.user.id = :userId AND d.deletedAt IS NOT NULL")
    Optional<Deck> findDeletedByIdAndUserId(@Param("deckId") UUID deckId, @Param("userId") UUID userId);
}
