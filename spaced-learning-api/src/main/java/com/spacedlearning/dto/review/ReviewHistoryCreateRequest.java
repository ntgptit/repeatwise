package com.spacedlearning.dto.review;

import com.spacedlearning.entity.enums.ReviewStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewHistoryCreateRequest {

    @NotNull(message = "Set ID is required")
    private UUID setId;

    @NotNull(message = "Cycle number is required")
    @Min(value = 1, message = "Cycle number must be greater than 0")
    private Integer cycleNo;

    @NotNull(message = "Review number is required")
    @Min(value = 1, message = "Review number must be greater than 0")
    @Max(value = 5, message = "Review number must not exceed 5")
    private Integer reviewNo;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must not exceed 100")
    private Integer score;

    @NotNull(message = "Status is required")
    private ReviewStatus status;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;
}
