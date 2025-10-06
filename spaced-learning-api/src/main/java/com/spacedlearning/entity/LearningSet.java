package com.spacedlearning.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;

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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
 * LearningSet entity representing learning sets containing vocabulary/grammar items
 * Maps to the 'learning_sets' table in the database
 */
@Entity
@Table(name = "learning_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class LearningSet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    @ToString.Include
    private String name;

    @Size(max = 500)
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    @Builder.Default
    private SetCategory category = SetCategory.VOCABULARY;

    @Min(value = 0, message = "Word count must be non-negative")
    @Column(name = "word_count", nullable = false)
    @Builder.Default
    private Integer wordCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private SetStatus status = SetStatus.NOT_STARTED;

    @Min(value = 1, message = "Current cycle must be positive")
    @Column(name = "current_cycle", nullable = false)
    @Builder.Default
    private Integer currentCycle = 1;

    @Min(value = 0, message = "Total reviews must be non-negative")
    @Column(name = "total_reviews", nullable = false)
    @Builder.Default
    private Integer totalReviews = 0;

    @DecimalMin(value = "0.0", message = "Average score must be at least 0")
    @DecimalMax(value = "100.0", message = "Average score must be at most 100")
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "learningSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetItem> setItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "learningSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningCycle> learningCycles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "learningSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewHistory> reviewHistories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "learningSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReminderSchedule> reminderSchedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "learningSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistics> statistics = new ArrayList<>();

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getLearningSets().contains(this)) {
            user.addLearningSet(this);
        }
    }

    public void addSetItem(SetItem setItem) {
        this.setItems.add(setItem);
        setItem.setLearningSet(this);
    }

    public void removeSetItem(SetItem setItem) {
        this.setItems.remove(setItem);
        setItem.setLearningSet(null);
    }

    public void addLearningCycle(LearningCycle learningCycle) {
        this.learningCycles.add(learningCycle);
        learningCycle.setLearningSet(this);
    }

    public void removeLearningCycle(LearningCycle learningCycle) {
        this.learningCycles.remove(learningCycle);
        learningCycle.setLearningSet(null);
    }

    public void addReviewHistory(ReviewHistory reviewHistory) {
        this.reviewHistories.add(reviewHistory);
        reviewHistory.setLearningSet(this);
    }

    public void removeReviewHistory(ReviewHistory reviewHistory) {
        this.reviewHistories.remove(reviewHistory);
        reviewHistory.setLearningSet(null);
    }

    public void addReminderSchedule(ReminderSchedule reminderSchedule) {
        this.reminderSchedules.add(reminderSchedule);
        reminderSchedule.setLearningSet(this);
    }

    public void removeReminderSchedule(ReminderSchedule reminderSchedule) {
        this.reminderSchedules.remove(reminderSchedule);
        reminderSchedule.setLearningSet(null);
    }

    public void addStatistics(Statistics stat) {
        this.statistics.add(stat);
        stat.setLearningSet(this);
    }

    public void removeStatistics(Statistics stat) {
        this.statistics.remove(stat);
        stat.setLearningSet(null);
    }

    /**
     * Check if set is not started
     */
    public boolean isNotStarted() {
        return status == SetStatus.NOT_STARTED;
    }

    /**
     * Check if set is in learning phase
     */
    public boolean isLearning() {
        return status == SetStatus.LEARNING;
    }

    /**
     * Check if set is in reviewing phase
     */
    public boolean isReviewing() {
        return status == SetStatus.REVIEWING;
    }

    /**
     * Check if set is mastered
     */
    public boolean isMastered() {
        return status == SetStatus.MASTERED;
    }

    /**
     * Update word count based on set items
     */
    public void updateWordCount() {
        this.wordCount = this.setItems.size();
    }

    /**
     * Calculate and update average score
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
}