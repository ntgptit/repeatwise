package com.repeatwise.mapper;

import com.repeatwise.dto.NotificationDto;
import com.repeatwise.model.Notification;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper implements BaseMapper<Notification, NotificationDto> {

    private final ModelMapper modelMapper;

    public NotificationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Notification toEntity(NotificationDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, Notification.class);
    }

    @Override
    public NotificationDto toDto(Notification entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, NotificationDto.class);
    }

    @Override
    public List<NotificationDto> toDtoList(List<Notification> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> toEntityList(List<NotificationDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Notification updateEntity(Notification entity, NotificationDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }
} 
