package com.repeatwise.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.request.user.UpdateUserRequest;
import com.repeatwise.dto.response.user.UserResponse;
import com.repeatwise.entity.User;

/**
 * MapStruct mapper for User entity
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, builder = @Builder(disableBuilder = true))
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    UserResponse toResponse(User user);

    /**
     * Alias for toResponse - Convert User entity to UserResponse DTO
     */
    default UserResponse toUserResponse(User user) {
        return toResponse(user);
    }

    /**
     * Convert RegisterRequest to User entity
     * Note: Password will be hashed separately in service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "folders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "srsSettings", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "cardBoxPositions", ignore = true)
    @Mapping(target = "reviewLogs", ignore = true)
    @Mapping(target = "userStats", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "language", ignore = true)
    @Mapping(target = "theme", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Update User entity from UpdateUserRequest
     * Only updates non-null fields
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "folders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "srsSettings", ignore = true)
    @Mapping(target = "refreshTokens", ignore = true)
    @Mapping(target = "cardBoxPositions", ignore = true)
    @Mapping(target = "reviewLogs", ignore = true)
    @Mapping(target = "userStats", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
