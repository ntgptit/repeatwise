package com.repeatwise.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.repeatwise.entity.Folder;

/**
 * Repository for Folder entity
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    /**
     * Find all active folders for a user (excluding soft-deleted)
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL ORDER BY f.path")
    List<Folder> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Find active folder by ID and user ID
     */
    @Query("SELECT f FROM Folder f WHERE f.id = :folderId AND f.user.id = :userId AND f.deletedAt IS NULL")
    Optional<Folder> findByIdAndUserId(@Param("folderId") UUID folderId, @Param("userId") UUID userId);

    /**
     * Find all root folders for a user (no parent)
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.parentFolder IS NULL AND f.deletedAt IS NULL ORDER BY f.name")
    List<Folder> findRootFoldersByUserId(@Param("userId") UUID userId);

    /**
     * Find all child folders of a parent folder
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.parentFolder.id = :parentId AND f.deletedAt IS NULL ORDER BY f.name")
    List<Folder> findChildrenByUserIdAndParentId(@Param("userId") UUID userId, @Param("parentId") UUID parentId);

    /**
     * Find all descendants of a folder using path pattern (for move/copy/delete operations)
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.path LIKE CONCAT(:pathPrefix, '%') AND f.deletedAt IS NULL")
    List<Folder> findDescendantsByPath(@Param("userId") UUID userId, @Param("pathPrefix") String pathPrefix);

    /**
     * Check if folder name exists for user under same parent (case-insensitive)
     */
    @Query("""
            SELECT COUNT(f) > 0 FROM Folder f WHERE f.user.id = :userId \
            AND (:parentId IS NULL AND f.parentFolder IS NULL OR f.parentFolder.id = :parentId) \
            AND LOWER(f.name) = LOWER(:name) AND f.deletedAt IS NULL""")
    boolean existsByNameAndParent(@Param("userId") UUID userId,
            @Param("parentId") UUID parentId,
            @Param("name") String name);

    /**
     * Check if folder name exists for user under same parent, excluding specific folder ID
     */
    @Query("""
            SELECT COUNT(f) > 0 FROM Folder f WHERE f.user.id = :userId \
            AND (:parentId IS NULL AND f.parentFolder IS NULL OR f.parentFolder.id = :parentId) \
            AND LOWER(f.name) = LOWER(:name) AND f.id <> :excludeId AND f.deletedAt IS NULL""")
    boolean existsByNameAndParentExcludingId(@Param("userId") UUID userId,
            @Param("parentId") UUID parentId,
            @Param("name") String name,
            @Param("excludeId") UUID excludeId);

    /**
     * Count total folders for a user (active only)
     */
    @Query("SELECT COUNT(f) FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NULL")
    long countByUserId(@Param("userId") UUID userId);

    /**
     * Get maximum depth in a subtree (for validation during move/copy)
     */
    @Query("SELECT MAX(f.depth) FROM Folder f WHERE f.user.id = :userId AND f.path LIKE CONCAT(:pathPrefix, '%') AND f.deletedAt IS NULL")
    Integer getMaxDepthInSubtree(@Param("userId") UUID userId, @Param("pathPrefix") String pathPrefix);

    /**
     * Count total items (folders + decks) in a folder subtree
     */
    @Query("""
            SELECT COUNT(f) + \
            (SELECT COUNT(d) FROM Deck d WHERE d.folder.id IN \
            (SELECT f2.id FROM Folder f2 WHERE f2.user.id = :userId AND f2.path LIKE CONCAT(:pathPrefix, '%') AND f2.deletedAt IS NULL)) \
            FROM Folder f WHERE f.user.id = :userId AND f.path LIKE CONCAT(:pathPrefix, '%') AND f.deletedAt IS NULL""")
    long countItemsInSubtree(@Param("userId") UUID userId, @Param("pathPrefix") String pathPrefix);

    /**
     * Find folders deleted by user (in trash)
     */
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId AND f.deletedAt IS NOT NULL ORDER BY f.deletedAt DESC")
    List<Folder> findDeletedByUserId(@Param("userId") UUID userId);

    /**
     * Find soft-deleted folder by ID and user ID
     */
    @Query("SELECT f FROM Folder f WHERE f.id = :folderId AND f.user.id = :userId AND f.deletedAt IS NOT NULL")
    Optional<Folder> findDeletedByIdAndUserId(@Param("folderId") UUID folderId, @Param("userId") UUID userId);
}
