package com.spacedlearning.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.CycleStatus;
import com.spacedlearning.entity.enums.ReviewStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * LearningCycle entity representing learning cycles for spaced repetition
 * Maps to the 'learning_cycles' table in the database
 */
@Entity
@Table(name = "learning_cycles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class LearningCycle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;

    @Min(value = 1, message = "Cycle number must be positive")
    @Column(name = "cycle_number", nullable = false)
    @ToString.Include
    private Integer cycleNumber;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @DecimalMin(value = "0.0", message = "Average score must be at least 0")
    @DecimalMax(value = "100.0", message = "Average score must be at most 100")
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private CycleStatus status = CycleStatus.ACTIVE;

    @Min(value = 7, message = "Next cycle delay must be at least 7 days")
    @Max(value = 90, message = "Next cycle delay must be at most 90 days")
    @Column(name = "next_cycle_delay_days")
    private Integer nextCycleDelayDays;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "learningCycle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewHistory> reviewHistories = new ArrayList<>();

    // Helper methods
    public void setLearningSet(LearningSet learningSet) {
        this.learningSet = learningSet;
        if (learningSet != null && !learningSet.getLearningCycles().contains(this)) {
            learningSet.addLearningCycle(this);
        }
    }

    public void addReviewHistory(ReviewHistory reviewHistory) {
        this.reviewHistories.add(reviewHistory);
        reviewHistory.setLearningCycle(this);
    }

    public void removeReviewHistory(ReviewHistory reviewHistory) {
        this.reviewHistories.remove(reviewHistory);
        reviewHistory.setLearningCycle(null);
    }

    /**
     * Check if cycle is active
     */
    public boolean isActive() {
        return status == CycleStatus.ACTIVE;
    }

    /**
     * Check if cycle is completed
     */
    public boolean isCompleted() {
        return status == CycleStatus.COMPLETED;
    }

    /**
     * Check if cycle is paused
     */
    public boolean isPaused() {
        return status == CycleStatus.PAUSED;
    }

    /**
     * Calculate and update average score based on review histories
     */
    public void updateAverageScore() {
        if (reviewHistories.isEmpty()) {
            this.averageScore = null;
            return;
        }

        double totalScore = reviewHistories.stream()
                .filter(rh -> rh.getScore() != null)
                .mapToInt(ReviewHistory::getScore)
                .average()
                .orElse(0.0);

        this.averageScore = BigDecimal.valueOf(totalScore);
    }

    /**
     * Get the number of completed reviews
     */
    public long getCompletedReviewsCount() {
        return reviewHistories.stream()
                .filter(rh -> rh.getStatus() == ReviewStatus.COMPLETED)
                .count();
    }

    /**
     * Get the number of skipped reviews
     */
    public long getSkippedReviewsCount() {
        return reviewHistories.stream()
                .filter(rh -> rh.getStatus() == ReviewStatus.SKIPPED)
                .count();
    }
}
