package com.repeatwise.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.FolderStats;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseError;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.FolderMapper;
import com.repeatwise.repository.DeckRepository;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.FolderStatsRepository;
import com.repeatwise.repository.CardBoxPositionRepository;
import com.repeatwise.repository.projection.FolderCardStatsProjection;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.FolderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of FolderService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderServiceImpl implements FolderService {

    private static final int MAX_FOLDER_DEPTH = 10;
    private static final int MAX_COPY_ITEMS = 500;
    private static final String PATH_DELIMITER = "/";

    private final FolderRepository folderRepository;
    private final DeckRepository deckRepository;
    private final FolderStatsRepository folderStatsRepository;
    private final CardBoxPositionRepository cardBoxPositionRepository;
    private final UserRepository userRepository;
    private final FolderMapper folderMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        log.debug("Creating folder with name '{}' for user {}", request.getName(), userId);

        // Get user
        final var user = getUserOrThrow(userId);

        // Validate parent folder if provided
        Folder parentFolder = null;
        var newDepth = 0;

        if (request.getParentFolderId() != null) {
            parentFolder = getFolderEntityByIdInternal(request.getParentFolderId(), userId);

            // Calculate new depth
            newDepth = parentFolder.getDepth() + 1;

            // Validate max depth constraint (BR-FOLD-01)
            if (newDepth > MAX_FOLDER_DEPTH) {
                throw new RepeatWiseException(
                        RepeatWiseError.MAX_FOLDER_DEPTH_EXCEEDED,
                        MAX_FOLDER_DEPTH);
            }
        }

        // Validate name uniqueness within same parent (BR-FOLD-02)
        final var trimmedName = request.getName().trim();
        if (folderNameExists(userId, request.getParentFolderId(), trimmedName)) {
            throw new RepeatWiseException(
                    RepeatWiseError.FOLDER_NAME_ALREADY_EXISTS,
                    trimmedName);
        }

        // Create folder entity
        final var folder = this.folderMapper.toEntity(request);
        folder.setUser(user);
        folder.setParentFolder(parentFolder);
        folder.setName(trimmedName);
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription().trim());
        }

        folder.setSortOrder(getNextSortOrder(userId, request.getParentFolderId()));

        // Build materialized path and set depth
        folder.buildPath();

        // Save folder
        final var savedFolder = this.folderRepository.save(folder);

        log.info("Created folder {} with ID {} for user {}", savedFolder.getName(), savedFolder.getId(), userId);

        return this.folderMapper.toResponse(savedFolder);
    }

    @Override
    @Transactional
    public FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId) {
        log.debug("Updating folder {} for user {}", folderId, userId);

        // Get folder
        final var folder = getFolderEntityByIdInternal(folderId, userId);

        // Check if name is being changed
        if (request.getName() != null) {
            final var trimmedName = request.getName().trim();

            // Validate name uniqueness if name changed
            if (!trimmedName.equalsIgnoreCase(folder.getName())) {
                final var parentId = folder.getParentFolder() != null ? folder.getParentFolder().getId() : null;
                if (folderNameExistsExcluding(userId, parentId, trimmedName, folderId)) {
                    throw new RepeatWiseException(
                            RepeatWiseError.FOLDER_NAME_ALREADY_EXISTS,
                            trimmedName);
                }
            }

            folder.setName(trimmedName);
        }

        // Update description
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription().trim());
        }

        folder.setUpdatedAt(LocalDateTime.now());

        final var updatedFolder = this.folderRepository.save(folder);

        log.info("Updated folder {} for user {}", folderId, userId);

        return this.folderMapper.toResponse(updatedFolder);
    }

    @Override
    @Transactional
    public FolderResponse moveFolder(UUID folderId, MoveFolderRequest request, UUID userId) {
        log.debug("Moving folder {} to parent {} for user {}", folderId, request.getTargetParentFolderId(), userId);

        // Get source folder
        final var sourceFolder = getFolderEntityByIdInternal(folderId, userId);
        final var oldParentId = sourceFolder.getParentFolder() != null ? sourceFolder.getParentFolder().getId() : null;

        // Check if moving to same parent (no-op)
        final var targetParentId = request.getTargetParentFolderId();
        if (((targetParentId == null) && (oldParentId == null)) ||
                ((targetParentId != null) && targetParentId.equals(oldParentId))) {
            log.debug("Folder {} is already in target location", folderId);
            return this.folderMapper.toResponse(sourceFolder);
        }

        // Get destination parent folder
        Folder targetParent = null;
        var newDepth = 0;

        if (targetParentId != null) {
            targetParent = getFolderEntityByIdInternal(targetParentId, userId);
            newDepth = targetParent.getDepth() + 1;

            // Validate: Cannot move into itself (BR-FOLD-04)
            if (targetParentId.equals(folderId)) {
                throw new RepeatWiseException(
                        RepeatWiseError.CIRCULAR_FOLDER_REFERENCE,
                        folderId);
            }

            // Validate: Cannot move into descendant (BR-FOLD-04)
            if (targetParent.getPath().startsWith(sourceFolder.getPath() + PATH_DELIMITER)) {
                throw new RepeatWiseException(
                        RepeatWiseError.CIRCULAR_FOLDER_REFERENCE,
                        sourceFolder.getName(),
                        targetParent.getName());
            }
        }

        // Calculate depth delta
        final var depthDelta = newDepth - sourceFolder.getDepth();

        // Validate max depth for all descendants (BR-FOLD-01)
        final var sourcePath = sourceFolder.getPath() + PATH_DELIMITER;
        final var maxDescendantDepth = this.folderRepository.getMaxDepthInSubtree(userId, sourcePath);
        if (maxDescendantDepth != null) {
            final var newMaxDepth = maxDescendantDepth + depthDelta;
            if (newMaxDepth > MAX_FOLDER_DEPTH) {
                throw new RepeatWiseException(
                        RepeatWiseError.MAX_FOLDER_DEPTH_EXCEEDED,
                        newMaxDepth,
                        MAX_FOLDER_DEPTH);
            }
        }

        // Validate name uniqueness at destination
        if (folderNameExistsExcluding(userId, targetParentId, sourceFolder.getName(), folderId)) {
            throw new RepeatWiseException(
                    RepeatWiseError.FOLDER_NAME_ALREADY_EXISTS,
                    sourceFolder.getName());
        }

        // Perform move operation
        final var oldPath = sourceFolder.getPath();
        final var parentChanged = !Objects.equals(oldParentId, targetParentId);

        if (parentChanged) {
            sourceFolder.setSortOrder(getNextSortOrder(userId, targetParentId));
        }

        sourceFolder.setParentFolder(targetParent);
        sourceFolder.buildPath(); // Rebuild path based on new parent
        final var newPath = sourceFolder.getPath();

        final var now = LocalDateTime.now();
        sourceFolder.setUpdatedAt(now);
        this.folderRepository.save(sourceFolder);

        // Update all descendants' paths and depths
        final var descendants = this.folderRepository.findDescendantsByPath(userId, oldPath + PATH_DELIMITER);
        for (final Folder descendant : descendants) {
            // Update path by replacing old prefix with new prefix
            final var updatedPath = descendant.getPath().replaceFirst("^" + oldPath, newPath);
            descendant.setPath(updatedPath);

            // Update depth
            descendant.setDepth(descendant.getDepth() + depthDelta);

            descendant.setUpdatedAt(now);

            this.folderRepository.save(descendant);
        }

        log.info("Moved folder {} to new parent {} for user {}", folderId, targetParentId, userId);

        return this.folderMapper.toResponse(sourceFolder);
    }

    @Override
    @Transactional
    public FolderResponse copyFolder(UUID folderId, UUID destinationFolderId, String newName, UUID userId) {
        log.debug("Copying folder {} to destination {} for user {}", folderId, destinationFolderId, userId);

        // Get source folder
        final var sourceFolder = getFolderEntityByIdInternal(folderId, userId);

        // Count total items in subtree
        final var sourcePath = sourceFolder.getPath() + PATH_DELIMITER;
        final var totalItems = this.folderRepository.countItemsInSubtree(userId, sourcePath) + 1; // +1 for source
                                                                                                   // folder itself

        // Validate size limit (BR-COPY-01, BR-COPY-02, BR-COPY-03)
        if (totalItems > MAX_COPY_ITEMS) {
            throw new RepeatWiseException(
                    RepeatWiseError.FOLDER_TOO_LARGE,
                    totalItems,
                    MAX_COPY_ITEMS);
        }

        // Get destination parent
        Folder destinationParent = null;
        var destinationDepth = 0;

        if (destinationFolderId != null) {
            destinationParent = getFolderEntityByIdInternal(destinationFolderId, userId);
            destinationDepth = destinationParent.getDepth();
        }

        // Validate max depth constraint
        final var maxSourceDepth = this.folderRepository.getMaxDepthInSubtree(userId, sourcePath);
        final var sourceSubtreeHeight = (maxSourceDepth != null ? maxSourceDepth : sourceFolder.getDepth())
                - sourceFolder.getDepth();
        final var newMaxDepth = destinationDepth + 1 + sourceSubtreeHeight;

        if (newMaxDepth > MAX_FOLDER_DEPTH) {
            throw new RepeatWiseException(
                    RepeatWiseError.MAX_FOLDER_DEPTH_EXCEEDED,
                    newMaxDepth,
                    MAX_FOLDER_DEPTH);
        }

        // Generate unique name at destination
        final var copyName = newName != null ? newName
                : generateUniqueCopyName(sourceFolder.getName(), destinationFolderId, userId);

        // Perform recursive copy
        final var copiedFolder = copyFolderRecursive(sourceFolder, destinationParent, copyName, userId);

        log.info("Copied folder {} to destination {} for user {}", folderId, destinationFolderId, userId);

        return this.folderMapper.toResponse(copiedFolder);
    }

    private String generateUniqueCopyName(String baseName, UUID parentId, UUID userId) {
        var copyName = baseName + " (copy)";
        var counter = 2;

        while (folderNameExists(userId, parentId, copyName)) {
            copyName = baseName + " (copy " + counter + ")";
            counter++;
        }

        return copyName;
    }

    private Folder copyFolderRecursive(Folder source, Folder newParent, String newName, UUID userId) {
        final var user = getUserOrThrow(userId);

        // Create new folder
        var copiedFolder = Folder.builder()
                .user(user)
                .parentFolder(newParent)
                .name(newName)
                .description(source.getDescription())
                .sortOrder(getNextSortOrder(userId, newParent != null ? newParent.getId() : null))
                .build();

        copiedFolder.buildPath();

        copiedFolder = this.folderRepository.save(copiedFolder);

        // Copy child folders recursively
        final var children = this.folderRepository.findChildrenByUserIdAndParentId(userId, source.getId());
        for (final Folder child : children) {
            copyFolderRecursive(child, copiedFolder, child.getName(), userId);
        }

        // Copy decks in this folder (if DeckRepository is available)
        // This would be implemented when deck copying is ready

        return copiedFolder;
    }

    @Override
    @Transactional
    public DeletionSummary deleteFolder(UUID folderId, UUID userId) {
        log.debug("Soft deleting folder {} for user {}", folderId, userId);

        // Get folder
        final var folder = getFolderEntityByIdInternal(folderId, userId);

        final var now = LocalDateTime.now();

        // Soft delete folder
        folder.setDeletedAt(now);
        this.folderRepository.save(folder);

        // Soft delete all descendants
        final var path = folder.getPath() + PATH_DELIMITER;
        final var descendants = this.folderRepository.findDescendantsByPath(userId, path);
        var foldersDeleted = 1; // Include the folder itself

        for (final Folder descendant : descendants) {
            descendant.setDeletedAt(now);
            this.folderRepository.save(descendant);
            foldersDeleted++;
        }

        // Soft delete all decks in folder tree
        // Get all folder IDs in the subtree
        final List<UUID> folderIds = new ArrayList<>();
        folderIds.add(folderId);
        descendants.forEach(d -> folderIds.add(d.getId()));

        // Soft delete decks (if DeckRepository supports it)
        final var decksDeleted = softDeleteDecksInFolders(folderIds, now);

        final var message = this.messageSource.getMessage(
                "success.folder.deleted",
                new Object[] { folder.getName() },
                LocaleContextHolder.getLocale());

        log.info("Soft deleted folder {} and {} descendants for user {}", folderId, foldersDeleted - 1, userId);

        return new DeletionSummary(foldersDeleted, decksDeleted, message);
    }

    private int softDeleteDecksInFolders(List<UUID> folderIds, LocalDateTime deletedAt) {
        if (folderIds.isEmpty()) {
            return 0;
        }
        return this.deckRepository.softDeleteByFolderIds(folderIds, deletedAt);
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse getFolderById(UUID folderId, UUID userId) {
        final var folder = getFolderEntityByIdInternal(folderId, userId);
        return this.folderMapper.toResponse(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getAllFolders(UUID userId) {
        final var folders = this.folderRepository.findAllByUserId(userId);
        return folders.stream()
                .map(this.folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getRootFolders(UUID userId) {
        final var folders = this.folderRepository.findRootFoldersByUserId(userId);
        return folders.stream()
                .map(this.folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getChildFolders(UUID parentId, UUID userId) {
        // Verify parent exists and belongs to user
        getFolderEntityByIdInternal(parentId, userId);

        final var folders = this.folderRepository.findChildrenByUserIdAndParentId(userId, parentId);
        return folders.stream()
                .map(this.folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public FolderResponse restoreFolder(UUID folderId, UUID userId) {
        log.debug("Restoring folder {} for user {}", folderId, userId);

        // Find soft-deleted folder
        final var folder = this.folderRepository.findDeletedByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new RepeatWiseException(
                        RepeatWiseError.FOLDER_NOT_FOUND,
                        folderId));

        // Restore folder
        folder.setDeletedAt(null);
        this.folderRepository.save(folder);

        // Restore all descendants
        final var path = folder.getPath() + PATH_DELIMITER;
        final var descendants = this.folderRepository.findDescendantsByPath(userId, path);

        final List<UUID> folderIds = new ArrayList<>();
        folderIds.add(folderId);

        for (final Folder descendant : descendants) {
            descendant.setDeletedAt(null);
            this.folderRepository.save(descendant);
            folderIds.add(descendant.getId());
        }

        // Restore all decks in folder tree
        if (!folderIds.isEmpty()) {
            this.deckRepository.restoreByFolderIds(folderIds);
        }

        log.info("Restored folder {} and {} descendants for user {}", folderId, descendants.size(), userId);

        return this.folderMapper.toResponse(folder);
    }

    @Override
    @Transactional
    public FolderStatsResponse getFolderStats(UUID folderId, UUID userId, boolean forceRefresh) {
        final var folder = getFolderEntityByIdInternal(folderId, userId);
        final var folderIds = collectFolderIds(folder, userId);
        final var deckIds = getActiveDeckIds(userId, folderIds);

        final int totalDescendants = Math.max(folderIds.size() - 1, 0);
        final int totalDecks = deckIds.size();

        final var folderStats = this.folderStatsRepository.findByFolderIdAndUserId(folderId, userId)
                .orElseGet(() -> FolderStats.builder()
                        .folder(folder)
                        .user(getUserOrThrow(userId))
                        .build());

        final boolean needsRecompute = forceRefresh || folderStats.isStale();
        CardStatsAggregate aggregate = null;

        if (needsRecompute) {
            aggregate = computeCardStats(userId, deckIds);
            folderStats.setTotalCardsCount(intValue(aggregate.totalCards()));
            folderStats.setDueCardsCount(intValue(aggregate.dueCards()));
            folderStats.setNewCardsCount(intValue(aggregate.newCards()));
            folderStats.setLearningCardsCount(intValue(aggregate.learningCards()));
            folderStats.setReviewCardsCount(intValue(aggregate.reviewCards()));
            folderStats.setMatureCardsCount(intValue(aggregate.masteredCards()));
            folderStats.setTotalFoldersCount(totalDescendants);
            folderStats.setTotalDecksCount(totalDecks);
            folderStats.refreshTimestamp();
            this.folderStatsRepository.save(folderStats);
        } else {
            boolean structureUpdated = false;
            if (!folderStats.getTotalFoldersCount().equals(totalDescendants)) {
                folderStats.setTotalFoldersCount(totalDescendants);
                structureUpdated = true;
            }
            if (!folderStats.getTotalDecksCount().equals(totalDecks)) {
                folderStats.setTotalDecksCount(totalDecks);
                structureUpdated = true;
            }
            if (structureUpdated) {
                this.folderStatsRepository.save(folderStats);
            }
        }

        // Ensure aggregate is available for completion rate when stats recomputed
        if (aggregate == null) {
            aggregate = new CardStatsAggregate(
                    folderStats.getTotalCardsCount(),
                    folderStats.getDueCardsCount(),
                    folderStats.getNewCardsCount(),
                    folderStats.getLearningCardsCount(),
                    folderStats.getReviewCardsCount(),
                    folderStats.getMatureCardsCount());
        }

        final double completionRate = calculateCompletionRate(aggregate.masteredCards(), aggregate.totalCards());

        return FolderStatsResponse.builder()
                .folderId(folderId)
                .folderName(folder.getName())
                .totalFolders(folderStats.getTotalFoldersCount())
                .totalDecks(folderStats.getTotalDecksCount())
                .totalCards(folderStats.getTotalCardsCount())
                .dueCards(folderStats.getDueCardsCount())
                .newCards(folderStats.getNewCardsCount())
                .learningCards(folderStats.getLearningCardsCount())
                .reviewCards(folderStats.getReviewCardsCount())
                .masteredCards(folderStats.getMatureCardsCount())
                .completionRate(completionRate)
                .cached(!needsRecompute)
                .lastUpdatedAt(folderStats.getLastComputedAt())
                .build();
    }

    private List<UUID> collectFolderIds(Folder folder, UUID userId) {
        final List<UUID> ids = new ArrayList<>();
        ids.add(folder.getId());
        final var descendants = this.folderRepository.findDescendantsByPath(userId, folder.getPath() + PATH_DELIMITER);
        descendants.forEach(descendant -> ids.add(descendant.getId()));
        return ids;
    }

    private List<UUID> getActiveDeckIds(UUID userId, List<UUID> folderIds) {
        if (folderIds.isEmpty()) {
            return List.of();
        }
        return this.deckRepository.findActiveDeckIdsByUserIdAndFolderIds(userId, folderIds);
    }

    private CardStatsAggregate computeCardStats(UUID userId, List<UUID> deckIds) {
        if (deckIds.isEmpty()) {
            return CardStatsAggregate.empty();
        }

        final FolderCardStatsProjection projection = this.cardBoxPositionRepository.aggregateStats(userId, deckIds,
                LocalDate.now());

        if (projection == null) {
            return CardStatsAggregate.empty();
        }

        return new CardStatsAggregate(
                projection.getTotalCards(),
                projection.getDueCards(),
                projection.getNewCards(),
                projection.getLearningCards(),
                projection.getReviewCards(),
                projection.getMasteredCards());
    }

    private double calculateCompletionRate(long masteredCards, long totalCards) {
        if (totalCards <= 0) {
            return 0d;
        }
        final double rate = (masteredCards * 100.0d) / totalCards;
        return Math.round(rate * 10d) / 10d;
    }

    private int intValue(long value) {
        return Math.toIntExact(value);
    }

    private record CardStatsAggregate(long totalCards,
            long dueCards,
            long newCards,
            long learningCards,
            long reviewCards,
            long masteredCards) {

        private static CardStatsAggregate empty() {
            return new CardStatsAggregate(0L, 0L, 0L, 0L, 0L, 0L);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderEntityById(UUID folderId, UUID userId) {
        return getFolderEntityByIdInternal(folderId, userId);
    }

    private Folder getFolderEntityByIdInternal(UUID folderId, UUID userId) {
        return this.folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new RepeatWiseException(
                        RepeatWiseError.FOLDER_NOT_FOUND,
                        folderId));
    }

    private User getUserOrThrow(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(
                        RepeatWiseError.USER_NOT_FOUND,
                        userId));
    }

    private int getNextSortOrder(UUID userId, UUID parentId) {
        final Integer maxSortOrder = parentId == null
                ? this.folderRepository.getMaxSortOrderForRoot(userId)
                : this.folderRepository.getMaxSortOrderForParent(userId, parentId);

        return (maxSortOrder != null ? maxSortOrder : 0) + 1;
    }

    private boolean folderNameExists(UUID userId, UUID parentId, String name) {
        return parentId == null
                ? this.folderRepository.existsByUserIdAndParentFolderIsNullAndNameIgnoreCaseAndDeletedAtIsNull(userId,
                        name)
                : this.folderRepository.existsByUserIdAndParentFolderIdAndNameIgnoreCaseAndDeletedAtIsNull(userId,
                        parentId,
                        name);
    }

    private boolean folderNameExistsExcluding(UUID userId, UUID parentId, String name, UUID excludeId) {
        return parentId == null
                ? this.folderRepository
                        .existsByUserIdAndParentFolderIsNullAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(userId,
                                name,
                                excludeId)
                : this.folderRepository.existsByUserIdAndParentFolderIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
                        userId,
                        parentId,
                        name,
                        excludeId);
    }
}
