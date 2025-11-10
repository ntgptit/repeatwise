package com.repeatwise.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.repeatwise.dto.request.folder.CreateFolderRequest;
import com.repeatwise.dto.request.folder.UpdateFolderRequest;
import com.repeatwise.dto.response.folder.FolderResponse;
import com.repeatwise.entity.Folder;

/**
 * MapStruct mapper for Folder entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, builder = @Builder(disableBuilder = true))
public interface FolderMapper {

    /**
     * Convert Folder entity to FolderResponse DTO
     */
    @Mapping(source = "parentFolder.id", target = "parentFolderId")
    FolderResponse toResponse(Folder folder);

    /**
     * Convert CreateFolderRequest to Folder entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentFolder", ignore = true)
    @Mapping(target = "depth", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "pathSegments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "childFolders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "folderStats", ignore = true)
    Folder toEntity(CreateFolderRequest request);

    /**
     * Update Folder entity from UpdateFolderRequest
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "parentFolder", ignore = true)
    @Mapping(target = "depth", ignore = true)
    @Mapping(target = "path", ignore = true)
    @Mapping(target = "pathSegments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "childFolders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "folderStats", ignore = true)
    void updateEntityFromRequest(UpdateFolderRequest request, @MappingTarget Folder folder);
}
