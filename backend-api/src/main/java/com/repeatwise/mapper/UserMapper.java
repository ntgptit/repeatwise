package com.repeatwise.mapper;

import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.request.user.UpdateUserRequest;
import com.repeatwise.dto.response.user.UserResponse;
import com.repeatwise.entity.User;
import org.mapstruct.*;

/**
 * MapStruct mapper for User entity
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    UserResponse toResponse(User user);

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
