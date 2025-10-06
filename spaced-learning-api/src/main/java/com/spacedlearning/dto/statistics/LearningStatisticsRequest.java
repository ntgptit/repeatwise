package com.spacedlearning.dto.statistics;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for learning statistics request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatisticsRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID setId; // Optional: filter by specific set

    private LocalDate startDate; // Optional: filter by date range

    private LocalDate endDate; // Optional: filter by date range

    private String statType; // Optional: filter by statistic type
}
