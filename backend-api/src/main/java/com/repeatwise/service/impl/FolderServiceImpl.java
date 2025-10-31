package com.repeatwise.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.folder.CopyFolderRequest;
import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.CopyJobResponse;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.dto.response.folder.FolderTreeResponse;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.FolderStats;
import com.repeatwise.entity.User;
import com.repeatwise.exception.CircularReferenceException;
import com.repeatwise.exception.FolderNameExistsException;
import com.repeatwise.exception.FolderTooLargeException;
import com.repeatwise.exception.MaxDepthExceededException;
import com.repeatwise.exception.ResourceNotFoundException;
import com.repeatwise.exception.ValidationException;
import com.repeatwise.mapper.FolderMapper;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.FolderStatsRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.IFolderService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of Folder Service
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
 * - BR-010: Folder naming (1-100 chars, trim whitespace)
 * - BR-011: Max depth = 10 levels
 * - BR-012: Materialized path format
 * - BR-013: Unique name within same parent
 *
 * Coding Standards:
 * - Method max 30 lines
 * - Use Apache Commons for string operations
 * - Use MessageSource for error messages
 * - Early return & Guard clauses
 * - Final for immutable variables
 * - Logging with context
 *
 * @author RepeatWise Team
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class FolderServiceImpl extends BaseService implements IFolderService {

    // ==================== Constants ====================

    private static final int MAX_FOLDER_DEPTH = 10;

    // ==================== Dependencies ====================

    private final FolderRepository folderRepository;
    private final FolderStatsRepository folderStatsRepository;
    private final UserRepository userRepository;
    private final FolderMapper folderMapper;

    public FolderServiceImpl(
            final FolderRepository folderRepository,
            final FolderStatsRepository folderStatsRepository,
            final UserRepository userRepository,
            final FolderMapper folderMapper,
            final MessageSource messageSource) {
        super(messageSource);
        this.folderRepository = folderRepository;
        this.folderStatsRepository = folderStatsRepository;
        this.userRepository = userRepository;
        this.folderMapper = folderMapper;
    }

    // ==================== UC-005: Create Folder Hierarchy ====================

    /**
     * Build folder entity from request
     */
    private Folder buildFolder(final CreateFolderRequest request, final User user, final Folder parentFolder) {
        final var trimmedName = StringUtils.trim(request.getName());
        final var trimmedDescription = StringUtils.trim(request.getDescription());

        final var depth = parentFolder == null ? 0 : parentFolder.getDepth() + 1;

        final Folder folder = Folder.builder()
                .name(trimmedName)
                .description(trimmedDescription)
                .user(user)
                .parentFolder(parentFolder)
                .depth(depth)
                .build();

        // Calculate path (called after save to get ID, but we need to set a temporary path)
        // Note: Path will be updated after save via @PrePersist hook or manual update
        folder.calculatePath();

        return folder;
    }

    // ==================== Helper Methods (Private) ====================

    /**
     * Build FolderStatsResponse from entity
     */
    private FolderStatsResponse buildStatsResponse(
            final Folder folder,
            final FolderStats stats,
            final boolean isStale) {

        // Calculate learning cards (boxes 1-4)
        final var learningCards = stats.getTotalCardsCount() - stats.getMatureCardsCount();

        return FolderStatsResponse.builder()
                .folderId(folder.getId())
                .folderName(folder.getName())
                .totalCards(stats.getTotalCardsCount())
                .dueCards(stats.getDueCardsCount())
                .newCards(stats.getNewCardsCount())
                .learningCards(learningCards)
                .matureCards(stats.getMatureCardsCount())
                .lastComputedAt(stats.getLastComputedAt())
                .isStale(isStale)
                .build();
    }

    /**
     * Calculate and cache folder statistics
     *
     * For MVP: Only count folders (no decks/cards yet)
     * TODO: Add deck/card counting when entities ready
     */
    private FolderStats calculateAndCacheStats(
            final Folder folder,
            final Optional<FolderStats> existingStats) {

        log.debug("Calculating stats for folder: folderId={}", folder.getId());

        // For MVP: Return zero stats (no decks/cards implemented yet)
        // TODO: Calculate actual stats when Deck and Card entities are ready
        final var totalCards = 0;
        final var dueCards = 0;
        final var newCards = 0;
        final var matureCards = 0;

        // Create or update stats entity
        final var stats = existingStats.orElseGet(() -> FolderStats.builder()
                .folder(folder)
                .user(folder.getUser())
                .build());

        stats.updateStats(totalCards, dueCards, newCards, matureCards);

        // Save to cache
        return this.folderStatsRepository.save(stats);
    }

    /**
     * Copy folder synchronously (for small folders)
     *
     * Requirements:
     * - UC-008: Copy Folder
     * - BR-021: Copy scope (folders, decks, cards)
     * - BR-022: SRS state reset (all cards to Box 1)
     * - BR-023: Async threshold (>50 items)
     * - BR-024: Auto-naming (Copy, Copy 2, Copy 3)
     * - BR-025: Depth validation
     *
     * Steps:
     * 1. Validate request and get source folder
     * 2. Get target parent folder
     * 3. Validate copy constraints (depth)
     * 4. Generate unique name (auto-rename if duplicate)
     * 5. Perform recursive copy
     * 6. Return copied folder
     *
     * Note: For MVP, only copy folder structure (no decks/cards yet)
     * TODO: Add deck/card copying when entities are ready
     *
     * @param folderId Source folder ID to copy
     * @param request  Copy request with target parent and options
     * @param userId   Current user ID
     * @return Copied folder response
     * @throws ResourceNotFoundException if source folder not found
     * @throws MaxDepthExceededException if copy would exceed max depth
     * @throws FolderTooLargeException   if folder too large for sync copy
     */
    @Transactional
    @Override
    public FolderResponse copyFolder(final UUID folderId, final CopyFolderRequest request, final UUID userId) {
        // Guard clause: Validate request
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(request, "CopyFolderRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Copying folder: folderId={}, targetParentId={}, newName={}, userId={}",
                folderId, request.getTargetParentFolderId(), request.getNewName(), userId);

        // Validate copy name
        validateFolderName(request.getNewName());

        // Get source folder with ownership check
        final var sourceFolder = getFolderByIdAndUserId(folderId, userId);

        // Get target parent folder (null if copying to root)
        final var targetParentFolder = getTargetParentFolder(request.getTargetParentFolderId(), userId);

        // Validate copy operation
        validateCopyOperation(sourceFolder, targetParentFolder);

        // Generate unique name (handle duplicates with auto-rename)
        final var uniqueName = generateUniqueCopyName(
                request.getNewName(),
                targetParentFolder,
                userId);

        // Perform recursive copy
        final var copiedFolder = performCopy(sourceFolder, targetParentFolder, uniqueName, request);

        log.info("Folder copied successfully: sourceFolderId={}, copiedFolderId={}, userId={}",
                folderId, copiedFolder.getId(), userId);

        return this.folderMapper.toResponse(copiedFolder);
    }

    @Override
    public CopyJobResponse copyFolderAsync(final UUID folderId, final CopyFolderRequest request, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Recursively copy all sub-folders
     */
    private void copySubFolders(
            final Folder sourceFolder,
            final Folder copiedParentFolder,
            final CopyFolderRequest request) {

        // Get all direct children of source folder
        final var children = this.folderRepository.findChildrenByParentId(
                sourceFolder.getUser().getId(),
                sourceFolder.getId());

        if (children.isEmpty()) {
            log.debug("No sub-folders to copy for folder: {}", sourceFolder.getId());
            return;
        }

        log.debug("Copying {} sub-folders for folder: {}", children.size(), sourceFolder.getId());

        // Recursively copy each child
        for (final Folder child : children) {
            // Use child's original name (no auto-rename for sub-folders)
            final var childName = child.getName();

            // Recursive call
            performCopy(child, copiedParentFolder, childName, request);
        }

        log.debug("Completed copying {} sub-folders", children.size());
    }

    /**
     * Create copied folder entity with new ID
     */
    private Folder createCopiedFolder(
            final Folder sourceFolder,
            final Folder targetParentFolder,
            final String newName) {

        final var newDepth = targetParentFolder == null ? 0 : targetParentFolder.getDepth() + 1;

        final Folder copiedFolder = Folder.builder()
                .name(newName)
                .description(sourceFolder.getDescription())
                .user(sourceFolder.getUser())
                .parentFolder(targetParentFolder)
                .depth(newDepth)
                .build();

        // Calculate path (will be finalized after save when ID is available)
        copiedFolder.calculatePath();

        return copiedFolder;
    }

    /**
     * Create a new folder
     *
     * Requirements:
     * - UC-005: Create Folder Hierarchy
     * - BR-010: Folder naming
     * - BR-011: Max depth = 10
     * - BR-013: Unique name within parent
     *
     * Steps:
     * 1. Validate request (not null, name not blank)
     * 2. Get user (verify exists)
     * 3. Get parent folder (if specified)
     * 4. Validate depth limit
     * 5. Validate name uniqueness
     * 6. Build folder entity
     * 7. Save folder
     * 8. Return response
     */
    @Transactional
    @Override
    public FolderResponse createFolder(final CreateFolderRequest request, final UUID userId) {
        // Guard clause: Validate request
        Objects.requireNonNull(request, "CreateFolderRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Creating folder: name={}, parentFolderId={}, userId={}",
                request.getName(), request.getParentFolderId(), userId);

        // Validate name
        validateFolderName(request.getName());

        // Get user
        final var user = getUser(userId);

        // Get parent folder (if specified)
        final var parentFolder = getParentFolder(request.getParentFolderId(), userId);

        // Validate depth
        validateDepth(parentFolder);

        // Validate name uniqueness
        validateNameUniqueness(request.getName(), parentFolder, userId);

        // Build folder
        final var folder = buildFolder(request, user, parentFolder);

        // Save folder
        final var savedFolder = this.folderRepository.save(folder);

        log.info("Folder created successfully: folderId={}, name={}, userId={}",
                savedFolder.getId(), savedFolder.getName(), userId);

        return this.folderMapper.toResponse(savedFolder);
    }

    /**
     * Soft-delete folder and all descendants (UC-009)
     *
     * Requirements:
     * - UC-009: Delete Folder
     * - BR-026: Soft delete (set deleted_at timestamp)
     * - BR-027: Cascade delete to descendants, decks, cards
     * - BR-029: Update folder_stats for parent chain
     *
     * Steps:
     * 1. Validate folder exists and not already deleted
     * 2. Soft-delete folder and all descendants
     * 3. Soft-delete decks in folder tree (when Deck entity ready)
     * 4. Soft-delete cards in those decks (when Card entity ready)
     * 5. Update folder_stats (when implemented)
     *
     * Note: For MVP, only soft-delete folder structure
     * TODO: Add deck/card cascade delete when entities ready
     *
     * @param folderId Folder ID to delete
     * @param userId   Current user ID
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder already deleted
     */
    @Transactional
    @Override
    public void deleteFolder(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Soft-deleting folder: folderId={}, userId={}", folderId, userId);

        // Get folder (must exist and not be deleted)
        final var folder = getFolderForDelete(folderId, userId);

        // Perform soft delete
        performSoftDelete(folder);

        log.info("Folder soft-deleted successfully: folderId={}, name={}, userId={}",
                folderId, folder.getName(), userId);
    }

    /**
     * Extract base name by removing existing (Copy N) suffix
     *
     * Examples:
     * - "Folder Name (Copy)" → "Folder Name"
     * - "Folder Name (Copy 2)" → "Folder Name"
     * - "Folder Name" → "Folder Name"
     */
    private String extractBaseName(final String name) {
        // Remove "(Copy)" or "(Copy N)" suffix
        final var trimmed = StringUtils.trim(name);

        // Pattern: ends with "(Copy)" or "(Copy N)"
        if (trimmed.endsWith(")")) {
            final var lastOpenParen = trimmed.lastIndexOf('(');
            if (lastOpenParen > 0) {
                final var suffix = trimmed.substring(lastOpenParen + 1, trimmed.length() - 1).trim();

                // Check if suffix is "Copy" or "Copy N"
                if ("Copy".equals(suffix) || suffix.matches("Copy \\d+")) {
                    return trimmed.substring(0, lastOpenParen).trim();
                }
            }
        }

        return trimmed;
    }

    /**
     * Generate unique name for copied folder (BR-024)
     *
     * Auto-rename strategy:
     * 1. Try requested name
     * 2. If exists: Try "{name} (Copy)"
     * 3. If exists: Try "{name} (Copy 2)", "{name} (Copy 3)", etc.
     *
     * @return Unique name that doesn't exist in target parent
     */
    private String generateUniqueCopyName(
            final String requestedName,
            final Folder targetParentFolder,
            final UUID userId) {

        final var baseName = StringUtils.trim(requestedName);

        // Try requested name first
        if (!isNameExistsInParent(baseName, targetParentFolder, userId)) {
            return baseName;
        }

        // Try with copy suffix
        final var copyPattern = extractBaseName(baseName);
        var copyNumber = 1;

        // Try "Name (Copy)", "Name (Copy 2)", "Name (Copy 3)", etc.
        while (copyNumber <= 100) { // Safety limit
            final var candidateName = copyNumber == 1
                    ? String.format("%s (Copy)", copyPattern)
                    : String.format("%s (Copy %d)", copyPattern, copyNumber);

            if (!isNameExistsInParent(candidateName, targetParentFolder, userId)) {
                log.debug("Generated unique copy name: original={}, unique={}",
                        baseName, candidateName);
                return candidateName;
            }

            copyNumber++;
        }

        // Fallback: Use UUID suffix (should never happen)
        final var fallbackName = String.format("%s (Copy %s)",
                copyPattern, UUID.randomUUID().toString().substring(0, 8));

        log.warn("Used fallback name with UUID: {}", fallbackName);
        return fallbackName;
    }

    @Override
    public CopyJobResponse getCopyJobStatus(final UUID jobId, final UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public FolderResponse getFolderById(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Getting folder by ID: folderId={}, userId={}", folderId, userId);

        // Get folder with ownership check
        final var folder = getFolderByIdAndUserId(folderId, userId);

        return this.folderMapper.toResponse(folder);
    }

    // ==================== UC-007: Move Folder - Helper Methods ====================

    /**
     * Get folder by ID and user ID (with ownership check)
     *
     * @throws ResourceNotFoundException if folder not found or not owned by user
     */
    private Folder getFolderByIdAndUserId(final UUID folderId, final UUID userId) {
        return this.folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> {
                    log.error("Folder not found or not authorized: folderId={}, userId={}", folderId, userId);
                    return new ResourceNotFoundException(
                            "FOLDER_002",
                            getMessage("error.folder.not.found", folderId));
                });
    }

    /**
     * Get folder for delete operation
     * Folder must exist and NOT be already deleted
     *
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder already deleted
     */
    private Folder getFolderForDelete(final UUID folderId, final UUID userId) {
        // Find folder including deleted ones
        final var folder = this.folderRepository.findById(folderId)
                .orElseThrow(() -> {
                    log.error("Folder not found for delete: folderId={}", folderId);
                    return new ResourceNotFoundException(
                            "FOLDER_002",
                            getMessage("error.folder.not.found", folderId));
                });

        // Check ownership
        if (!folder.getUser().getId().equals(userId)) {
            log.error("Folder does not belong to user: folderId={}, userId={}", folderId, userId);
            throw new ResourceNotFoundException(
                    "FOLDER_002",
                    getMessage("error.folder.not.found", folderId));
        }

        // Check if already deleted
        if (folder.getDeletedAt() != null) {
            log.warn("Folder already deleted: folderId={}, deletedAt={}", folderId, folder.getDeletedAt());
            throw new ValidationException(
                    "FOLDER_DELETE_001",
                    getMessage("error.folder.delete.already.deleted"));
        }

        return folder;
    }

    /**
     * Get folder for permanent delete operation
     * Folder must exist and be in trash (deleted_at IS NOT NULL)
     *
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder not in trash
     */
    private Folder getFolderForPermanentDelete(final UUID folderId, final UUID userId) {
        // Same validation as restore (must be in trash)
        return getFolderForRestore(folderId, userId);
    }

    /**
     * Get folder for restore operation
     * Folder must exist and be in trash (deleted_at IS NOT NULL)
     *
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder not in trash
     */
    private Folder getFolderForRestore(final UUID folderId, final UUID userId) {
        // Find folder including deleted ones
        final var folder = this.folderRepository.findById(folderId)
                .orElseThrow(() -> {
                    log.error("Folder not found for restore: folderId={}", folderId);
                    return new ResourceNotFoundException(
                            "FOLDER_002",
                            getMessage("error.folder.not.found", folderId));
                });

        // Check ownership
        if (!folder.getUser().getId().equals(userId)) {
            log.error("Folder does not belong to user: folderId={}, userId={}", folderId, userId);
            throw new ResourceNotFoundException(
                    "FOLDER_002",
                    getMessage("error.folder.not.found", folderId));
        }

        // Check if in trash (deleted)
        if (folder.getDeletedAt() == null) {
            log.warn("Folder not in trash, cannot restore: folderId={}", folderId);
            throw new ValidationException(
                    "FOLDER_RESTORE_001",
                    getMessage("error.folder.restore.not.deleted"));
        }

        return folder;
    }

    /**
     * Get folder statistics (with cache) (UC-010)
     *
     * Requirements:
     * - UC-010: View Folder Statistics
     * - BR-020: Cache TTL = 5 minutes
     *
     * Steps:
     * 1. Validate folder exists and user owns it
     * 2. Check cache for existing stats
     * 3. If cache valid (< 5 min): Return cached stats
     * 4. If cache stale/missing: Calculate and cache new stats
     * 5. Return stats response
     *
     * @param folderId Folder ID to get stats for
     * @param userId   Current user ID
     * @return Folder statistics response
     * @throws ResourceNotFoundException if folder not found
     */
    @Override
    public FolderStatsResponse getFolderStats(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Getting folder stats: folderId={}, userId={}", folderId, userId);

        // Validate folder exists and user owns it
        final var folder = getFolderByIdAndUserId(folderId, userId);

        // Check cache
        final var cachedStats = this.folderStatsRepository.findByFolderIdAndUserId(folderId, userId);

        if (cachedStats.isPresent() && !cachedStats.get().isStale()) {
            log.debug("Returning cached stats: folderId={}, lastComputedAt={}",
                    folderId, cachedStats.get().getLastComputedAt());
            return buildStatsResponse(folder, cachedStats.get(), false);
        }

        // Calculate new stats
        final var stats = calculateAndCacheStats(folder, cachedStats);

        log.info("Stats calculated and cached: folderId={}, totalCards={}",
                folderId, stats.getTotalCardsCount());

        return buildStatsResponse(folder, stats, false);
    }

    @Override
    public List<FolderTreeResponse> getFolderTree(final UUID userId, final Integer maxDepth) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);
        Objects.requireNonNull(maxDepth, "Max depth cannot be null");

        log.info("Getting folder tree: userId={}, maxDepth={}", userId, maxDepth);

        // Get all folders for user up to max depth
        final var folders = this.folderRepository.findByUserIdAndMaxDepth(userId, maxDepth);

        if (folders.isEmpty()) {
            log.debug("No folders found for user: userId={}", userId);
            return List.of();
        }

        // Get folder IDs for stats lookup
        final var folderIds = folders.stream()
                .map(Folder::getId)
                .toList();

        // Get stats for all folders (batch query)
        final var statsMap = this.folderStatsRepository.findByFolderIdsAndUserId(folderIds, userId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        fs -> fs.getFolder().getId(),
                        fs -> fs
                ));

        // Build tree structure
        return buildFolderTree(folders, statsMap);
    }

    /**
     * Build hierarchical tree structure from flat folder list
     *
     * Steps:
     * 1. Convert folders to tree responses with stats
     * 2. Build parent-child relationships
     * 3. Return only root folders (children are nested)
     */
    private List<FolderTreeResponse> buildFolderTree(
            final List<Folder> folders,
            final java.util.Map<UUID, FolderStats> statsMap) {

        // Convert all folders to tree responses
        final var folderMap = new java.util.HashMap<UUID, FolderTreeResponse>();
        for (final Folder folder : folders) {
            final var treeResponse = this.folderMapper.toTreeResponse(folder);
            enrichTreeResponseWithStats(treeResponse, folder, statsMap.get(folder.getId()));
            folderMap.put(folder.getId(), treeResponse);
        }

        // Build parent-child relationships
        final var rootFolders = new java.util.ArrayList<FolderTreeResponse>();
        for (final Folder folder : folders) {
            final var treeResponse = folderMap.get(folder.getId());

            if (folder.getParentFolder() == null) {
                // Root folder
                rootFolders.add(treeResponse);
            } else {
                // Child folder - add to parent's children list
                final var parentResponse = folderMap.get(folder.getParentFolder().getId());
                if (parentResponse != null) {
                    parentResponse.addChild(treeResponse);
                }
            }
        }

        // Sort root folders by name
        rootFolders.sort(Comparator.comparing(FolderTreeResponse::getName));

        log.debug("Built folder tree: totalFolders={}, rootFolders={}", folders.size(), rootFolders.size());

        return rootFolders;
    }

    /**
     * Enrich tree response with statistics from FolderStats
     */
    private void enrichTreeResponseWithStats(
            final FolderTreeResponse treeResponse,
            final Folder folder,
            final FolderStats stats) {

        if (stats == null) {
            // No stats available, keep defaults (0)
            return;
        }

        treeResponse.setTotalCards(stats.getTotalCardsCount());
        treeResponse.setDueCards(stats.getDueCardsCount());
        treeResponse.setNewCards(stats.getNewCardsCount());
        treeResponse.setMatureCards(stats.getMatureCardsCount());
    }


    /**
     * Get parent folder by ID (if not null)
     * Returns null if parentFolderId is null (root folder)
     */
    private Folder getParentFolder(final UUID parentFolderId, final UUID userId) {
        if (parentFolderId == null) {
            return null;
        }

        return this.folderRepository.findByIdAndUserId(parentFolderId, userId)
                .orElseThrow(() -> {
                    log.error("Parent folder not found: folderId={}, userId={}", parentFolderId, userId);
                    return new ResourceNotFoundException(
                            "FOLDER_002",
                            getMessage("error.folder.not.found", parentFolderId));
                });
    }

    // ==================== UC-008: Copy Folder - Helper Methods ====================

    /**
     * Get target parent folder for move operation
     * Returns null if moving to root level
     *
     * @param targetParentFolderId Target parent folder ID (nullable)
     * @param userId               User ID
     * @return Target parent folder or null for root
     */
    private Folder getTargetParentFolder(final UUID targetParentFolderId, final UUID userId) {
        if (targetParentFolderId == null) {
            log.debug("Moving to root level");
            return null;
        }

        return this.folderRepository.findByIdAndUserId(targetParentFolderId, userId)
                .orElseThrow(() -> {
                    log.error("Target parent folder not found: folderId={}, userId={}",
                            targetParentFolderId, userId);
                    return new ResourceNotFoundException(
                            "FOLDER_002",
                            getMessage("error.folder.parent.not.found"));
                });
    }

    /**
     * Get user by ID
     */
    private User getUser(final UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: userId={}", userId);
                    return new ResourceNotFoundException(
                            "USER_001",
                            getMessage("error.user.not.found", userId));
                });
    }

    /**
     * Hard-delete all descendant folders
     * Delete in reverse depth order to avoid FK constraint issues
     */
    private void hardDeleteFolderDescendants(final List<Folder> descendants) {
        if (descendants.isEmpty()) {
            log.debug("No descendants to hard-delete");
            return;
        }

        log.warn("Hard-deleting {} descendants", descendants.size());

        // Sort by depth descending (delete deepest first)
        descendants.sort(Comparator.comparing(Folder::getDepth).reversed());

        // Delete each descendant
        this.folderRepository.deleteAll(descendants);

        log.warn("Hard-deleted {} descendants", descendants.size());
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
     * @param userId   Current user ID
     * @throws ResourceNotFoundException if folder not found
     */
    @Transactional
    @Override
    public void invalidateFolderStats(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Invalidating folder stats: folderId={}, userId={}", folderId, userId);

        // Validate folder exists and user owns it
        getFolderByIdAndUserId(folderId, userId);

        // Delete cached stats
        this.folderStatsRepository.deleteByFolderIdAndUserId(folderId, userId);

        log.info("Folder stats invalidated: folderId={}, userId={}", folderId, userId);
    }

    /**
     * Check if folder name exists in parent
     */
    private boolean isNameExistsInParent(final String name, final Folder parentFolder, final UUID userId) {
        if (parentFolder == null) {
            // Root level
            return this.folderRepository.existsByUserIdAndRootAndName(userId, name);
        }

        // Child level
        return this.folderRepository.existsByUserIdAndParentFolderIdAndName(
                userId,
                parentFolder.getId(),
                name);
    }

    /**
     * Check if folder name exists in parent (excluding a specific folder)
     * Used for update validation to allow same name if not changed
     */
    private boolean isNameExistsInParentExcluding(
            final String name,
            final Folder parentFolder,
            final UUID userId,
            final UUID excludeFolderId) {

        if (parentFolder == null) {
            // Root level
            return this.folderRepository.existsByUserIdAndRootAndNameExcluding(
                    userId,
                    name,
                    excludeFolderId);
        }

        // Child level
        return this.folderRepository.existsByUserIdAndParentFolderIdAndNameExcluding(
                userId,
                parentFolder.getId(),
                name,
                excludeFolderId);
    }

    /**
     * Move folder to new parent (and optionally rename)
     *
     * Requirements:
     * - UC-007: Move Folder
     * - BR-017: Move validation (circular ref, depth, uniqueness)
     * - BR-018: Path recalculation for all descendants
     * - BR-019: Depth recalculation with delta
     *
     * Steps:
     * 1. Validate request and get folder
     * 2. Get target parent folder (if not root)
     * 3. Validate move constraints (circular ref, depth, name)
     * 4. Update folder parent and recalculate path/depth
     * 5. Update all descendant paths and depths
     * 6. Return updated folder
     *
     * @param folderId Folder ID to move
     * @param request  Move request with target parent and optional new name
     * @param userId   Current user ID
     * @return Moved folder response
     * @throws ResourceNotFoundException  if folder or target parent not found
     * @throws CircularReferenceException if trying to move into self or descendant
     * @throws MaxDepthExceededException  if move would exceed max depth
     * @throws FolderNameExistsException  if name exists in target parent
     */
    @Transactional
    @Override
    public FolderResponse moveFolder(final UUID folderId, final MoveFolderRequest request, final UUID userId) {
        // Guard clause: Validate request
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(request, "MoveFolderRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Moving folder: folderId={}, newParentFolderId={}, userId={}",
                folderId, request.getNewParentFolderId(), userId);

        // Get source folder with ownership check
        final var sourceFolder = getFolderByIdAndUserId(folderId, userId);

        // Get target parent folder (null if moving to root)
        final var targetParentFolder = getTargetParentFolder(request.getNewParentFolderId(), userId);

        // Validate move operation
        validateMoveOperation(sourceFolder, targetParentFolder, request);

        // Perform move
        performMove(sourceFolder, targetParentFolder, request);

        log.info("Folder moved successfully: folderId={}, newParentFolderId={}, userId={}",
                folderId, request.getNewParentFolderId(), userId);

        return this.folderMapper.toResponse(sourceFolder);
    }

    // ==================== UC-009: Delete Folder - Helper Methods ====================

    /**
     * Perform recursive copy of folder and all descendants (BR-021)
     *
     * Steps:
     * 1. Create new folder with new ID
     * 2. Recursively copy sub-folders (if includeSubfolders = true)
     * 3. Copy decks (when implemented)
     * 4. Copy cards (when implemented, with SRS reset)
     *
     * @return Copied folder with new ID
     */
    private Folder performCopy(
            final Folder sourceFolder,
            final Folder targetParentFolder,
            final String newName,
            final CopyFolderRequest request) {

        // Create copied folder
        final var copiedFolder = createCopiedFolder(sourceFolder, targetParentFolder, newName);

        // Save copied folder
        final var savedFolder = this.folderRepository.save(copiedFolder);

        log.debug("Created copied folder: sourceId={}, copiedId={}, name={}",
                sourceFolder.getId(), savedFolder.getId(), newName);

        // Recursively copy sub-folders if requested
        if (Boolean.TRUE.equals(request.getIncludeSubfolders())) {
            copySubFolders(sourceFolder, savedFolder, request);
        }

        // TODO: Copy decks when Deck entity is ready
        // if (Boolean.TRUE.equals(request.getIncludeCards())) {
        // copyDecksAndCards(sourceFolder, savedFolder, request);
        // }

        return savedFolder;
    }

    /**
     * Perform hard delete on folder and all descendants (BR-028)
     *
     * WARNING: This is irreversible!
     *
     * Steps:
     * 1. Hard-delete all descendants (bottom-up to avoid FK issues)
     * 2. Hard-delete the folder itself
     *
     * Note: For MVP, only delete folders
     * TODO: Delete decks, cards, stats when entities ready
     */
    private void performHardDelete(final Folder folder) {
        // Get all descendants (including deleted ones)
        final var descendants = this.folderRepository.findAllDescendants(
                folder.getUser().getId(),
                folder.getPath());

        // TODO: Hard-delete cards when Card entity is ready
        // hardDeleteCardsInDecks(folder, descendants);

        // TODO: Hard-delete decks when Deck entity is ready
        // hardDeleteDecksInFolders(folder, descendants);

        // TODO: Hard-delete folder_stats when implemented
        // hardDeleteStatsForFolders(folder, descendants);

        // Hard-delete descendants (reverse order to avoid issues)
        hardDeleteFolderDescendants(descendants);

        // Hard-delete the folder itself
        this.folderRepository.delete(folder);

        log.warn("Hard-deleted folder and {} descendants: folderId={}, path={}",
                descendants.size(), folder.getId(), folder.getPath());
    }

    /**
     * Perform move operation (BR-018, BR-019)
     *
     * Steps:
     * 1. Store old path for descendant update
     * 2. Update folder parent and recalculate path/depth
     * 3. Update name if provided
     * 4. Save folder
     * 5. Update all descendant paths and depths
     */
    private void performMove(
            final Folder sourceFolder,
            final Folder targetParentFolder,
            final MoveFolderRequest request) {

        // Store old values for descendant update
        final var oldPath = sourceFolder.getPath();
        final int oldDepth = sourceFolder.getDepth();

        // Update folder parent
        sourceFolder.setParentFolder(targetParentFolder);

        // Update name if provided
        if (StringUtils.isNotBlank(request.getNewName())) {
            final var trimmedNewName = StringUtils.trim(request.getNewName());
            sourceFolder.setName(trimmedNewName);
            log.debug("Folder renamed during move: oldName={}, newName={}",
                    sourceFolder.getName(), trimmedNewName);
        }

        // Recalculate folder path and depth
        sourceFolder.calculatePath();

        // Save folder (triggers path recalculation)
        this.folderRepository.save(sourceFolder);

        // Update all descendant paths and depths
        updateDescendantPathsAfterMove(sourceFolder, oldPath, oldDepth);
    }

    /**
     * Perform restore on folder and all descendants (BR-026)
     *
     * Steps:
     * 1. Restore folder and all descendants (set deleted_at = NULL)
     * 2. Restore decks and cards (when entities ready)
     * 3. Update folder_stats (when implemented)
     */
    private void performRestore(final Folder folder) {
        // Restore the folder itself
        folder.restore();
        this.folderRepository.save(folder);

        // Restore all descendants
        restoreDescendants(folder);

        // TODO: Restore decks when Deck entity is ready
        // restoreDecksInFolder(folder);

        // TODO: Restore cards when Card entity is ready
        // restoreCardsInDecks(folder);

        // TODO: Update folder_stats when implemented
        // updateStatsAfterRestore(folder);

        log.debug("Restored folder and descendants: folderId={}, path={}",
                folder.getId(), folder.getPath());
    }

    /**
     * Perform soft delete on folder and all descendants (BR-026, BR-027)
     *
     * Steps:
     * 1. Soft-delete folder and all descendants
     * 2. Update folder_stats (when implemented)
     */
    private void performSoftDelete(final Folder folder) {
        // Soft-delete the folder itself
        folder.softDelete();
        this.folderRepository.save(folder);

        // Soft-delete all descendants using materialized path
        softDeleteDescendants(folder);

        // TODO: Soft-delete decks when Deck entity is ready
        // softDeleteDecksInFolder(folder);

        // TODO: Soft-delete cards when Card entity is ready
        // softDeleteCardsInDecks(folder);

        // TODO: Update folder_stats when implemented
        // updateStatsAfterDelete(folder);

        log.debug("Soft-deleted folder and descendants: folderId={}, path={}",
                folder.getId(), folder.getPath());
    }

    /**
     * Permanently delete folder (hard delete from trash) (UC-009 A4)
     *
     * Requirements:
     * - UC-009: Delete Folder - A4: Delete from Trash
     * - BR-028: Permanent delete (hard delete)
     *
     * Steps:
     * 1. Validate folder exists and is in trash (soft-deleted)
     * 2. Hard-delete cards (when entities ready)
     * 3. Hard-delete decks (when entities ready)
     * 4. Hard-delete folder_stats
     * 5. Hard-delete folder and descendants
     *
     * WARNING: This action is irreversible!
     *
     * @param folderId Folder ID to permanently delete
     * @param userId   Current user ID
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder not in trash
     */
    @Transactional
    @Override
    public void permanentlyDeleteFolder(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.warn("Permanently deleting folder: folderId={}, userId={}", folderId, userId);

        // Get deleted folder (must be in trash)
        final var folder = getFolderForPermanentDelete(folderId, userId);

        // Perform hard delete
        performHardDelete(folder);

        log.warn("Folder permanently deleted: folderId={}, name={}, userId={}",
                folderId, folder.getName(), userId);
    }

    /**
     * Restore all descendant folders
     */
    private void restoreDescendants(final Folder folder) {
        // Get all deleted descendants
        // Note: We need to find descendants even if they are deleted
        // This requires a custom query or temporary bypass of deleted_at filter
        final var descendants = this.folderRepository.findAllDescendants(
                folder.getUser().getId(),
                folder.getPath());

        if (descendants.isEmpty()) {
            log.debug("No descendants to restore for folder: {}", folder.getId());
            return;
        }

        log.debug("Restoring {} descendants for folder: {}", descendants.size(), folder.getId());

        // Restore each descendant
        for (final Folder descendant : descendants) {
            if (descendant.getDeletedAt() != null) {
                descendant.restore();
            }
        }

        this.folderRepository.saveAll(descendants);

        log.debug("Restored {} descendants", descendants.size());
    }

    /**
     * Restore soft-deleted folder from trash (UC-009)
     *
     * Requirements:
     * - UC-009: Delete Folder - Step 5 (Undo)
     * - BR-026: Soft delete recovery
     * - BR-027: Cascade restore to descendants
     *
     * Steps:
     * 1. Validate folder exists and is deleted
     * 2. Restore folder and all descendants (set deleted_at = NULL)
     * 3. Restore decks and cards (when entities ready)
     * 4. Update folder_stats
     *
     * @param folderId Folder ID to restore
     * @param userId   Current user ID
     * @return Restored folder response
     * @throws ResourceNotFoundException if folder not found
     * @throws ValidationException       if folder is not deleted
     */
    @Transactional
    @Override
    public FolderResponse restoreFolder(final UUID folderId, final UUID userId) {
        // Guard clause: Validate parameters
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Restoring folder from trash: folderId={}, userId={}", folderId, userId);

        // Get deleted folder
        final var folder = getFolderForRestore(folderId, userId);

        // Perform restore
        performRestore(folder);

        log.info("Folder restored successfully: folderId={}, name={}, userId={}",
                folderId, folder.getName(), userId);

        return this.folderMapper.toResponse(folder);
    }

    /**
     * Soft-delete all descendant folders using materialized path
     */
    private void softDeleteDescendants(final Folder folder) {
        // Get all descendants
        final var descendants = this.folderRepository.findAllDescendants(
                folder.getUser().getId(),
                folder.getPath());

        if (descendants.isEmpty()) {
            log.debug("No descendants to delete for folder: {}", folder.getId());
            return;
        }

        log.debug("Soft-deleting {} descendants for folder: {}", descendants.size(), folder.getId());

        // Soft-delete each descendant
        for (final Folder descendant : descendants) {
            descendant.softDelete();
        }

        this.folderRepository.saveAll(descendants);

        log.debug("Soft-deleted {} descendants", descendants.size());
    }

    // ==================== Placeholder Methods (To be implemented) ====================

    /**
     * Update all descendant paths and depths after move (BR-018, BR-019)
     *
     * Uses bulk update for performance
     */
    private void updateDescendantPathsAfterMove(
            final Folder movedFolder,
            final String oldPath,
            final int oldDepth) {

        final var newPath = movedFolder.getPath();
        final int newDepth = movedFolder.getDepth();
        final var depthDelta = newDepth - oldDepth;

        // Update descendants using bulk operation
        final var updatedCount = this.folderRepository.updateDescendantPaths(
                movedFolder.getUser().getId(),
                oldPath,
                newPath,
                depthDelta);

        log.info("Updated descendant paths: movedFolderId={}, descendantsUpdated={}, depthDelta={}",
                movedFolder.getId(), updatedCount, depthDelta);
    }

    /**
     * Update folder (rename and update description)
     *
     * Requirements:
     * - UC-006: Rename Folder
     * - BR-014: Rename validation (unique name within parent)
     * - BR-015: Only name and description can be changed
     *
     * Steps:
     * 1. Validate request (not null, name not blank)
     * 2. Get folder by ID and user ID
     * 3. Validate folder ownership
     * 4. Validate name uniqueness (if name changed)
     * 5. Update folder name and description
     * 6. Save and return response
     *
     * @param folderId Folder ID to update
     * @param request  Update request with new name and description
     * @param userId   Current user ID
     * @return Updated folder response
     * @throws ResourceNotFoundException if folder not found
     * @throws FolderNameExistsException if new name already exists in parent
     */
    @Transactional
    @Override
    public FolderResponse updateFolder(final UUID folderId, final UpdateFolderRequest request, final UUID userId) {
        // Guard clause: Validate request
        Objects.requireNonNull(folderId, "Folder ID cannot be null");
        Objects.requireNonNull(request, "UpdateFolderRequest cannot be null");
        Objects.requireNonNull(userId, MSG_USER_ID_CANNOT_BE_NULL);

        log.info("Updating folder: folderId={}, newName={}, userId={}",
                folderId, request.getName(), userId);

        // Validate name
        validateFolderName(request.getName());

        // Get folder with ownership check
        final var folder = getFolderByIdAndUserId(folderId, userId);

        // Validate name uniqueness (only if name changed)
        final var trimmedNewName = StringUtils.trim(request.getName());
        final var nameChanged = !folder.getName().equals(trimmedNewName);

        if (nameChanged) {
            validateNameUniquenessForUpdate(trimmedNewName, folder.getParentFolder(), userId, folderId);
        }

        // Update folder
        updateFolderFields(folder, request);

        // Save folder
        final var savedFolder = this.folderRepository.save(folder);

        log.info("Folder updated successfully: folderId={}, newName={}, userId={}",
                savedFolder.getId(), savedFolder.getName(), userId);

        return this.folderMapper.toResponse(savedFolder);
    }

    /**
     * Update folder fields from request
     * Only updates name and description (BR-015)
     */
    private void updateFolderFields(final Folder folder, final UpdateFolderRequest request) {
        final var trimmedName = StringUtils.trim(request.getName());
        final var trimmedDescription = StringUtils.trim(request.getDescription());

        folder.setName(trimmedName);
        folder.setDescription(trimmedDescription);

        log.debug("Folder fields updated: folderId={}, name={}", folder.getId(), trimmedName);
    }

    /**
     * Validate copy depth constraint (BR-025)
     *
     * Same logic as move: Ensure resulting max depth ≤ 10
     */
    private void validateCopyDepth(final Folder sourceFolder, final Folder targetParentFolder) {
        // Calculate new depth for copied folder
        final var newDepth = targetParentFolder == null ? 0 : targetParentFolder.getDepth() + 1;

        // Get max descendant depth from source folder
        final var maxSourceDepth = this.folderRepository.findMaxDescendantDepth(
                sourceFolder.getUser().getId(),
                sourceFolder.getPath());

        // Calculate depth delta
        final var depthDelta = newDepth - sourceFolder.getDepth();

        // Calculate resulting max depth after copy
        final var resultingMaxDepth = maxSourceDepth + depthDelta;

        // Check if exceeds max depth
        if (resultingMaxDepth > MAX_FOLDER_DEPTH) {
            log.error(
                    "Copy would exceed max depth: sourceMaxDepth={}, depthDelta={}, resultingMaxDepth={}, maxAllowed={}",
                    maxSourceDepth, depthDelta, resultingMaxDepth, MAX_FOLDER_DEPTH);

            throw new MaxDepthExceededException(resultingMaxDepth);
        }

        log.debug("Copy depth validation passed: newDepth={}, resultingMaxDepth={}",
                newDepth, resultingMaxDepth);
    }

    /**
     * Validate copy operation constraints (BR-025)
     *
     * Checks:
     * 1. Max depth constraint
     * 2. Folder size threshold (sync vs async)
     */
    private void validateCopyOperation(
            final Folder sourceFolder,
            final Folder targetParentFolder) {

        // Validate max depth constraint
        validateCopyDepth(sourceFolder, targetParentFolder);

        // For MVP: Skip size validation (async not implemented yet)
        // TODO: Add size validation when async copy is implemented
        // validateCopySize(sourceFolder, request);
    }

    /**
     * Validate depth does not exceed maximum
     */
    private void validateDepth(final Folder parentFolder) {
        if (parentFolder == null) {
            return; // Root folder, depth = 0
        }

        final var newDepth = parentFolder.getDepth() + 1;

        if (newDepth > MAX_FOLDER_DEPTH) {
            log.warn("Max depth exceeded: parentDepth={}, newDepth={}, maxDepth={}",
                    parentFolder.getDepth(), newDepth, MAX_FOLDER_DEPTH);

            throw new MaxDepthExceededException(newDepth);
        }
    }

    /**
     * Validate folder name is not blank after trim
     */
    private void validateFolderName(final String name) {
        if (StringUtils.isBlank(name)) {
            log.error("Folder creation failed: name is blank");
            throw new ValidationException(
                    "FOLDER_001",
                    getMessage("error.folder.name.required"));
        }
    }

    /**
     * Validate move depth constraint (BR-017, BR-011)
     *
     * Calculates if move would cause any descendant to exceed max depth
     */
    private void validateMoveDepth(final Folder sourceFolder, final Folder targetParentFolder) {
        // Calculate new depth for source folder
        final var newDepth = targetParentFolder == null ? 0 : targetParentFolder.getDepth() + 1;

        // Calculate depth delta
        final var depthDelta = newDepth - sourceFolder.getDepth();

        // Get max descendant depth (including source folder itself)
        final var maxDescendantDepth = this.folderRepository.findMaxDescendantDepth(
                sourceFolder.getUser().getId(),
                sourceFolder.getPath());

        // Calculate resulting max depth after move
        final var resultingMaxDepth = maxDescendantDepth + depthDelta;

        // Check if exceeds max depth
        if (resultingMaxDepth > MAX_FOLDER_DEPTH) {
            log.error(
                    "Move would exceed max depth: currentMaxDepth={}, depthDelta={}, resultingMaxDepth={}, maxAllowed={}",
                    maxDescendantDepth, depthDelta, resultingMaxDepth, MAX_FOLDER_DEPTH);

            throw new MaxDepthExceededException(resultingMaxDepth);
        }

        log.debug("Move depth validation passed: newDepth={}, depthDelta={}, resultingMaxDepth={}",
                newDepth, depthDelta, resultingMaxDepth);
    }

    /**
     * Validate name uniqueness in target parent
     *
     * If request has new name, validate that name
     * Otherwise, validate current folder name
     */
    private void validateMoveNameUniqueness(
            final Folder sourceFolder,
            final Folder targetParentFolder,
            final MoveFolderRequest request) {

        // Determine name to use
        final var nameToUse = StringUtils.isNotBlank(request.getNewName())
                ? StringUtils.trim(request.getNewName())
                : sourceFolder.getName();

        // Check name uniqueness in target parent
        final var nameExists = isNameExistsInParentExcluding(
                nameToUse,
                targetParentFolder,
                sourceFolder.getUser().getId(),
                sourceFolder.getId());

        if (nameExists) {
            log.warn("Folder name already exists in target location: name={}, targetParentId={}",
                    nameToUse,
                    targetParentFolder != null ? targetParentFolder.getId() : null);

            throw new FolderNameExistsException(
                    "FOLDER_MOVE_003",
                    getMessage("error.folder.move.name.conflict", nameToUse));
        }
    }

    /**
     * Validate move operation constraints (BR-017)
     *
     * Checks:
     * 1. Not moving to same parent (no-op check)
     * 2. Not moving into self
     * 3. Not moving into descendant (circular reference)
     * 4. Max depth constraint
     * 5. Name uniqueness in target parent
     */
    private void validateMoveOperation(
            final Folder sourceFolder,
            final Folder targetParentFolder,
            final MoveFolderRequest request) {

        // Check if moving to same parent
        validateNotSameParent(sourceFolder, targetParentFolder);

        // Check circular reference (moving into self or descendant)
        validateNoCircularReference(sourceFolder, targetParentFolder);

        // Calculate new depth and validate max depth constraint
        validateMoveDepth(sourceFolder, targetParentFolder);

        // Validate name uniqueness in target parent
        validateMoveNameUniqueness(sourceFolder, targetParentFolder, request);
    }

    /**
     * Validate folder name is unique within parent
     */
    private void validateNameUniqueness(final String name, final Folder parentFolder, final UUID userId) {
        final var trimmedName = StringUtils.trim(name);

        final var nameExists = isNameExistsInParent(trimmedName, parentFolder, userId);

        if (nameExists) {
            log.warn("Folder name already exists: name={}, parentFolderId={}, userId={}",
                    trimmedName,
                    parentFolder != null ? parentFolder.getId() : null,
                    userId);

            throw new FolderNameExistsException(
                    "FOLDER_004",
                    getMessage("error.folder.name.exists", trimmedName));
        }
    }

    /**
     * Validate folder name is unique within parent (for update operation)
     * Excludes the current folder being updated
     *
     * @param name            New folder name
     * @param parentFolder    Parent folder (null if root)
     * @param userId          User ID
     * @param excludeFolderId Folder ID to exclude from uniqueness check
     */
    private void validateNameUniquenessForUpdate(
            final String name,
            final Folder parentFolder,
            final UUID userId,
            final UUID excludeFolderId) {

        final var trimmedName = StringUtils.trim(name);

        final var nameExists = isNameExistsInParentExcluding(
                trimmedName,
                parentFolder,
                userId,
                excludeFolderId);

        if (nameExists) {
            log.warn("Folder name already exists: name={}, parentFolderId={}, userId={}, excludeFolderId={}",
                    trimmedName,
                    parentFolder != null ? parentFolder.getId() : null,
                    userId,
                    excludeFolderId);

            throw new FolderNameExistsException(
                    "FOLDER_004",
                    getMessage("error.folder.name.exists", trimmedName));
        }
    }

    // ==================== UC-010: Folder Statistics - Helper Methods ====================

    /**
     * Validate no circular reference (BR-017)
     *
     * Cannot move folder into:
     * 1. Itself
     * 2. Any of its descendants
     */
    private void validateNoCircularReference(final Folder sourceFolder, final Folder targetParentFolder) {
        if (targetParentFolder == null) {
            return; // Moving to root, no circular reference possible
        }

        // Check if moving into self
        if (sourceFolder.getId().equals(targetParentFolder.getId())) {
            log.error("Cannot move folder into itself: folderId={}", sourceFolder.getId());

            throw new CircularReferenceException(
                    getMessage("error.folder.move.into.self"));
        }

        // Check if target is descendant of source (using materialized path)
        final var isDescendant = this.folderRepository.isDescendantOf(
                targetParentFolder.getId(),
                sourceFolder.getPath());

        if (isDescendant) {
            log.error("Cannot move folder into descendant: sourceId={}, targetId={}",
                    sourceFolder.getId(), targetParentFolder.getId());

            throw new CircularReferenceException(
                    getMessage("error.folder.move.into.descendant",
                            sourceFolder.getName(),
                            targetParentFolder.getName()));
        }
    }

    /**
     * Check if folder is already in target parent (no-op)
     */
    private void validateNotSameParent(final Folder sourceFolder, final Folder targetParentFolder) {
        final var currentParentId = sourceFolder.getParentFolder() != null
                ? sourceFolder.getParentFolder().getId()
                : null;

        final var targetParentId = targetParentFolder != null
                ? targetParentFolder.getId()
                : null;

        if (Objects.equals(currentParentId, targetParentId)) {
            log.warn("Folder is already in target location: folderId={}, parentId={}",
                    sourceFolder.getId(), targetParentId);

            throw new ValidationException(
                    "FOLDER_MOVE_001",
                    getMessage("error.folder.move.same.parent"));
        }
    }
}
