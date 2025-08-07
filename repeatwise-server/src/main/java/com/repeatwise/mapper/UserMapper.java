package com.repeatwise.mapper;

import com.repeatwise.dto.UserDto;
import com.repeatwise.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserMapper implements BaseMapper<User, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, UserDto.class);
    }

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, User.class);
    }

    @Override
    public List<UserDto> toDtoList(List<User> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> toEntityList(List<UserDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public User updateEntity(User entity, UserDto dto) {
        if (entity == null || dto == null) {
            return entity;
        }
        modelMapper.map(dto, entity);
        return entity;
    }

    /**
     * Map User entity to UserDto.Response
     */
    public UserDto.Response toResponse(User entity) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, UserDto.Response.class);
    }

    /**
     * Map UserDto.CreateRequest to User entity
     */
    public User toEntity(UserDto.CreateRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        return modelMapper.map(createRequest, User.class);
    }

    /**
     * Map UserDto.UpdateRequest to User entity
     */
    public User toEntity(UserDto.UpdateRequest updateRequest) {
        if (updateRequest == null) {
            return null;
        }
        return modelMapper.map(updateRequest, User.class);
    }
} 
