package com.repeatwise.mapper;

import com.repeatwise.dto.SetDto;
import com.repeatwise.model.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetMapper implements BaseMapper<Set, SetDto> {

    private final ModelMapper modelMapper;

    @Override
    public SetDto toDto(Set entity) {
        if (entity == null) {
            return null;
        }
        SetDto dto = modelMapper.map(entity, SetDto.class);
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
        }
        return dto;
    }

    @Override
    public Set toEntity(SetDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Set.class);
    }

    @Override
    public List<SetDto> toDtoList(List<Set> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Set> toEntityList(List<SetDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Set updateEntity(Set entity, SetDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }

    /**
     * Map Set entity to SetDto.Response
     */
    public SetDto.Response toResponse(Set entity) {
        if (entity == null) {
            return null;
        }
        SetDto.Response response = modelMapper.map(entity, SetDto.Response.class);
        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
        }
        return response;
    }

    /**
     * Map SetDto.CreateRequest to Set entity
     */
    public Set toEntity(SetDto.CreateRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return modelMapper.map(createRequest, Set.class);
    }

    /**
     * Map SetDto.UpdateRequest to Set entity
     */
    public Set toEntity(SetDto.UpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }
        return modelMapper.map(updateRequest, Set.class);
    }

    /**
     * Map Set entity to SetDto.Summary
     */
    public SetDto.Summary toSummary(Set entity) {
        if (entity == null) {
            return null;
        }
        SetDto.Summary summary = new SetDto.Summary();
        summary.setId(entity.getId());
        summary.setName(entity.getName());
        summary.setDescription(entity.getDescription());
        summary.setWordCount(entity.getWordCount());
        summary.setStatus(entity.getStatus());
        summary.setCurrentCycle(entity.getCurrentCycle());
        return summary;
    }

    /**
     * Map list of Set entities to list of SetDto.Summary
     */
    public List<SetDto.Summary> toSummaryList(List<Set> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }
} 
