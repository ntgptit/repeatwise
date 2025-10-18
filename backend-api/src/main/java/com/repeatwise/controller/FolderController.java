package com.repeatwise.controller;

import com.repeatwise.dto.request.folder.CopyFolderRequest;
import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.CopyJobResponse;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.dto.response.folder.FolderTreeResponse;
import com.repeatwise.security.SecurityUtils;
import com.repeatwise.service.IFolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Folder Management
 *
 * Requirements:
 * - UC-005: Create Folder Hierarchy
 * - UC-006: Rename Folder
 * - UC-007: Move Folder
 * - UC-008: Copy Folder
 * - UC-009: Delete Folder
 * - UC-010: View Folder Statistics
 *
 * Endpoints:
 * - GET    /api/folders              - Get folder tree
 * - GET    /api/folders/{id}         - Get folder details
 * - POST   /api/folders              - Create folder
 * - PUT    /api/folders/{id}         - Update folder
 * - POST   /api/folders/{id}/move    - Move folder
 * - POST   /api/folders/{id}/copy    - Copy folder
 * - DELETE /api/folders/{id}         - Soft delete folder
 * - POST   /api/folders/{id}/restore - Restore folder
 * - DELETE /api/folders/{id}/permanent - Hard delete folder
 * - GET    /api/folders/{id}/stats   - Get folder statistics (UC-010)
 *
 * @author RepeatWise Team
 */
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@Slf4j
public class FolderController {

    private final IFolderService folderService;

    // ==================== UC-005: Create Folder Hierarchy ====================

    /**
     * Create a new folder (UC-005)
     *
     * Requirements:
     * - UC-005: Create Folder Hierarchy
     * - BR-010: Folder naming (1-100 chars, trim whitespace)
     * - BR-011: Max depth = 10 levels
     * - BR-013: Unique name within same parent
     *
     * @param request Create folder request
     * @return Created folder response with 201 Created status
     */
    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody final CreateFolderRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/folders - Creating folder: name={}, userId={}",
            request.getName(), userId);

        final FolderResponse response = folderService.createFolder(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== UC-006: Rename Folder ====================

    /**
     * Update folder (rename and update description) (UC-006)
     *
     * Requirements:
     * - UC-006: Rename Folder
     * - BR-014: Rename validation (unique name within parent)
     * - BR-015: Only name and description can be changed
     *
     * @param folderId Folder ID to update
     * @param request Update folder request
     * @return Updated folder response
     */
    @PutMapping("/{folderId}")
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable final UUID folderId,
            @Valid @RequestBody final UpdateFolderRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("PUT /api/folders/{} - Updating folder: userId={}", folderId, userId);

        final FolderResponse response = folderService.updateFolder(folderId, request, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-007: Move Folder ====================

    /**
     * Move folder to new parent (UC-007)
     *
     * Requirements:
     * - UC-007: Move Folder
     * - BR-017: Move validation (circular ref, depth, uniqueness)
     * - BR-018: Path recalculation for all descendants
     * - BR-019: Depth recalculation with delta
     *
     * @param folderId Folder ID to move
     * @param request Move folder request
     * @return Moved folder response
     */
    @PostMapping("/{folderId}/move")
    public ResponseEntity<FolderResponse> moveFolder(
            @PathVariable final UUID folderId,
            @Valid @RequestBody final MoveFolderRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/folders/{}/move - Moving folder: userId={}", folderId, userId);

        final FolderResponse response = folderService.moveFolder(folderId, request, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-008: Copy Folder ====================

    /**
     * Copy folder (UC-008)
     *
     * Requirements:
     * - UC-008: Copy Folder
     * - BR-021: Copy scope (folders, decks, cards)
     * - BR-023: Async threshold (>50 items)
     * - BR-024: Auto-naming (Copy, Copy 2, Copy 3)
     * - BR-025: Depth validation
     *
     * @param folderId Source folder ID to copy
     * @param request Copy folder request
     * @return Copied folder response (sync) or job response (async)
     */
    @PostMapping("/{folderId}/copy")
    public ResponseEntity<FolderResponse> copyFolder(
            @PathVariable final UUID folderId,
            @Valid @RequestBody final CopyFolderRequest request) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/folders/{}/copy - Copying folder: userId={}", folderId, userId);

        final FolderResponse response = folderService.copyFolder(folderId, request, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get copy job status (UC-008 - Async copy)
     *
     * @param jobId Copy job ID
     * @return Copy job status response
     */
    @GetMapping("/copy-status/{jobId}")
    public ResponseEntity<CopyJobResponse> getCopyJobStatus(@PathVariable final UUID jobId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/folders/copy-status/{} - Getting copy job status: userId={}",
            jobId, userId);

        final CopyJobResponse response = folderService.getCopyJobStatus(jobId, userId);

        return ResponseEntity.ok(response);
    }

    // ==================== UC-009: Delete Folder ====================

    /**
     * Soft delete folder (UC-009)
     *
     * Requirements:
     * - UC-009: Delete Folder
     * - BR-026: Soft delete (set deleted_at timestamp)
     * - BR-027: Cascade delete to descendants, decks, cards
     *
     * @param folderId Folder ID to delete
     * @return 204 No Content
     */
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("DELETE /api/folders/{} - Soft-deleting folder: userId={}", folderId, userId);

        folderService.deleteFolder(folderId, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Restore soft-deleted folder from trash (UC-009)
     *
     * Requirements:
     * - UC-009: Delete Folder - Step 5 (Undo)
     * - BR-026: Soft delete recovery
     * - BR-027: Cascade restore to descendants
     *
     * @param folderId Folder ID to restore
     * @return Restored folder response
     */
    @PostMapping("/{folderId}/restore")
    public ResponseEntity<FolderResponse> restoreFolder(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("POST /api/folders/{}/restore - Restoring folder: userId={}", folderId, userId);

        final FolderResponse response = folderService.restoreFolder(folderId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Permanently delete folder from trash (UC-009 A4)
     *
     * Requirements:
     * - UC-009: Delete Folder - A4: Delete from Trash
     * - BR-028: Permanent delete (hard delete)
     *
     * WARNING: This action is irreversible!
     *
     * @param folderId Folder ID to permanently delete
     * @return 204 No Content
     */
    @DeleteMapping("/{folderId}/permanent")
    public ResponseEntity<Void> permanentlyDeleteFolder(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.warn("DELETE /api/folders/{}/permanent - Permanently deleting folder: userId={}",
            folderId, userId);

        folderService.permanentlyDeleteFolder(folderId, userId);

        return ResponseEntity.noContent().build();
    }

    // ==================== UC-010: View Folder Statistics ====================

    /**
     * Get folder statistics (UC-010)
     *
     * Requirements:
     * - UC-010: View Folder Statistics
     * - BR-020: Cache TTL = 5 minutes
     *
     * Statistics include:
     * - Total cards (recursive, including all descendants)
     * - Due cards (due_date <= today)
     * - New cards (review_count = 0)
     * - Learning cards (box 1-4)
     * - Mature cards (box 5-7)
     *
     * Cache behavior:
     * - If cache valid (< 5 min): Returns cached stats immediately
     * - If cache stale/missing: Calculates and caches new stats
     *
     * @param folderId Folder ID to get stats for
     * @return Folder statistics response
     */
    @GetMapping("/{folderId}/stats")
    public ResponseEntity<FolderStatsResponse> getFolderStats(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/folders/{}/stats - Getting folder statistics: userId={}",
            folderId, userId);

        final FolderStatsResponse response = folderService.getFolderStats(folderId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Invalidate folder statistics cache (UC-010)
     *
     * Requirements:
     * - UC-010: View Folder Statistics
     * - BR-029: Cache invalidation on content changes
     *
     * Use cases:
     * - Called when folder contents change (cards added/removed, decks moved, etc.)
     * - Called on-demand by user to force refresh
     *
     * @param folderId Folder ID to invalidate stats for
     * @return 204 No Content
     */
    @DeleteMapping("/{folderId}/stats")
    public ResponseEntity<Void> invalidateFolderStats(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("DELETE /api/folders/{}/stats - Invalidating folder statistics: userId={}",
            folderId, userId);

        folderService.invalidateFolderStats(folderId, userId);

        return ResponseEntity.noContent().build();
    }

    // ==================== Folder Tree & Details ====================

    /**
     * Get folder tree (for sidebar/navigation)
     *
     * @param maxDepth Maximum depth to fetch (default 10)
     * @return List of folders in tree structure
     */
    @GetMapping
    public ResponseEntity<List<FolderTreeResponse>> getFolderTree(
            @RequestParam(defaultValue = "10") final Integer maxDepth) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/folders - Getting folder tree: userId={}, maxDepth={}",
            userId, maxDepth);

        final List<FolderTreeResponse> response = folderService.getFolderTree(userId, maxDepth);

        return ResponseEntity.ok(response);
    }

    /**
     * Get folder details by ID
     *
     * @param folderId Folder ID
     * @return Folder details response
     */
    @GetMapping("/{folderId}")
    public ResponseEntity<FolderResponse> getFolderById(@PathVariable final UUID folderId) {

        final UUID userId = SecurityUtils.getCurrentUserId();

        log.info("GET /api/folders/{} - Getting folder details: userId={}", folderId, userId);

        final FolderResponse response = folderService.getFolderById(folderId, userId);

        return ResponseEntity.ok(response);
    }
}
