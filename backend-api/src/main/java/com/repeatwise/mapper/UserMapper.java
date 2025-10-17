package com.repeatwise.mapper;

import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User Mapper - MapStruct mapper for User entity
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     *
     * @param user User entity
     * @return UserResponse DTO
     */
    UserResponse toResponse(User user);

    /**
     * Convert RegisterRequest to User entity
     * Note: password_hash must be set separately after bcrypt hashing
     *
     * @param request RegisterRequest DTO
     * @return User entity (without password hash)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "language", constant = "EN")
    @Mapping(target = "theme", constant = "LIGHT")
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    User toEntity(RegisterRequest request);
}
