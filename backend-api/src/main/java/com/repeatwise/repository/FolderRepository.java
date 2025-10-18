package com.repeatwise.repository;

import com.repeatwise.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Folder entity
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy
 * - UC-006: Rename Folder
 * - UC-007: Move Folder
 * - UC-008: Copy Folder
 * - UC-009: Delete Folder
 * - UC-010: View Folder Statistics
 *
 * Custom Queries:
 * - Folder tree retrieval (all folders for user)
 * - Descendant queries (using materialized path)
 * - Depth calculations
 * - Name uniqueness checks
 *
 * @author RepeatWise Team
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    // ==================== Basic Queries ====================

    /**
     * Find folder by ID and user ID (ownership check)
     * UC-005, UC-006, UC-007, UC-008, UC-009
     */
    @Query("SELECT f FROM Folder f WHERE f.id = :folderId AND f.user.id = :userId AND f.deletedAt IS NULL")
    Optional<Folder> findByIdAndUserId(@Param("folderId") UUID folderId, @Param("userId") UUID userId);

    /**
     * Find all root folders for user (parentFolder = null)
     * UC-005: Get folder tree root nodes
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.parentFolder IS NULL AND f.deletedAt IS NULL ORDER BY f.name ASC")
    List<Folder> findRootFoldersByUserId(@Param("userId") UUID userId);

    /**
     * Find all folders for user (entire tree, sorted by path)
     * UC-005: Get complete folder tree
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL ORDER BY f.path ASC")
    List<Folder> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Find folders up to max depth (for tree view with depth limit)
     * UC-005: Get folder tree with depth constraint
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.depth <= :maxDepth AND f.deletedAt IS NULL ORDER BY f.path ASC")
    List<Folder> findByUserIdAndMaxDepth(@Param("userId") UUID userId, @Param("maxDepth") Integer maxDepth);

    // ==================== Name Uniqueness Checks ====================

    /**
     * Check if folder name exists in same parent (for create/rename validation)
     * UC-005: BR-013 - Unique name within same parent folder
     * UC-006: Rename validation
     */
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.parentFolder.id = :parentFolderId " +
           "AND f.name = :name " +
           "AND f.deletedAt IS NULL")
    boolean existsByUserIdAndParentFolderIdAndName(
        @Param("userId") UUID userId,
        @Param("parentFolderId") UUID parentFolderId,
        @Param("name") String name
    );

    /**
     * Check if root folder name exists (parentFolder = null)
     * UC-005: BR-013 - Unique name at root level
     */
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.parentFolder IS NULL " +
           "AND f.name = :name " +
           "AND f.deletedAt IS NULL")
    boolean existsByUserIdAndRootAndName(@Param("userId") UUID userId, @Param("name") String name);

    /**
     * Check if root folder name exists (excluding current folder for rename)
     * UC-006: Rename validation for root folders
     */
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.parentFolder IS NULL " +
           "AND f.name = :name " +
           "AND f.id != :excludeFolderId " +
           "AND f.deletedAt IS NULL")
    boolean existsByUserIdAndRootAndNameExcluding(
        @Param("userId") UUID userId,
        @Param("name") String name,
        @Param("excludeFolderId") UUID excludeFolderId
    );

    /**
     * Check if folder name exists in parent (excluding current folder for rename)
     * UC-006: Rename validation
     */
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.parentFolder.id = :parentFolderId " +
           "AND f.name = :name " +
           "AND f.id != :excludeFolderId " +
           "AND f.deletedAt IS NULL")
    boolean existsByUserIdAndParentFolderIdAndNameExcluding(
        @Param("userId") UUID userId,
        @Param("parentFolderId") UUID parentFolderId,
        @Param("name") String name,
        @Param("excludeFolderId") UUID excludeFolderId
    );

    // ==================== Hierarchy Queries (Materialized Path) ====================

    /**
     * Find all descendants of folder (children, grandchildren, etc.)
     * UC-007: Move folder - Update descendant paths
     * UC-008: Copy folder - Copy all descendants
     * UC-009: Delete folder - Cascade delete descendants
     *
     * Uses materialized path: WHERE path LIKE '/parent_id/%'
     */
    @Query("SELECT f FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.path LIKE CONCAT(:folderPath, '/%') " +
           "AND f.deletedAt IS NULL " +
           "ORDER BY f.depth ASC")
    List<Folder> findAllDescendants(@Param("userId") UUID userId, @Param("folderPath") String folderPath);

    /**
     * Find direct children of folder (depth = parent.depth + 1)
     * UC-005: Get immediate children for tree view
     */
    @Query("SELECT f FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.parentFolder.id = :parentFolderId " +
           "AND f.deletedAt IS NULL " +
           "ORDER BY f.name ASC")
    List<Folder> findChildrenByParentId(@Param("userId") UUID userId, @Param("parentFolderId") UUID parentFolderId);

    /**
     * Get max descendant depth (for move/copy validation)
     * UC-007: Move folder - Check max depth constraint
     * UC-008: Copy folder - Check max depth constraint
     */
    @Query("SELECT COALESCE(MAX(f.depth), 0) FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND (f.path = :folderPath OR f.path LIKE CONCAT(:folderPath, '/%')) " +
           "AND f.deletedAt IS NULL")
    Integer findMaxDescendantDepth(@Param("userId") UUID userId, @Param("folderPath") String folderPath);

    /**
     * Count total descendants (folders + decks)
     * UC-008: Copy folder - Check size threshold (sync vs async)
     */
    @Query("SELECT COUNT(f) FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.path LIKE CONCAT(:folderPath, '/%') " +
           "AND f.deletedAt IS NULL")
    Long countDescendants(@Param("userId") UUID userId, @Param("folderPath") String folderPath);

    // ==================== Circular Reference Check ====================

    /**
     * Check if target folder is descendant of source folder
     * UC-007: Move folder - Prevent circular reference
     *
     * Returns true if targetPath starts with sourcePath (circular reference)
     */
    @Query("SELECT COUNT(f) > 0 FROM Folder f " +
           "WHERE f.id = :targetFolderId " +
           "AND f.path LIKE CONCAT(:sourceFolderPath, '%') " +
           "AND f.deletedAt IS NULL")
    boolean isDescendantOf(
        @Param("targetFolderId") UUID targetFolderId,
        @Param("sourceFolderPath") String sourceFolderPath
    );

    // ==================== Breadcrumb Path ====================

    /**
     * Get breadcrumb path (all ancestors from root to folder)
     * UC-010: Display folder breadcrumb navigation
     *
     * Example: Home > English Learning > IELTS > Vocabulary
     */
    @Query("SELECT f FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND :folderPath LIKE CONCAT(f.path, '%') " +
           "AND f.deletedAt IS NULL " +
           "ORDER BY f.depth ASC")
    List<Folder> findAncestors(@Param("userId") UUID userId, @Param("folderPath") String folderPath);

    // ==================== Soft Delete Queries ====================

    /**
     * Find soft-deleted folders (for trash view)
     * Future: UC-011 - Restore from Trash
     */
    @Query("SELECT f FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND f.deletedAt IS NOT NULL " +
           "ORDER BY f.deletedAt DESC")
    List<Folder> findDeletedByUserId(@Param("userId") UUID userId);

    /**
     * Hard delete folder and all descendants (permanent delete)
     * UC-009: Permanent delete from trash
     */
    @Query("DELETE FROM Folder f " +
           "WHERE f.user.id = :userId " +
           "AND (f.id = :folderId OR f.path LIKE CONCAT(:folderPath, '/%'))")
    void hardDeleteFolderAndDescendants(@Param("userId") UUID userId,
                                       @Param("folderId") UUID folderId,
                                       @Param("folderPath") String folderPath);

    // ==================== Batch Operations ====================

    /**
     * Update paths for all descendants after move
     * UC-007: Move folder - Update descendant paths
     *
     * This is a native query for bulk path update (more efficient)
     */
    @Query(value = "UPDATE folders SET " +
                   "path = REPLACE(path, :oldPathPrefix, :newPathPrefix), " +
                   "depth = depth + :depthDelta, " +
                   "updated_at = NOW() " +
                   "WHERE user_id = :userId " +
                   "AND path LIKE CONCAT(:oldPathPrefix, '%') " +
                   "AND deleted_at IS NULL",
           nativeQuery = true)
    int updateDescendantPaths(@Param("userId") UUID userId,
                             @Param("oldPathPrefix") String oldPathPrefix,
                             @Param("newPathPrefix") String newPathPrefix,
                             @Param("depthDelta") Integer depthDelta);
}
