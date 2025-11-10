package com.repeatwise.service;

import java.util.List;
import java.util.UUID;

import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.entity.Folder;

/**
 * Service interface for Folder operations
 */
public interface FolderService {

    /**
     * UC-007: Create a new folder
     *
     * @param request CreateFolderRequest with name, description, parentFolderId
     * @param userId  ID of the authenticated user
     * @return FolderResponse with created folder details
     */
    FolderResponse createFolder(CreateFolderRequest request, UUID userId);

    /**
     * UC-008: Rename/update a folder
     *
     * @param folderId ID of the folder to update
     * @param request  UpdateFolderRequest with new name and/or description
     * @param userId   ID of the authenticated user
     * @return FolderResponse with updated folder details
     */
    FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId);

    /**
     * UC-009: Move a folder to a different parent
     *
     * @param folderId ID of the folder to move
     * @param request  MoveFolderRequest with target parent folder ID
     * @param userId   ID of the authenticated user
     * @return FolderResponse with updated folder details
     */
    FolderResponse moveFolder(UUID folderId, MoveFolderRequest request, UUID userId);

    /**
     * UC-010: Copy a folder and its subtree (sync mode for small folders)
     *
     * @param folderId            ID of the folder to copy
     * @param destinationFolderId ID of the destination parent folder (null for root)
     * @param newName             Optional new name for copied folder
     * @param userId              ID of the authenticated user
     * @return FolderResponse with copied folder details
     */
    FolderResponse copyFolder(UUID folderId, UUID destinationFolderId, String newName, UUID userId);

    /**
     * UC-011: Soft delete a folder and its entire subtree
     *
     * @param folderId ID of the folder to delete
     * @param userId   ID of the authenticated user
     * @return Deletion summary (folders deleted, decks deleted, etc.)
     */
    DeletionSummary deleteFolder(UUID folderId, UUID userId);

    /**
     * Get a single folder by ID
     *
     * @param folderId ID of the folder
     * @param userId   ID of the authenticated user
     * @return FolderResponse
     */
    FolderResponse getFolderById(UUID folderId, UUID userId);

    /**
     * Get all folders for a user (hierarchical tree)
     *
     * @param userId ID of the authenticated user
     * @return List of FolderResponse
     */
    List<FolderResponse> getAllFolders(UUID userId);

    /**
     * Get all root-level folders for a user
     *
     * @param userId ID of the authenticated user
     * @return List of FolderResponse
     */
    List<FolderResponse> getRootFolders(UUID userId);

    /**
     * Get all child folders of a parent
     *
     * @param parentId ID of the parent folder
     * @param userId   ID of the authenticated user
     * @return List of FolderResponse
     */
    List<FolderResponse> getChildFolders(UUID parentId, UUID userId);

    /**
     * Restore a soft-deleted folder (future feature)
     *
     * @param folderId ID of the folder to restore
     * @param userId   ID of the authenticated user
     * @return FolderResponse
     */
    FolderResponse restoreFolder(UUID folderId, UUID userId);

    /**
     * Get folder entity by ID (internal use)
     *
     * @param folderId ID of the folder
     * @param userId   ID of the authenticated user
     * @return Folder entity
     */
    Folder getFolderEntityById(UUID folderId, UUID userId);

    /**
     * Deletion summary DTO for delete operation
     */
    record DeletionSummary(
            int deletedFolders,
            int deletedDecks,
            String message) {
    }
}
