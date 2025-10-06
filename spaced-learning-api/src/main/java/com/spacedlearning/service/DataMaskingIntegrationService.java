package com.spacedlearning.service;

import org.springframework.stereotype.Service;

import com.spacedlearning.masking.DataMaskingService;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.mapper.LearningSetMapper;
import com.spacedlearning.mapper.ReviewHistoryMapper;
import com.spacedlearning.mapper.RemindScheduleMapper;
import com.spacedlearning.mapper.StatisticsMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to integrate data masking with MapStruct mappers
 * Provides methods to map entities to DTOs with automatic data masking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataMaskingIntegrationService {

    private final DataMaskingService dataMaskingService;
    private final UserMapper userMapper;
    private final LearningSetMapper learningSetMapper;
    private final ReviewHistoryMapper reviewHistoryMapper;
    private final RemindScheduleMapper remindScheduleMapper;
    private final StatisticsMapper statisticsMapper;

    /**
     * Maps User entity to UserRegistrationResponse with data masking
     */
    public com.spacedlearning.dto.user.UserRegistrationResponse mapUserToRegistrationResponse(
            com.spacedlearning.entity.User user) {
        com.spacedlearning.dto.user.UserRegistrationResponse response = userMapper.toResponse(user);
        return dataMaskingService.maskSensitiveData(response);
    }

    /**
     * Maps LearningSet entity to SetCreationResponse
     */
    public com.spacedlearning.dto.set.SetCreationResponse mapLearningSetToCreationResponse(
            com.spacedlearning.entity.LearningSet learningSet) {
        return learningSetMapper.toCreationResponse(learningSet);
    }

    /**
     * Maps ReviewHistory entity to ReviewSessionResponse
     */
    public com.spacedlearning.dto.review.ReviewSessionResponse mapReviewHistoryToSessionResponse(
            com.spacedlearning.entity.ReviewHistory reviewHistory) {
        return reviewHistoryMapper.toSessionResponse(reviewHistory);
    }

    /**
     * Maps RemindSchedule entity to ReminderResponse
     */
    public com.spacedlearning.dto.reminder.ReminderResponse mapRemindScheduleToResponse(
            com.spacedlearning.entity.RemindSchedule remindSchedule) {
        return remindScheduleMapper.toResponse(remindSchedule);
    }

    /**
     * Maps LearningSet entity to SetProgress with data masking if needed
     */
    public com.spacedlearning.dto.statistics.StatisticsResponse.SetProgress mapLearningSetToSetProgress(
            com.spacedlearning.entity.LearningSet learningSet) {
        return statisticsMapper.toSetProgress(learningSet);
    }

    /**
     * Maps ReviewHistory entity to DailyProgress
     */
    public com.spacedlearning.dto.statistics.StatisticsResponse.DailyProgress mapReviewHistoryToDailyProgress(
            com.spacedlearning.entity.ReviewHistory reviewHistory) {
        return statisticsMapper.toDailyProgress(reviewHistory);
    }
}
