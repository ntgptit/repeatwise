package com.spacedlearning.dto.reminder;

import java.time.LocalDate;
import java.util.UUID;

import com.spacedlearning.entity.enums.RemindStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reminder request
 * Contains all necessary fields for creating or updating a reminder schedule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {

    @NotNull(message = "Learning set ID is required")
    private UUID setId;

    @NotNull(message = "Reminder date is required")
    private LocalDate remindDate;

    @NotNull(message = "Reminder status is required")
    @Builder.Default
    private RemindStatus status = RemindStatus.PENDING;
}
