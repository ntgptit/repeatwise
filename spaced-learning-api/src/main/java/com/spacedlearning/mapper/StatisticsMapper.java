package com.spacedlearning.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.spacedlearning.dto.statistics.StatisticsResponse;
import com.spacedlearning.dto.statistics.StatisticsResponse.DailyProgress;
import com.spacedlearning.dto.statistics.StatisticsResponse.SetProgress;
import com.spacedlearning.entity.LearningSet;
import com.spacedlearning.entity.ReviewHistory;

/**
 * MapStruct mapper for Statistics DTOs
 * Handles mapping between entities and statistics response DTOs
 */
@Mapper(componentModel = "spring")
public interface StatisticsMapper {

    StatisticsMapper INSTANCE = Mappers.getMapper(StatisticsMapper.class);

    /**
     * Maps LearningSet entity to SetProgress DTO
     */
    @Mapping(target = "setName", source = "name")
    @Mapping(target = "totalReviews", source = "totalReviews")
    @Mapping(target = "averageScore", source = "averageScore")
    @Mapping(target = "currentCycle", source = "currentCycle")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "lastReviewedAt", source = "lastReviewedAt")
    SetProgress toSetProgress(LearningSet learningSet);

    /**
     * Maps list of LearningSet entities to list of SetProgress DTOs
     */
    List<SetProgress> toSetProgressList(List<LearningSet> learningSets);

    /**
     * Maps ReviewHistory entity to DailyProgress DTO
     */
    @Mapping(target = "date", source = "reviewDate")
    @Mapping(target = "reviewsCompleted", constant = "1")
    @Mapping(target = "averageScore", source = "score")
    @Mapping(target = "studyTime", constant = "0")
    DailyProgress toDailyProgress(ReviewHistory reviewHistory);

    /**
     * Maps list of ReviewHistory entities to list of DailyProgress DTOs
     */
    List<DailyProgress> toDailyProgressList(List<ReviewHistory> reviewHistories);
}
