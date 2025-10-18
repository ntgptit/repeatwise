package com.repeatwise.repository;

import com.repeatwise.entity.FolderStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for FolderStats entity
 *
 * Requirements:
 * - UC-010: View Folder Statistics
 * - Schema: folder_stats table (section 4.2)
 * - NFR: Performance optimization via denormalized cache (5-min TTL)
 *
 * Queries:
 * - Get/create stats for folder
 * - Invalidate stale stats
 * - Batch invalidation for folder moves
 *
 * @author RepeatWise Team
 */
@Repository
public interface FolderStatsRepository extends JpaRepository<FolderStats, UUID> {

    // ==================== Basic Queries ====================

    /**
     * Find stats for folder and user
     * UC-010: Get folder statistics
     */
    @Query("SELECT fs FROM FolderStats fs " +
           "WHERE fs.folder.id = :folderId " +
           "AND fs.user.id = :userId")
    Optional<FolderStats> findByFolderIdAndUserId(
        @Param("folderId") UUID folderId,
        @Param("userId") UUID userId
    );

    /**
     * Find stats for multiple folders (batch query for tree view)
     * UC-010: Get stats for entire folder tree
     */
    @Query("SELECT fs FROM FolderStats fs " +
           "WHERE fs.folder.id IN :folderIds " +
           "AND fs.user.id = :userId")
    List<FolderStats> findByFolderIdsAndUserId(
        @Param("folderIds") List<UUID> folderIds,
        @Param("userId") UUID userId
    );

    /**
     * Check if stats exist for folder
     * Used to determine if stats need to be created
     */
    @Query("SELECT COUNT(fs) > 0 FROM FolderStats fs " +
           "WHERE fs.folder.id = :folderId " +
           "AND fs.user.id = :userId")
    boolean existsByFolderIdAndUserId(
        @Param("folderId") UUID folderId,
        @Param("userId") UUID userId
    );

    // ==================== Invalidation Queries ====================

    /**
     * Delete stats for folder (invalidation)
     * Called when folder contents change (cards added/removed, decks moved, etc.)
     */
    @Modifying
    @Query("DELETE FROM FolderStats fs " +
           "WHERE fs.folder.id = :folderId " +
           "AND fs.user.id = :userId")
    void deleteByFolderIdAndUserId(
        @Param("folderId") UUID folderId,
        @Param("userId") UUID userId
    );

    /**
     * Delete stats for folder and all ancestors (invalidate parent chain)
     * UC-007: Move folder - Invalidate old and new parent chains
     * UC-009: Delete folder - Invalidate parent chain
     *
     * Used when folder contents change, need to invalidate entire ancestor chain
     */
    @Modifying
    @Query("DELETE FROM FolderStats fs " +
           "WHERE fs.user.id = :userId " +
           "AND fs.folder.id IN :folderIds")
    void deleteByFolderIdsAndUserId(
        @Param("userId") UUID userId,
        @Param("folderIds") List<UUID> folderIds
    );

    /**
     * Delete stats for all descendants of folder
     * UC-007: Move folder - Invalidate all descendants
     * UC-009: Delete folder - Cascade invalidation
     */
    @Modifying
    @Query("DELETE FROM FolderStats fs " +
           "WHERE fs.user.id = :userId " +
           "AND fs.folder.path LIKE CONCAT(:folderPath, '%')")
    void deleteByDescendantsOfFolder(
        @Param("userId") UUID userId,
        @Param("folderPath") String folderPath
    );

    /**
     * Delete all stale stats (older than TTL)
     * Scheduled job: Clean up stale cache entries (optional, stats auto-recompute)
     */
    @Modifying
    @Query(value = "DELETE FROM folder_stats " +
                   "WHERE last_computed_at < NOW() - INTERVAL '30 minutes'",
           nativeQuery = true)
    void deleteStaleStats();

    // ==================== Batch Recalculation ====================

    /**
     * Find folders that need stats recalculation (stale or missing)
     * Used by background job to batch recalculate stats
     */
    @Query("SELECT f.id FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.deletedAt IS NULL " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM FolderStats fs " +
           "  WHERE fs.folder.id = f.id " +
           "  AND fs.user.id = :userId " +
           "  AND fs.lastComputedAt > CURRENT_TIMESTAMP - 300000" + // 5 minutes in millis
           ")")
    List<UUID> findFoldersNeedingStatsRecalculation(@Param("userId") UUID userId);

    // ==================== Statistics Queries ====================

    /**
     * Get total due cards count for user (all folders)
     * Used for dashboard summary
     */
    @Query("SELECT COALESCE(SUM(fs.dueCardsCount), 0) FROM FolderStats fs " +
           "WHERE fs.user.id = :userId " +
           "AND fs.folder.parentFolder IS NULL") // Only root folders to avoid double counting
    Long getTotalDueCardsForUser(@Param("userId") UUID userId);

    /**
     * Get total cards count for user (all folders)
     * Used for dashboard summary
     */
    @Query("SELECT COALESCE(SUM(fs.totalCardsCount), 0) FROM FolderStats fs " +
           "WHERE fs.user.id = :userId " +
           "AND fs.folder.parentFolder IS NULL") // Only root folders to avoid double counting
    Long getTotalCardsForUser(@Param("userId") UUID userId);
}
