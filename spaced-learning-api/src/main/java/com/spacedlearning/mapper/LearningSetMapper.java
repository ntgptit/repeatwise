package com.spacedlearning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.spacedlearning.dto.set.SetCreationRequest;
import com.spacedlearning.dto.set.SetCreationResponse;
import com.spacedlearning.entity.LearningSet;

/**
 * MapStruct mapper for LearningSet entity and DTOs
 * Handles mapping between LearningSet entity and various LearningSet DTOs
 */
@Mapper(componentModel = "spring")
public interface LearningSetMapper {

    LearningSetMapper INSTANCE = Mappers.getMapper(LearningSetMapper.class);

    /**
     * Maps SetCreationRequest to LearningSet entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "wordCount", constant = "0")
    @Mapping(target = "status", constant = "NOT_STARTED")
    @Mapping(target = "currentCycle", constant = "1")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "averageScore", ignore = true)
    @Mapping(target = "lastReviewedAt", ignore = true)
    @Mapping(target = "learningCycles", ignore = true)
    @Mapping(target = "reviewHistories", ignore = true)
    @Mapping(target = "remindSchedules", ignore = true)
    LearningSet toEntity(SetCreationRequest request);

    /**
     * Maps LearningSet entity to SetCreationResponse
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "wordCount", source = "wordCount")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "currentCycle", source = "currentCycle")
    @Mapping(target = "totalReviews", source = "totalReviews")
    @Mapping(target = "averageScore", source = "averageScore")
    @Mapping(target = "lastReviewedAt", source = "lastReviewedAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SetCreationResponse toCreationResponse(LearningSet learningSet);
}