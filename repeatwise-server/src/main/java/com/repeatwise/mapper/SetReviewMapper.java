package com.repeatwise.mapper;

import com.repeatwise.dto.SetReviewDto;
import com.repeatwise.model.SetReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetReviewMapper implements BaseMapper<SetReview, SetReviewDto> {

    private final ModelMapper modelMapper;

    @Override
    public SetReviewDto toDto(SetReview entity) {
        if (entity == null) {
            return null;
        }
        SetReviewDto dto = modelMapper.map(entity, SetReviewDto.class);
        if (entity.getSetCycle() != null) {
            dto.setSetCycleId(entity.getSetCycle().getId());
        }
        return dto;
    }

    @Override
    public SetReview toEntity(SetReviewDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, SetReview.class);
    }

    @Override
    public List<SetReviewDto> toDtoList(List<SetReview> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SetReview> toEntityList(List<SetReviewDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public SetReview updateEntity(SetReview entity, SetReviewDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }

    /**
     * Map SetReview entity to SetReviewDto.Response
     */
    public SetReviewDto.Response toResponse(SetReview entity) {
        if (entity == null) {
            return null;
        }
        SetReviewDto.Response response = modelMapper.map(entity, SetReviewDto.Response.class);
        if (entity.getSetCycle() != null) {
            response.setSetCycleId(entity.getSetCycle().getId());
        }
        return response;
    }

    /**
     * Map SetReviewDto.CreateRequest to SetReview entity
     */
    public SetReview toEntity(SetReviewDto.CreateRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return modelMapper.map(createRequest, SetReview.class);
    }

    /**
     * Map SetReviewDto.UpdateRequest to SetReview entity
     */
    public SetReview toEntity(SetReviewDto.UpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }
        return modelMapper.map(updateRequest, SetReview.class);
    }

    /**
     * Map SetReview entity to SetReviewDto.Summary
     */
    public SetReviewDto.Summary toSummary(SetReview entity) {
        if (entity == null) {
            return null;
        }
        SetReviewDto.Summary summary = new SetReviewDto.Summary();
        summary.setId(entity.getId());
        summary.setReviewNo(entity.getReviewNo());
        summary.setReviewedAt(entity.getReviewedAt());
        summary.setScore(entity.getScore());
        return summary;
    }

    /**
     * Map list of SetReview entities to list of SetReviewDto.Summary
     */
    public List<SetReviewDto.Summary> toSummaryList(List<SetReview> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
} 
