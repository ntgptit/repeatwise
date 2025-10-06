package com.spacedlearning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserRegistrationRequest;
import com.spacedlearning.dto.user.UserRegistrationResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.entity.User;

/**
 * MapStruct mapper for User entity and DTOs
 * Handles mapping between User entity and various User DTOs
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Maps UserRegistrationRequest to User entity
     * Note: password will be hashed by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "learningSets", ignore = true)
    @Mapping(target = "remindSchedules", ignore = true)
    @Mapping(target = "reviewHistories", ignore = true)
    @Mapping(target = "activityLogs", ignore = true)
    User toEntity(UserRegistrationRequest request);

    /**
     * Maps User entity to UserRegistrationResponse
     * Applies data masking for sensitive fields
     */
    @Mapping(target = "email", source = "email")
    UserRegistrationResponse toRegistrationResponse(User user);

    /**
     * Maps RegisterRequest to User entity
     * Note: password will be hashed by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "learningSets", ignore = true)
    @Mapping(target = "remindSchedules", ignore = true)
    @Mapping(target = "reviewHistories", ignore = true)
    @Mapping(target = "activityLogs", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User registerRequestToEntity(RegisterRequest request);

    /**
     * Maps User entity to UserResponse
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "preferredLanguage", source = "preferredLanguage")
    @Mapping(target = "timezone", source = "timezone")
    @Mapping(target = "defaultReminderTime", source = "defaultReminderTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toDto(User user);

    /**
     * Maps User entity to UserRegistrationResponse with custom field mapping
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "preferredLanguage", source = "preferredLanguage")
    @Mapping(target = "timezone", source = "timezone")
    @Mapping(target = "defaultReminderTime", source = "defaultReminderTime")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserRegistrationResponse toResponse(User user);
}