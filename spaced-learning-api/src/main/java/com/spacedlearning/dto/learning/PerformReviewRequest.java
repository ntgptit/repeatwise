package com.spacedlearning.dto.learning;

import com.spacedlearning.entity.enums.SkipReason;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for performing review session based on UC-011: Perform Review Session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformReviewRequest {

    @NotNull(message = "Set ID is required")
    private UUID setId;

    @NotNull(message = "Cycle number is required")
    @Min(value = 1, message = "Cycle number must be at least 1")
    private Integer cycleNumber;

    @NotNull(message = "Review number is required")
    @Min(value = 1, message = "Review number must be at least 1")
    @Max(value = 5, message = "Review number must be at most 5")
    private Integer reviewNumber;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must be at most 100")
    private Integer score;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    private SkipReason skipReason;

    /**
     * Check if this is a skip request
     */
    public boolean isSkipRequest() {
        return skipReason != null;
    }

    /**
     * Check if this is a score submission
     */
    public boolean isScoreSubmission() {
        return score != null && skipReason == null;
    }
}
