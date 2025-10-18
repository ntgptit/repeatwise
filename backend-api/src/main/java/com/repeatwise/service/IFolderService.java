package com.repeatwise.service;

import com.repeatwise.dto.request.folder.CopyFolderRequest;
import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.CopyJobResponse;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.dto.response.folder.FolderTreeResponse;

import java.util.List;
import java.util.UUID;

/**
 * Folder Service Interface
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy
 * - UC-006: Rename Folder
 * - UC-007: Move Folder
 * - UC-008: Copy Folder
 * - UC-009: Delete Folder
 * - UC-010: View Folder Statistics
 *
 * Business Rules:
 * - BR-010 to BR-030: Folder management rules
 *
 * @author RepeatWise Team
 */
public interface IFolderService {

    // ==================== UC-005: Create Folder Hierarchy ====================

    /**
     * Create a new folder
     *
     * Requirements:
     * - UC-005: Create Folder Hierarchy
     * - BR-010: Folder naming
     * - BR-011: Max depth = 10
     * - BR-013: Unique name within parent
     *
     * Validation:
     * - Name must be 1-100 chars, trimmed
     * - Parent must exist and belong to user (if not null)
     * - Depth must not exceed 10
     * - Name must be unique in parent
     *
     * @param request CreateFolderRequest
     * @param userId  Current user ID
     * @return Created folder details
     * @throws FolderNameExistsException  if name exists in parent
     * @throws MaxDepthExceededException if depth would exceed 10
     * @throws ResourceNotFoundException if parent not found
     */
    FolderResponse createFolder(CreateFolderRequest request, UUID userId);

    /**
     * Get folder tree for user (with statistics)
     *
     * Requirements:
     * - UC-005: Display folder tree
     * - UC-010: View folder statistics
     *
     * Returns:
     * - All folders sorted by path
     * - With card statistics from FolderStats
     *
     * @param userId   Current user ID
     * @param maxDepth Max depth to retrieve (optional, default 10)
     * @return List of folders with statistics
     */
    List<FolderTreeResponse> getFolderTree(UUID userId, Integer maxDepth);

    /**
     * Get folder details by ID
     *
     * Requirements:
     * - UC-005: View folder details
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     * @return Folder details
     * @throws ResourceNotFoundException if folder not found or not owned by user
     */
    FolderResponse getFolderById(UUID folderId, UUID userId);

    // ==================== UC-006: Rename Folder ====================

    /**
     * Update folder (rename and change description)
     *
     * Requirements:
     * - UC-006: Rename Folder
     * - BR-014: Rename validation
     * - BR-015: Only name and description can be changed
     *
     * Validation:
     * - Name must be unique in parent (excluding current folder)
     * - Name must be 1-100 chars
     *
     * @param folderId Folder ID
     * @param request  UpdateFolderRequest
     * @param userId   Current user ID
     * @return Updated folder
     * @throws FolderNameExistsException if name exists in parent
     * @throws ResourceNotFoundException if folder not found
     */
    FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId);

    // ==================== UC-007: Move Folder ====================

    /**
     * Move folder to new parent
     *
     * Requirements:
     * - UC-007: Move Folder
     * - BR-017: Move validation (no circular ref, max depth)
     * - BR-018: Path recalculation
     * - BR-019: Depth recalculation
     *
     * Validation:
     * - Cannot move into self or descendants (circular ref)
     * - Resulting depth must not exceed 10
     * - Name must be unique in target parent
     *
     * Operations:
     * - Update folder parent_folder_id, path, depth
     * - Update all descendant paths and depths (batch)
     * - Invalidate folder_stats for old and new parent chains
     *
     * @param folderId Folder ID
     * @param request  MoveFolderRequest
     * @param userId   Current user ID
     * @return Moved folder
     * @throws CircularReferenceException if target is descendant
     * @throws MaxDepthExceededException  if depth would exceed 10
     * @throws FolderNameExistsException  if name exists in target
     */
    FolderResponse moveFolder(UUID folderId, MoveFolderRequest request, UUID userId);

    // ==================== UC-008: Copy Folder ====================

    /**
     * Copy folder synchronously (for small folders < 50 items)
     *
     * Requirements:
     * - UC-008: Copy Folder
     * - BR-021: Copy scope
     * - BR-022: SRS state reset
     * - BR-023: Async threshold
     *
     * Validation:
     * - Folder must have < 50 items (sync limit)
     * - Resulting depth must not exceed 10
     *
     * Operations:
     * - Create new folder with new UUID
     * - Copy all sub-folders (recursive)
     * - Copy all decks and cards
     * - Reset all cards to Box 1
     *
     * @param folderId Folder ID to copy
     * @param request  CopyFolderRequest
     * @param userId   Current user ID
     * @return Copied folder
     * @throws FolderTooLargeException   if folder has >50 items
     * @throws MaxDepthExceededException if depth would exceed 10
     */
    FolderResponse copyFolder(UUID folderId, CopyFolderRequest request, UUID userId);

    /**
     * Copy folder asynchronously (for large folders >= 50 items)
     *
     * Requirements:
     * - UC-008: Copy Folder - A1: Large Folder
     * - BR-023: Async threshold (>50 items or >1000 cards)
     *
     * Returns:
     * - Job ID for tracking progress
     * - Use getCopyJobStatus() to check progress
     *
     * @param folderId Folder ID to copy
     * @param request  CopyFolderRequest
     * @param userId   Current user ID
     * @return Copy job details
     */
    CopyJobResponse copyFolderAsync(UUID folderId, CopyFolderRequest request, UUID userId);

    /**
     * Get copy job status
     *
     * Requirements:
     * - UC-008: Track async copy job progress
     *
     * @param jobId  Job ID
     * @param userId Current user ID
     * @return Job status and progress
     * @throws ResourceNotFoundException if job not found
     */
    CopyJobResponse getCopyJobStatus(UUID jobId, UUID userId);

    // ==================== UC-009: Delete Folder ====================

    /**
     * Soft-delete folder (and all descendants)
     *
     * Requirements:
     * - UC-009: Delete Folder
     * - BR-026: Soft delete
     * - BR-027: Cascade delete
     *
     * Operations:
     * - Set deleted_at for folder and all descendants
     * - Set deleted_at for all decks and cards (cascade)
     * - Invalidate folder_stats for parent chain
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     * @throws ResourceNotFoundException if folder not found
     */
    void deleteFolder(UUID folderId, UUID userId);

    /**
     * Restore soft-deleted folder (undo delete)
     *
     * Requirements:
     * - UC-009: Undo delete (5 seconds window)
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     * @return Restored folder
     */
    FolderResponse restoreFolder(UUID folderId, UUID userId);

    /**
     * Permanently delete folder (hard delete)
     *
     * Requirements:
     * - UC-009: Permanent delete from trash
     * - BR-028: Hard delete only from trash
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     */
    void permanentlyDeleteFolder(UUID folderId, UUID userId);

    // ==================== UC-010: View Folder Statistics ====================

    /**
     * Get folder statistics (with caching)
     *
     * Requirements:
     * - UC-010: View Folder Statistics
     * - BR-020: Stats with 5-min TTL
     *
     * Returns:
     * - Total cards (folder + descendants)
     * - Due cards
     * - New cards
     * - Mature cards
     *
     * Behavior:
     * - If stats exist and not stale: Return cached
     * - If stats stale or missing: Recompute and cache
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     * @return Folder statistics
     */
    FolderStatsResponse getFolderStats(UUID folderId, UUID userId);

    /**
     * Invalidate folder statistics cache
     *
     * Requirements:
     * - BR-020: Stats invalidation on changes
     *
     * Called when:
     * - Cards added/removed
     * - Folder moved
     * - Review submitted
     *
     * @param folderId Folder ID
     * @param userId   Current user ID
     */
    void invalidateFolderStats(UUID folderId, UUID userId);
}
