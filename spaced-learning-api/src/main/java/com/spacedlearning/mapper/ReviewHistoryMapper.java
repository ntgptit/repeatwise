package com.spacedlearning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.spacedlearning.dto.review.ReviewSessionRequest;
import com.spacedlearning.dto.review.ReviewSessionResponse;
import com.spacedlearning.entity.ReviewHistory;

/**
 * MapStruct mapper for ReviewHistory entity and DTOs
 * Handles mapping between ReviewHistory entity and various ReviewHistory DTOs
 */
@Mapper(componentModel = "spring")
public interface ReviewHistoryMapper {

    ReviewHistoryMapper INSTANCE = Mappers.getMapper(ReviewHistoryMapper.class);

    /**
     * Maps ReviewSessionRequest to ReviewHistory entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "learningSet", ignore = true)
    @Mapping(target = "learningCycle", ignore = true)
    @Mapping(target = "reviewNumber", source = "reviewNumber")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "skipReason", source = "skipReason")
    @Mapping(target = "reviewDate", source = "reviewDate")
    @Mapping(target = "notes", source = "notes")
    ReviewHistory toEntity(ReviewSessionRequest request);

    /**
     * Maps ReviewHistory entity to ReviewSessionResponse
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "setId", source = "learningSet.id")
    @Mapping(target = "cycleId", source = "learningCycle.id")
    @Mapping(target = "reviewNumber", source = "reviewNumber")
    @Mapping(target = "score", source = "score")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "skipReason", source = "skipReason")
    @Mapping(target = "reviewDate", source = "reviewDate")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ReviewSessionResponse toSessionResponse(ReviewHistory reviewHistory);
}