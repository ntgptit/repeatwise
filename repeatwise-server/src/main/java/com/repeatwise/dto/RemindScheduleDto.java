package com.repeatwise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.repeatwise.enums.RemindStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RemindScheduleDto extends BaseDto {

    private UUID userId;
    private UUID setId;

    @NotNull(message = "Scheduled date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;

    private RemindStatus status;

    private UUID rescheduledBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime rescheduledAt;

    @Size(max = 1000, message = "Reschedule reason must not exceed 1000 characters")
    private String rescheduleReason;

    @Data
    public static class CreateRequest {
        @NotNull(message = "Set ID is required")
        private UUID setId;

        @NotNull(message = "Scheduled date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate scheduledDate;
    }

    @Data
    public static class UpdateRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate scheduledDate;

        private RemindStatus status;

        @Size(max = 1000, message = "Reschedule reason must not exceed 1000 characters")
        private String rescheduleReason;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Response extends RemindScheduleDto {
    }

    @Data
    public static class Summary {
        private UUID id;
        private LocalDate scheduledDate;
        private RemindStatus status;
        private String rescheduleReason;
    }
} 