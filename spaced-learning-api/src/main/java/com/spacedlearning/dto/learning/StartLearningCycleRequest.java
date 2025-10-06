package com.spacedlearning.dto.learning;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for starting a learning cycle based on UC-010: Start Learning Cycle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartLearningCycleRequest {

    @NotNull(message = "Set ID is required")
    private UUID setId;
}
