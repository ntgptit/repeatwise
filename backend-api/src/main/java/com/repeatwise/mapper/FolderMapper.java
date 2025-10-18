package com.repeatwise.mapper;

import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.dto.response.folder.FolderTreeResponse;
import com.repeatwise.entity.Folder;
import com.repeatwise.entity.FolderStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Folder Mapper - MapStruct mapper for Folder entity and DTOs
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-005 to UC-010: Folder management
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface FolderMapper {

    // ==================== Entity to Response ====================

    /**
     * Convert Folder entity to FolderResponse DTO
     * Used for single folder details (GET /api/folders/{id})
     *
     * @param folder Folder entity
     * @return FolderResponse DTO
     */
    @Mapping(target = "parentFolderId", source = "parentFolder.id")
    @Mapping(target = "childrenCount", expression = "java(folder.getChildFolders().size())")
    @Mapping(target = "deckCount", expression = "java(folder.getDecks().size())")
    FolderResponse toResponse(Folder folder);

    /**
     * Convert list of Folder entities to FolderResponse DTOs
     *
     * @param folders List of Folder entities
     * @return List of FolderResponse DTOs
     */
    List<FolderResponse> toResponseList(List<Folder> folders);

    /**
     * Convert Folder entity to FolderTreeResponse DTO
     * Used for tree view with statistics (GET /api/folders)
     *
     * Note: Statistics (totalCards, dueCards, etc.) are set separately
     * from FolderStats entity or calculated
     *
     * @param folder Folder entity
     * @return FolderTreeResponse DTO
     */
    @Mapping(target = "parentId", source = "parentFolder.id")
    @Mapping(target = "childrenCount", expression = "java(folder.getChildFolders().size())")
    @Mapping(target = "deckCount", expression = "java(folder.getDecks().size())")
    @Mapping(target = "totalCards", constant = "0")
    @Mapping(target = "dueCards", constant = "0")
    @Mapping(target = "newCards", constant = "0")
    @Mapping(target = "matureCards", constant = "0")
    @Mapping(target = "children", ignore = true)
    FolderTreeResponse toTreeResponse(Folder folder);

    /**
     * Convert list of Folder entities to FolderTreeResponse DTOs
     *
     * @param folders List of Folder entities
     * @return List of FolderTreeResponse DTOs
     */
    List<FolderTreeResponse> toTreeResponseList(List<Folder> folders);

    /**
     * Convert FolderStats entity to FolderStatsResponse DTO
     * Used for folder statistics endpoint (GET /api/folders/{id}/stats)
     *
     * @param stats FolderStats entity
     * @return FolderStatsResponse DTO
     */
    @Mapping(target = "folderId", source = "folder.id")
    @Mapping(target = "folderName", source = "folder.name")
    @Mapping(target = "totalCards", source = "totalCardsCount")
    @Mapping(target = "dueCards", source = "dueCardsCount")
    @Mapping(target = "newCards", source = "newCardsCount")
    @Mapping(target = "learningCards", expression = "java(calculateLearningCards(stats))")
    @Mapping(target = "matureCards", source = "matureCardsCount")
    @Mapping(target = "isStale", expression = "java(stats.isStale())")
    FolderStatsResponse toStatsResponse(FolderStats stats);

    // ==================== Request to Entity ====================

    /**
     * Convert CreateFolderRequest to Folder entity
     * Note: path, depth, user, parentFolder must be set separately
     *
     * @param request CreateFolderRequest DTO
     * @return Folder entity (partial)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentFolder", ignore = true)
    @Mapping(target = "childFolders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "depth", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Folder toEntity(CreateFolderRequest request);

    // ==================== Helper Methods ====================

    /**
     * Calculate learning cards count (total - new - mature)
     * Learning cards are in boxes 1-4
     *
     * @param stats FolderStats entity
     * @return Learning cards count
     */
    default Integer calculateLearningCards(final FolderStats stats) {
        if (stats == null) {
            return 0;
        }
        final int learning = stats.getTotalCardsCount()
            - stats.getNewCardsCount()
            - stats.getMatureCardsCount();
        return Math.max(0, learning);
    }
}
