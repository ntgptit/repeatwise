package com.spacedlearning.mapper;

import com.spacedlearning.dto.set.LearningSetCreateRequest;
import com.spacedlearning.dto.set.LearningSetDetailResponse;
import com.spacedlearning.dto.set.LearningSetResponse;
import com.spacedlearning.dto.set.LearningSetUpdateRequest;
import com.spacedlearning.entity.LearningSet;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LearningSetMapper {

    private final ModelMapper modelMapper;
    private final ReviewHistoryMapper reviewHistoryMapper;
    private final RemindScheduleMapper remindScheduleMapper;

    public LearningSet toEntity(LearningSetCreateRequest request) {
        return modelMapper.map(request, LearningSet.class);
    }

    public void updateEntityFromRequest(LearningSetUpdateRequest request, LearningSet entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            entity.setCategory(request.getCategory());
        }
        if (request.getWordCount() != null) {
            entity.setWordCount(request.getWordCount());
        }
    }

    public LearningSetResponse toResponse(LearningSet entity) {
        return modelMapper.map(entity, LearningSetResponse.class);
    }

    public LearningSetDetailResponse toDetailResponse(LearningSet entity) {
        LearningSetDetailResponse response = modelMapper.map(entity, LearningSetDetailResponse.class);
        
        // Map review histories
        if (entity.getReviewHistories() != null) {
            response.setReviewHistories(
                entity.getReviewHistories().stream()
                    .map(reviewHistoryMapper::toResponse)
                    .toList()
            );
        }
        
        // Map remind schedules
        if (entity.getRemindSchedules() != null) {
            response.setRemindSchedules(
                entity.getRemindSchedules().stream()
                    .map(remindScheduleMapper::toResponse)
                    .toList()
            );
        }
        
        return response;
    }

    public List<LearningSetResponse> toResponseList(List<LearningSet> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<LearningSetDetailResponse> toDetailResponseList(List<LearningSet> entities) {
        return entities.stream()
                .map(this::toDetailResponse)
                .toList();
    }
}
