package com.spacedlearning.dto.review;

import java.time.LocalDate;
import java.util.UUID;

import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.entity.enums.SkipReason;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for review session request
 * Contains all necessary fields for creating or updating a review session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSessionRequest {

    @NotNull(message = "Learning set ID is required")
    private UUID setId;

    @NotNull(message = "Learning cycle ID is required")
    private UUID cycleId;

    @NotNull(message = "Review number is required")
    @Min(value = 1, message = "Review number must be at least 1")
    @Max(value = 5, message = "Review number must be at most 5")
    private Integer reviewNumber;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must be at most 100")
    private Integer score;

    @NotNull(message = "Review status is required")
    private ReviewStatus status;

    private SkipReason skipReason;

    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
