package com.spacedlearning.mapper;

import com.spacedlearning.dto.reminder.RemindScheduleCreateRequest;
import com.spacedlearning.dto.reminder.RemindScheduleResponse;
import com.spacedlearning.dto.reminder.RemindScheduleUpdateRequest;
import com.spacedlearning.entity.RemindSchedule;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RemindScheduleMapper {

    private final ModelMapper modelMapper;

    public RemindSchedule toEntity(RemindScheduleCreateRequest request) {
        return modelMapper.map(request, RemindSchedule.class);
    }

    public void updateEntityFromRequest(RemindScheduleUpdateRequest request, RemindSchedule entity) {
        if (request.getRemindDate() != null) {
            entity.setRemindDate(request.getRemindDate());
        }
    }

    public RemindScheduleResponse toResponse(RemindSchedule entity) {
        RemindScheduleResponse response = modelMapper.map(entity, RemindScheduleResponse.class);
        
        // Set additional fields
        if (entity.getSet() != null) {
            response.setSetId(entity.getSet().getId());
            response.setSetName(entity.getSet().getName());
        }
        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
        }
        
        return response;
    }

    public List<RemindScheduleResponse> toResponseList(List<RemindSchedule> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
