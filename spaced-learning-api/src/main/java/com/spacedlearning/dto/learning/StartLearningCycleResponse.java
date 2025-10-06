package com.spacedlearning.dto.learning;

import com.spacedlearning.entity.enums.SetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for learning cycle start response based on UC-010: Start Learning Cycle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartLearningCycleResponse {

    private UUID setId;
    private String setName;
    private Integer currentCycle;
    private SetStatus status;
    private LocalDateTime cycleStartTime;
    private List<ReminderScheduleInfo> reminderSchedules;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReminderScheduleInfo {
        private Integer reviewNumber;
        private LocalDateTime scheduledTime;
        private String status;
    }
}
