package com.spacedlearning.dto.reminder;

import com.spacedlearning.entity.enums.RemindStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemindScheduleResponse {

    private UUID id;
    private UUID setId;
    private String setName;
    private UUID userId;
    private LocalDate remindDate;
    private RemindStatus status;
    private Integer rescheduleCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
