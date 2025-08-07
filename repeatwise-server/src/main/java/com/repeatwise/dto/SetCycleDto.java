package com.repeatwise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.repeatwise.enums.CycleStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SetCycleDto extends BaseDto {

    private UUID setId;

    @NotNull(message = "Cycle number is required")
    @Min(value = 1, message = "Cycle number must be at least 1")
    private Integer cycleNo;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishedAt;

    @DecimalMin(value = "0.0", message = "Average score must be at least 0")
    @DecimalMax(value = "100.0", message = "Average score must not exceed 100")
    private BigDecimal avgScore;

    @Min(value = 0, message = "Next cycle delay days must be at least 0")
    private Integer nextCycleDelayDays;

    private CycleStatus status;

    private List<SetReviewDto> reviews;

    @Data
    public static class CreateRequest {
        @NotNull(message = "Cycle number is required")
        @Min(value = 1, message = "Cycle number must be at least 1")
        private Integer cycleNo;

        @NotNull(message = "Start date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate startedAt;
    }

    @Data
    public static class UpdateRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate finishedAt;

        @DecimalMin(value = "0.0", message = "Average score must be at least 0")
        @DecimalMax(value = "100.0", message = "Average score must not exceed 100")
        private BigDecimal avgScore;

        @Min(value = 0, message = "Next cycle delay days must be at least 0")
        private Integer nextCycleDelayDays;

        private CycleStatus status;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Response extends SetCycleDto {
    }

    @Data
    public static class Summary {
        private UUID id;
        private Integer cycleNo;
        private LocalDate startedAt;
        private LocalDate finishedAt;
        private BigDecimal avgScore;
        private CycleStatus status;
    }
} 