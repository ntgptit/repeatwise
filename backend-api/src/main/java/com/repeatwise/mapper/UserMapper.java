package com.repeatwise.mapper;

import com.repeatwise.dto.request.auth.RegisterRequest;
import com.repeatwise.dto.response.auth.UserResponse;
import com.repeatwise.dto.response.user.UserProfileResponse;
import com.repeatwise.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * User Mapper - MapStruct mapper for User entity
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-004: User Profile Management
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
     * Convert User entity to UserProfileResponse DTO
     * Used for profile endpoints (GET /api/users/me, PUT /api/users/me)
     *
     * @param user User entity
     * @return UserProfileResponse DTO
     */
    UserProfileResponse toProfileResponse(User user);

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
    @Mapping(target = "language", constant = "VI")
    @Mapping(target = "theme", constant = "SYSTEM")
    User toEntity(RegisterRequest request);
}
