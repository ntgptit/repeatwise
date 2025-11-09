package com.repeatwise.service.impl;

import com.repeatwise.constant.ApiErrorCode;
import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
// Deck import removed - DeckRepository not yet implemented
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.User;
import com.repeatwise.exception.RepeatWiseException;
import com.repeatwise.mapper.FolderMapper;
import com.repeatwise.repository.FolderRepository;
import com.repeatwise.repository.UserRepository;
import com.repeatwise.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of FolderService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FolderServiceImpl implements FolderService {

    private static final int MAX_FOLDER_DEPTH = 10;
    private static final int MAX_COPY_ITEMS = 500;

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final FolderMapper folderMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        log.debug("Creating folder with name '{}' for user {}", request.getName(), userId);

        // Get user
        User user = getUserOrThrow(userId);

        // Validate parent folder if provided
        Folder parentFolder = null;
        int newDepth = 0;

        if (request.getParentFolderId() != null) {
            parentFolder = getFolderEntityById(request.getParentFolderId(), userId);

            // Calculate new depth
            newDepth = parentFolder.getDepth() + 1;

            // Validate max depth constraint (BR-FOLD-01)
            if (newDepth > MAX_FOLDER_DEPTH) {
                throw new RepeatWiseException(
                        ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED,
                        HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(
                                "error.folder.max.depth",
                                new Object[]{MAX_FOLDER_DEPTH},
                                LocaleContextHolder.getLocale()
                        )
                );
            }
        }

        // Validate name uniqueness within same parent (BR-FOLD-02)
        String trimmedName = request.getName().trim();
        if (folderRepository.existsByNameAndParent(userId, request.getParentFolderId(), trimmedName)) {
            throw new RepeatWiseException(
                    ApiErrorCode.FOLDER_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(
                            "error.folder.duplicate.name",
                            new Object[]{trimmedName},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Create folder entity
        Folder folder = folderMapper.toEntity(request);
        folder.setUser(user);
        folder.setParentFolder(parentFolder);
        folder.setName(trimmedName);
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription().trim());
        }

        // Build materialized path and set depth
        folder.buildPath();

        // Save folder
        Folder savedFolder = folderRepository.save(folder);

        log.info("Created folder {} with ID {} for user {}", savedFolder.getName(), savedFolder.getId(), userId);

        return folderMapper.toResponse(savedFolder);
    }

    @Override
    @Transactional
    public FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId) {
        log.debug("Updating folder {} for user {}", folderId, userId);

        // Get folder
        Folder folder = getFolderEntityById(folderId, userId);

        // Check if name is being changed
        if (request.getName() != null) {
            String trimmedName = request.getName().trim();

            // Validate name uniqueness if name changed
            if (!trimmedName.equalsIgnoreCase(folder.getName())) {
                UUID parentId = folder.getParentFolder() != null ? folder.getParentFolder().getId() : null;

                if (folderRepository.existsByNameAndParentExcludingId(userId, parentId, trimmedName, folderId)) {
                    throw new RepeatWiseException(
                            ApiErrorCode.FOLDER_NAME_ALREADY_EXISTS,
                            HttpStatus.BAD_REQUEST,
                            messageSource.getMessage(
                                    "error.folder.name.exists",
                                    new Object[]{trimmedName},
                                    LocaleContextHolder.getLocale()
                            )
                    );
                }
            }

            folder.setName(trimmedName);
        }

        // Update description
        if (request.getDescription() != null) {
            folder.setDescription(request.getDescription().trim());
        }

        folder.setUpdatedAt(LocalDateTime.now());

        Folder updatedFolder = folderRepository.save(folder);

        log.info("Updated folder {} for user {}", folderId, userId);

        return folderMapper.toResponse(updatedFolder);
    }

    @Override
    @Transactional
    public FolderResponse moveFolder(UUID folderId, MoveFolderRequest request, UUID userId) {
        log.debug("Moving folder {} to parent {} for user {}", folderId, request.getTargetParentFolderId(), userId);

        // Get source folder
        Folder sourceFolder = getFolderEntityById(folderId, userId);
        UUID oldParentId = sourceFolder.getParentFolder() != null ? sourceFolder.getParentFolder().getId() : null;

        // Check if moving to same parent (no-op)
        UUID targetParentId = request.getTargetParentFolderId();
        if ((targetParentId == null && oldParentId == null) ||
            (targetParentId != null && targetParentId.equals(oldParentId))) {
            log.debug("Folder {} is already in target location", folderId);
            return folderMapper.toResponse(sourceFolder);
        }

        // Get destination parent folder
        Folder targetParent = null;
        int newDepth = 0;

        if (targetParentId != null) {
            targetParent = getFolderEntityById(targetParentId, userId);
            newDepth = targetParent.getDepth() + 1;

            // Validate: Cannot move into itself (BR-FOLD-04)
            if (targetParentId.equals(folderId)) {
                throw new RepeatWiseException(
                        ApiErrorCode.CIRCULAR_FOLDER_REFERENCE,
                        HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(
                                "error.folder.move.into.self",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                );
            }

            // Validate: Cannot move into descendant (BR-FOLD-04)
            if (targetParent.getPath().startsWith(sourceFolder.getPath() + "/")) {
                throw new RepeatWiseException(
                        ApiErrorCode.CIRCULAR_FOLDER_REFERENCE,
                        HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(
                                "error.folder.move.into.descendant",
                                new Object[]{sourceFolder.getName(), targetParent.getName()},
                                LocaleContextHolder.getLocale()
                        )
                );
            }
        }

        // Calculate depth delta
        int depthDelta = newDepth - sourceFolder.getDepth();

        // Validate max depth for all descendants (BR-FOLD-01)
        String sourcePath = sourceFolder.getPath() + "/";
        Integer maxDescendantDepth = folderRepository.getMaxDepthInSubtree(userId, sourcePath);
        if (maxDescendantDepth != null) {
            int newMaxDepth = maxDescendantDepth + depthDelta;
            if (newMaxDepth > MAX_FOLDER_DEPTH) {
                throw new RepeatWiseException(
                        ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED,
                        HttpStatus.BAD_REQUEST,
                        messageSource.getMessage(
                                "error.folder.move.max.depth.exceeded",
                                new Object[]{newMaxDepth, MAX_FOLDER_DEPTH},
                                LocaleContextHolder.getLocale()
                        )
                );
            }
        }

        // Validate name uniqueness at destination
        if (folderRepository.existsByNameAndParentExcludingId(userId, targetParentId, sourceFolder.getName(), folderId)) {
            throw new RepeatWiseException(
                    ApiErrorCode.FOLDER_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(
                            "error.folder.move.name.conflict",
                            new Object[]{sourceFolder.getName()},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Perform move operation
        String oldPath = sourceFolder.getPath();
        sourceFolder.setParentFolder(targetParent);
        sourceFolder.buildPath(); // Rebuild path based on new parent
        String newPath = sourceFolder.getPath();

        sourceFolder.setUpdatedAt(LocalDateTime.now());
        folderRepository.save(sourceFolder);

        // Update all descendants' paths and depths
        List<Folder> descendants = folderRepository.findDescendantsByPath(userId, oldPath + "/");
        for (Folder descendant : descendants) {
            // Update path by replacing old prefix with new prefix
            String updatedPath = descendant.getPath().replaceFirst("^" + oldPath, newPath);
            descendant.setPath(updatedPath);

            // Update depth
            descendant.setDepth(descendant.getDepth() + depthDelta);

            folderRepository.save(descendant);
        }

        log.info("Moved folder {} to new parent {} for user {}", folderId, targetParentId, userId);

        return folderMapper.toResponse(sourceFolder);
    }

    @Override
    @Transactional
    public FolderResponse copyFolder(UUID folderId, UUID destinationFolderId, String newName, UUID userId) {
        log.debug("Copying folder {} to destination {} for user {}", folderId, destinationFolderId, userId);

        // Get source folder
        Folder sourceFolder = getFolderEntityById(folderId, userId);

        // Count total items in subtree
        String sourcePath = sourceFolder.getPath() + "/";
        long totalItems = folderRepository.countItemsInSubtree(userId, sourcePath) + 1; // +1 for source folder itself

        // Validate size limit (BR-COPY-01, BR-COPY-02, BR-COPY-03)
        if (totalItems > MAX_COPY_ITEMS) {
            throw new RepeatWiseException(
                    ApiErrorCode.FOLDER_TOO_LARGE,
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(
                            "error.folder.too.large",
                            new Object[]{totalItems, MAX_COPY_ITEMS},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Get destination parent
        Folder destinationParent = null;
        int destinationDepth = 0;

        if (destinationFolderId != null) {
            destinationParent = getFolderEntityById(destinationFolderId, userId);
            destinationDepth = destinationParent.getDepth();
        }

        // Validate max depth constraint
        Integer maxSourceDepth = folderRepository.getMaxDepthInSubtree(userId, sourcePath);
        int sourceSubtreeHeight = (maxSourceDepth != null ? maxSourceDepth : sourceFolder.getDepth()) - sourceFolder.getDepth();
        int newMaxDepth = destinationDepth + 1 + sourceSubtreeHeight;

        if (newMaxDepth > MAX_FOLDER_DEPTH) {
            throw new RepeatWiseException(
                    ApiErrorCode.MAX_FOLDER_DEPTH_EXCEEDED,
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage(
                            "error.folder.copy.max.depth.exceeded",
                            new Object[]{newMaxDepth, MAX_FOLDER_DEPTH},
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Generate unique name at destination
        String copyName = newName != null ? newName : generateUniqueCopyName(sourceFolder.getName(), destinationFolderId, userId);

        // Perform recursive copy
        Folder copiedFolder = copyFolderRecursive(sourceFolder, destinationParent, copyName, userId);

        log.info("Copied folder {} to destination {} for user {}", folderId, destinationFolderId, userId);

        return folderMapper.toResponse(copiedFolder);
    }

    private String generateUniqueCopyName(String baseName, UUID parentId, UUID userId) {
        String copyName = baseName + " (copy)";
        int counter = 2;

        while (folderRepository.existsByNameAndParent(userId, parentId, copyName)) {
            copyName = baseName + " (copy " + counter + ")";
            counter++;
        }

        return copyName;
    }

    private Folder copyFolderRecursive(Folder source, Folder newParent, String newName, UUID userId) {
        User user = getUserOrThrow(userId);

        // Create new folder
        Folder copiedFolder = Folder.builder()
                .user(user)
                .parentFolder(newParent)
                .name(newName)
                .description(source.getDescription())
                .build();

        copiedFolder.buildPath();

        copiedFolder = folderRepository.save(copiedFolder);

        // Copy child folders recursively
        List<Folder> children = folderRepository.findChildrenByUserIdAndParentId(userId, source.getId());
        for (Folder child : children) {
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
        Folder folder = getFolderEntityById(folderId, userId);

        LocalDateTime now = LocalDateTime.now();

        // Soft delete folder
        folder.setDeletedAt(now);
        folderRepository.save(folder);

        // Soft delete all descendants
        String path = folder.getPath() + "/";
        List<Folder> descendants = folderRepository.findDescendantsByPath(userId, path);
        int foldersDeleted = 1; // Include the folder itself

        for (Folder descendant : descendants) {
            descendant.setDeletedAt(now);
            folderRepository.save(descendant);
            foldersDeleted++;
        }

        // Soft delete all decks in folder tree
        // Get all folder IDs in the subtree
        List<UUID> folderIds = new ArrayList<>();
        folderIds.add(folderId);
        descendants.forEach(d -> folderIds.add(d.getId()));

        // Soft delete decks (if DeckRepository supports it)
        int decksDeleted = softDeleteDecksInFolders(folderIds, now);

        String message = messageSource.getMessage(
                "success.folder.deleted",
                new Object[]{folder.getName()},
                LocaleContextHolder.getLocale()
        );

        log.info("Soft deleted folder {} and {} descendants for user {}", folderId, foldersDeleted - 1, userId);

        return new DeletionSummary(foldersDeleted, decksDeleted, message);
    }

    private int softDeleteDecksInFolders(List<UUID> folderIds, LocalDateTime deletedAt) {
        // This method would soft delete all decks in the given folders
        // Implementation depends on DeckRepository having this capability
        // For now, returning 0 as placeholder
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public FolderResponse getFolderById(UUID folderId, UUID userId) {
        Folder folder = getFolderEntityById(folderId, userId);
        return folderMapper.toResponse(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getAllFolders(UUID userId) {
        List<Folder> folders = folderRepository.findAllByUserId(userId);
        return folders.stream()
                .map(folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getRootFolders(UUID userId) {
        List<Folder> folders = folderRepository.findRootFoldersByUserId(userId);
        return folders.stream()
                .map(folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderResponse> getChildFolders(UUID parentId, UUID userId) {
        // Verify parent exists and belongs to user
        getFolderEntityById(parentId, userId);

        List<Folder> folders = folderRepository.findChildrenByUserIdAndParentId(userId, parentId);
        return folders.stream()
                .map(folderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public FolderResponse restoreFolder(UUID folderId, UUID userId) {
        log.debug("Restoring folder {} for user {}", folderId, userId);

        // Find soft-deleted folder
        Folder folder = folderRepository.findDeletedByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new RepeatWiseException(
                        ApiErrorCode.FOLDER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage(
                                "error.folder.delete.not.found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        // Restore folder
        folder.setDeletedAt(null);
        folderRepository.save(folder);

        // Restore all descendants
        String path = folder.getPath() + "/";
        List<Folder> descendants = folderRepository.findDescendantsByPath(userId, path);
        for (Folder descendant : descendants) {
            descendant.setDeletedAt(null);
            folderRepository.save(descendant);
        }

        log.info("Restored folder {} and {} descendants for user {}", folderId, descendants.size(), userId);

        return folderMapper.toResponse(folder);
    }

    @Override
    @Transactional(readOnly = true)
    public Folder getFolderEntityById(UUID folderId, UUID userId) {
        return folderRepository.findByIdAndUserId(folderId, userId)
                .orElseThrow(() -> new RepeatWiseException(
                        ApiErrorCode.FOLDER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage(
                                "error.folder.not.found",
                                new Object[]{folderId},
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RepeatWiseException(
                        ApiErrorCode.USER_NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage(
                                "error.user.not.found",
                                new Object[]{userId},
                                LocaleContextHolder.getLocale()
                        )
                ));
    }
}
