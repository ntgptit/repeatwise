package com.repeatwise.mapper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repeatwise.dto.response.notification.NotificationLogResponse;
import com.repeatwise.dto.response.notification.NotificationSettingsResponse;
import com.repeatwise.entity.NotificationLog;
import com.repeatwise.entity.NotificationSettings;

/**
 * Notification Mapper - MapStruct mapper for Notification entities
 *
 * Requirements:
 * - UC-024: Manage Notifications
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    /**
     * Convert NotificationSettings entity to Response DTO
     *
     * UC-024 Step 2: View Current Settings
     * Used for: GET /api/notifications/settings
     *
     * @param settings NotificationSettings entity
     * @return NotificationSettingsResponse DTO
     */
    @Mapping(target = "dailyReminderTime", source = "dailyReminderTime", qualifiedByName = "localTimeToString")
    @Mapping(target = "dailyReminderDays", source = "dailyReminderDays", qualifiedByName = "csvToList")
    @Mapping(target = "notificationMethod", expression = "java(settings.getNotificationMethod().name())")
    @Mapping(target = "nextReminderAt", ignore = true) // Calculated in service layer
    NotificationSettingsResponse toResponse(NotificationSettings settings);

    /**
     * Convert NotificationLog entity to Response DTO
     *
     * UC-024: Notification logs history
     * Used for: GET /api/notifications/logs
     *
     * @param log NotificationLog entity
     * @return NotificationLogResponse DTO
     */
    @Mapping(target = "type", expression = "java(log.getNotificationType().name())")
    @Mapping(target = "status", expression = "java(log.getStatus().name())")
    @Mapping(target = "metadata", source = "metadata", qualifiedByName = "jsonToMap")
    NotificationLogResponse toLogResponse(NotificationLog log);

    /**
     * Convert LocalTime to String (HH:MM format)
     *
     * @param time LocalTime object
     * @return String in HH:MM format (e.g., "09:00", "19:30")
     */
    @Named("localTimeToString")
    default String localTimeToString(final LocalTime time) {
        if (time == null) {
            return null;
        }
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Convert CSV string to List of Strings
     *
     * Example: "MON,TUE,WED" → ["MON", "TUE", "WED"]
     *
     * @param csv Comma-separated values
     * @return List of strings
     */
    @Named("csvToList")
    default List<String> csvToList(final String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                     .map(String::trim)
                     .collect(Collectors.toList());
    }

    /**
     * Convert JSONB string to Map
     *
     * Used for metadata field in NotificationLog
     *
     * @param json JSON string
     * @return Map representation
     */
    @Named("jsonToMap")
    default java.util.Map<String, Object> jsonToMap(final String json) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<java.util.Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            // Log error and return null
            return null;
        }
    }

    /**
     * Convert List of days to CSV string
     *
     * Example: ["MON", "TUE", "WED"] → "MON,TUE,WED"
     * Used when updating settings
     *
     * @param days List of day strings
     * @return CSV string
     */
    @Named("listToCsv")
    default String listToCsv(final List<String> days) {
        if (days == null || days.isEmpty()) {
            return null;
        }
        return String.join(",", days);
    }

    /**
     * Convert String time to LocalTime
     *
     * Example: "09:00" → LocalTime.of(9, 0)
     *
     * @param timeString Time in HH:MM format
     * @return LocalTime object
     */
    @Named("stringToLocalTime")
    default LocalTime stringToLocalTime(final String timeString) {
        if (timeString == null || timeString.isBlank()) {
            return null;
        }
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
    }
}
