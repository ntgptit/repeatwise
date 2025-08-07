package com.repeatwise.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SetReviewDto extends BaseDto {

    private UUID setCycleId;

    @NotNull(message = "Review number is required")
    @Min(value = 1, message = "Review number must be at least 1")
    @Max(value = 5, message = "Review number must not exceed 5")
    private Integer reviewNo;

    @NotNull(message = "Review date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewedAt;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must not exceed 100")
    private Integer score;

    public static class CreateRequest {
        @NotNull(message = "Review number is required")
        @Min(value = 1, message = "Review number must be at least 1")
        @Max(value = 5, message = "Review number must not exceed 5")
        private Integer reviewNo;

        @NotNull(message = "Review date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate reviewedAt;

        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score must be at least 0")
        @Max(value = 100, message = "Score must not exceed 100")
        private Integer score;

        // Getters and setters
        public Integer getReviewNo() { return reviewNo; }
        public void setReviewNo(Integer reviewNo) { this.reviewNo = reviewNo; }
        public LocalDate getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDate reviewedAt) { this.reviewedAt = reviewedAt; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }

    public static class UpdateRequest {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate reviewedAt;

        @Min(value = 0, message = "Score must be at least 0")
        @Max(value = 100, message = "Score must not exceed 100")
        private Integer score;

        // Getters and setters
        public LocalDate getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDate reviewedAt) { this.reviewedAt = reviewedAt; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }

    @Data
    @SuperBuilder
    @EqualsAndHashCode(callSuper = true)
    public static class Response extends SetReviewDto {
    }

    public static class Summary {
        private UUID id;
        private Integer reviewNo;
        private LocalDate reviewedAt;
        private Integer score;

        // Getters and setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public Integer getReviewNo() { return reviewNo; }
        public void setReviewNo(Integer reviewNo) { this.reviewNo = reviewNo; }
        public LocalDate getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(LocalDate reviewedAt) { this.reviewedAt = reviewedAt; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }
} 