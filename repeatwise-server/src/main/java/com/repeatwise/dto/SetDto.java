package com.repeatwise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.repeatwise.enums.SetStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SetDto extends BaseDto {

    private UUID userId;

    @NotBlank(message = "Set name is required")
    @Size(min = 1, max = 128, message = "Set name must be between 1 and 128 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Word count is required")
    @Min(value = 1, message = "Word count must be at least 1")
    @Max(value = 10000, message = "Word count must not exceed 10000")
    private Integer wordCount;

    private SetStatus status;

    @Min(value = 1, message = "Current cycle must be at least 1")
    private Integer currentCycle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCycleEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextCycleStartDate;

    private List<SetCycleDto> cycles;

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Set name is required")
        @Size(min = 1, max = 128, message = "Set name must be between 1 and 128 characters")
        private String name;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @NotNull(message = "Word count is required")
        @Min(value = 1, message = "Word count must be at least 1")
        @Max(value = 10000, message = "Word count must not exceed 10000")
        private Integer wordCount;
    }

    @Data
    public static class UpdateRequest {
        @Size(min = 1, max = 128, message = "Set name must be between 1 and 128 characters")
        private String name;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Min(value = 1, message = "Word count must be at least 1")
        @Max(value = 10000, message = "Word count must not exceed 10000")
        private Integer wordCount;

        private SetStatus status;

        @Min(value = 1, message = "Current cycle must be at least 1")
        private Integer currentCycle;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate lastCycleEndDate;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate nextCycleStartDate;
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Response extends SetDto {
    }

    @Data
    public static class Summary {
        private UUID id;
        private String name;
        private String description;
        private Integer wordCount;
        private SetStatus status;
        private Integer currentCycle;
    }
} 