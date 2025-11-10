package com.repeatwise.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repeatwise.dto.request.folder.CopyFolderRequest;
import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.MoveFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.service.FolderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for folder operations
 * Implements UC-007 to UC-011
 */
@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Folder Management", description = "APIs for managing folder hierarchy")
@SecurityRequirement(name = "bearerAuth")
public class FolderController {

    private final FolderService folderService;

    /**
     * UC-007: Create a new folder
     */
    @PostMapping
    @Operation(summary = "Create a new folder", description = "Creates a new folder in the hierarchy. Max depth is 10 levels.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Folder created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Parent folder not found")
    })
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} creating folder '{}'", userId, request.getName());

        final var response = this.folderService.createFolder(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * UC-008: Rename/update a folder
     */
    @PatchMapping("/{folderId}")
    @Operation(summary = "Update folder name and/or description", description = "Updates folder metadata. Hierarchy position remains unchanged.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or name conflict"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<FolderResponse> updateFolder(
            @PathVariable UUID folderId,
            @Valid @RequestBody UpdateFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} updating folder {}", userId, folderId);

        final var response = this.folderService.updateFolder(folderId, request, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * UC-009: Move a folder to a different parent
     */
    @PostMapping("/{folderId}/move")
    @Operation(summary = "Move folder to a different parent", description = "Moves folder and all its descendants to a new location in the hierarchy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder moved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid move operation (circular reference, depth exceeded, etc.)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder or destination not found")
    })
    public ResponseEntity<FolderResponse> moveFolder(
            @PathVariable UUID folderId,
            @Valid @RequestBody MoveFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} moving folder {} to parent {}", userId, folderId, request.getTargetParentFolderId());

        final var response = this.folderService.moveFolder(folderId, request, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * UC-010: Copy a folder (sync mode)
     */
    @PostMapping("/{folderId}/copy")
    @Operation(summary = "Copy folder and its subtree", description = "Creates a deep copy of the folder and all its contents. Max 500 items for sync copy.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder copied successfully"),
            @ApiResponse(responseCode = "400", description = "Folder too large or depth exceeded"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Source or destination folder not found")
    })
    public ResponseEntity<FolderResponse> copyFolder(
            @PathVariable UUID folderId,
            @Valid @RequestBody CopyFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} copying folder {} to destination {}", userId, folderId, request.getDestinationFolderId());

        final var response = this.folderService.copyFolder(
                folderId,
                request.getDestinationFolderId(),
                request.getNewName(),
                userId);

        return ResponseEntity.ok(response);
    }

    /**
     * UC-011: Delete a folder (soft delete)
     */
    @DeleteMapping("/{folderId}")
    @Operation(summary = "Delete folder and its subtree", description = "Soft deletes folder and all its contents. Recoverable for 30 days.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<Map<String, Object>> deleteFolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} deleting folder {}", userId, folderId);

        final var summary = this.folderService.deleteFolder(folderId, userId);

        final Map<String, Object> response = Map.of(
                "message", summary.message(),
                "deletedFolders", summary.deletedFolders(),
                "deletedDecks", summary.deletedDecks());

        return ResponseEntity.ok(response);
    }

    /**
     * Restore a soft-deleted folder
     */
    @PostMapping("/{folderId}/restore")
    @Operation(summary = "Restore a deleted folder", description = "Restores a soft-deleted folder and all its contents from trash.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder restored successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder not found in trash")
    })
    public ResponseEntity<FolderResponse> restoreFolder(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());
        log.info("User {} restoring folder {}", userId, folderId);

        final var response = this.folderService.restoreFolder(folderId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get a single folder by ID
     */
    @GetMapping("/{folderId}")
    @Operation(summary = "Get folder by ID", description = "Retrieves a single folder's details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folder retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Folder not found")
    })
    public ResponseEntity<FolderResponse> getFolderById(
            @PathVariable UUID folderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());

        final var response = this.folderService.getFolderById(folderId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get all folders for current user
     */
    @GetMapping
    @Operation(summary = "Get all folders", description = "Retrieves all folders for the authenticated user (hierarchical tree).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Folders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FolderResponse>> getAllFolders(
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());

        final var folders = this.folderService.getAllFolders(userId);

        return ResponseEntity.ok(folders);
    }

    /**
     * Get root-level folders
     */
    @GetMapping("/root")
    @Operation(summary = "Get root folders", description = "Retrieves all root-level folders (no parent) for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Root folders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<FolderResponse>> getRootFolders(
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());

        final var folders = this.folderService.getRootFolders(userId);

        return ResponseEntity.ok(folders);
    }

    /**
     * Get child folders of a parent
     */
    @GetMapping("/{parentId}/children")
    @Operation(summary = "Get child folders", description = "Retrieves all direct children of a parent folder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Child folders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Parent folder not found")
    })
    public ResponseEntity<List<FolderResponse>> getChildFolders(
            @PathVariable UUID parentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        final var userId = UUID.fromString(userDetails.getUsername());

        final var folders = this.folderService.getChildFolders(parentId, userId);

        return ResponseEntity.ok(folders);
    }
}
