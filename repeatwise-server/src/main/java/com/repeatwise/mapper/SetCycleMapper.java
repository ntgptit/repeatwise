package com.repeatwise.mapper;

import com.repeatwise.dto.SetCycleDto;
import com.repeatwise.model.SetCycle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetCycleMapper implements BaseMapper<SetCycle, SetCycleDto> {

    private final ModelMapper modelMapper;

    @Override
    public SetCycleDto toDto(SetCycle entity) {
        if (entity == null) {
            return null;
        }
        SetCycleDto dto = modelMapper.map(entity, SetCycleDto.class);
        if (entity.getSet() != null) {
            dto.setSetId(entity.getSet().getId());
        }
        return dto;
    }

    @Override
    public SetCycle toEntity(SetCycleDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, SetCycle.class);
    }

    @Override
    public List<SetCycleDto> toDtoList(List<SetCycle> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SetCycle> toEntityList(List<SetCycleDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public SetCycle updateEntity(SetCycle entity, SetCycleDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }

    /**
     * Map SetCycle entity to SetCycleDto.Response
     */
    public SetCycleDto.Response toResponse(SetCycle entity) {
        if (entity == null) {
            return null;
        }
        SetCycleDto.Response response = modelMapper.map(entity, SetCycleDto.Response.class);
        if (entity.getSet() != null) {
            response.setSetId(entity.getSet().getId());
        }
        return response;
    }

    /**
     * Map SetCycleDto.CreateRequest to SetCycle entity
     */
    public SetCycle toEntity(SetCycleDto.CreateRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return modelMapper.map(createRequest, SetCycle.class);
    }

    /**
     * Map SetCycleDto.UpdateRequest to SetCycle entity
     */
    public SetCycle toEntity(SetCycleDto.UpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }
        return modelMapper.map(updateRequest, SetCycle.class);
    }

    /**
     * Map SetCycle entity to SetCycleDto.Summary
     */
    public SetCycleDto.Summary toSummary(SetCycle entity) {
        if (entity == null) {
            return null;
        }
        SetCycleDto.Summary summary = new SetCycleDto.Summary();
        summary.setId(entity.getId());
        summary.setCycleNo(entity.getCycleNo());
        summary.setStartedAt(entity.getStartedAt());
        summary.setFinishedAt(entity.getFinishedAt());
        summary.setAvgScore(entity.getAvgScore());
        summary.setStatus(entity.getStatus());
        return summary;
    }

    /**
     * Map list of SetCycle entities to list of SetCycleDto.Summary
     */
    public List<SetCycleDto.Summary> toSummaryList(List<SetCycle> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
} 
