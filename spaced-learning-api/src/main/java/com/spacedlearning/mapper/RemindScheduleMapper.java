package com.spacedlearning.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.spacedlearning.dto.reminder.ReminderRequest;
import com.spacedlearning.dto.reminder.ReminderResponse;
import com.spacedlearning.entity.RemindSchedule;

/**
 * MapStruct mapper for RemindSchedule entity and DTOs
 * Handles mapping between RemindSchedule entity and various RemindSchedule DTOs
 */
@Mapper(componentModel = "spring")
public interface RemindScheduleMapper {

    RemindScheduleMapper INSTANCE = Mappers.getMapper(RemindScheduleMapper.class);

    /**
     * Maps ReminderRequest to RemindSchedule entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "set", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "remindDate", source = "remindDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "rescheduleCount", constant = "0")
    RemindSchedule toEntity(ReminderRequest request);

    /**
     * Maps RemindSchedule entity to ReminderResponse
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "setId", source = "set.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "remindDate", source = "remindDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "rescheduleCount", source = "rescheduleCount")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ReminderResponse toResponse(RemindSchedule remindSchedule);
}