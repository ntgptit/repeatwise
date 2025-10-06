package com.spacedlearning.entity;

import java.time.LocalDate;

import com.spacedlearning.entity.enums.ReviewStatus;
import com.spacedlearning.entity.enums.SkipReason;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ReviewHistory entity representing history of review sessions and scores
 * Maps to the 'review_histories' table in the database
 */
@Entity
@Table(name = "review_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReviewHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private LearningCycle learningCycle;

    @Min(value = 1, message = "Review number must be at least 1")
    @Max(value = 5, message = "Review number must be at most 5")
    @Column(name = "review_number", nullable = false)
    @ToString.Include
    private Integer reviewNumber;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score must be at most 100")
    @Column(name = "score")
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.COMPLETED;

    @Enumerated(EnumType.STRING)
    @Column(name = "skip_reason", length = 20)
    private SkipReason skipReason;

    @NotNull
    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper methods
    public void setLearningSet(LearningSet learningSet) {
        this.learningSet = learningSet;
        if (learningSet != null && !learningSet.getReviewHistories().contains(this)) {
            learningSet.addReviewHistory(this);
        }
    }

    public void setLearningCycle(LearningCycle learningCycle) {
        this.learningCycle = learningCycle;
        if (learningCycle != null && !learningCycle.getReviewHistories().contains(this)) {
            learningCycle.addReviewHistory(this);
        }
    }

    /**
     * Check if review is completed
     */
    public boolean isCompleted() {
        return status == ReviewStatus.COMPLETED;
    }

    /**
     * Check if review is skipped
     */
    public boolean isSkipped() {
        return status == ReviewStatus.SKIPPED;
    }

    /**
     * Check if review has a score
     */
    public boolean hasScore() {
        return score != null;
    }

    /**
     * Get score or 0 if null
     */
    public int getScoreOrDefault() {
        return score != null ? score : 0;
    }

    /**
     * Validate that skip reason is provided when status is skipped
     */
    public boolean isValidSkipReason() {
        if (status == ReviewStatus.SKIPPED) {
            return skipReason != null;
        }
        return skipReason == null;
    }
}