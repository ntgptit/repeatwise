package com.repeatwise.mapper;

import com.repeatwise.dto.RemindScheduleDto;
import com.repeatwise.model.RemindSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemindScheduleMapper implements BaseMapper<RemindSchedule, RemindScheduleDto> {

    private final ModelMapper modelMapper;

    @Override
    public RemindScheduleDto toDto(RemindSchedule entity) {
        if (entity == null) {
            return null;
        }
        RemindScheduleDto dto = modelMapper.map(entity, RemindScheduleDto.class);
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        if (entity.getSet() != null) {
            dto.setSetId(entity.getSet().getId());
        }
        if (entity.getRescheduledBy() != null) {
            dto.setRescheduledBy(entity.getRescheduledBy().getId());
        }
        return dto;
    }

    @Override
    public RemindSchedule toEntity(RemindScheduleDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, RemindSchedule.class);
    }

    @Override
    public List<RemindScheduleDto> toDtoList(List<RemindSchedule> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RemindSchedule> toEntityList(List<RemindScheduleDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RemindSchedule updateEntity(RemindSchedule entity, RemindScheduleDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }

    /**
     * Map RemindSchedule entity to RemindScheduleDto.Response
     */
    public RemindScheduleDto.Response toResponse(RemindSchedule entity) {
        if (entity == null) {
            return null;
        }
        RemindScheduleDto.Response response = modelMapper.map(entity, RemindScheduleDto.Response.class);
        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
        }
        if (entity.getSet() != null) {
            response.setSetId(entity.getSet().getId());
        }
        if (entity.getRescheduledBy() != null) {
            response.setRescheduledBy(entity.getRescheduledBy().getId());
        }
        return response;
    }

    /**
     * Map RemindScheduleDto.CreateRequest to RemindSchedule entity
     */
    public RemindSchedule toEntity(RemindScheduleDto.CreateRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return modelMapper.map(createRequest, RemindSchedule.class);
    }

    /**
     * Map RemindScheduleDto.UpdateRequest to RemindSchedule entity
     */
    public RemindSchedule toEntity(RemindScheduleDto.UpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }
        return modelMapper.map(updateRequest, RemindSchedule.class);
    }

    /**
     * Map RemindSchedule entity to RemindScheduleDto.Summary
     */
    public RemindScheduleDto.Summary toSummary(RemindSchedule entity) {
        if (entity == null) {
            return null;
        }
        RemindScheduleDto.Summary summary = new RemindScheduleDto.Summary();
        summary.setId(entity.getId());
        summary.setScheduledDate(entity.getScheduledDate());
        summary.setStatus(entity.getStatus());
        summary.setRescheduleReason(entity.getRescheduleReason());
        return summary;
    }

    /**
     * Map list of RemindSchedule entities to list of RemindScheduleDto.Summary
     */
    public List<RemindScheduleDto.Summary> toSummaryList(List<RemindSchedule> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
} 
