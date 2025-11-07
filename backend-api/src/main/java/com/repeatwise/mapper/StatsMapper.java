package com.repeatwise.mapper;

import com.repeatwise.dto.response.folder.FolderStatsResponse;
import com.repeatwise.dto.response.stats.UserStatsResponse;
import com.repeatwise.entity.FolderStats;
import com.repeatwise.entity.UserStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for statistics entities
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatsMapper {

    /**
     * Convert UserStats entity to UserStatsResponse DTO
     */
    UserStatsResponse toResponse(UserStats userStats);

    /**
     * Convert FolderStats entity to FolderStatsResponse DTO
     */
    @Mapping(source = "folder.id", target = "folderId")
    FolderStatsResponse toResponse(FolderStats folderStats);
}
