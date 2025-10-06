package com.spacedlearning.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.spacedlearning.entity.enums.StatType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Statistics entity representing analytics and reporting data
 * Maps to the 'statistics' table in the database
 */
@Entity
@Table(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class Statistics extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    private LearningSet learningSet;

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_type", length = 50, nullable = false)
    @NotNull
    @ToString.Include
    private StatType statType;

    @NotNull
    @Column(name = "stat_date", nullable = false)
    @ToString.Include
    private LocalDate statDate;

    @DecimalMin(value = "0.0", message = "Stat value must be non-negative")
    @Column(name = "stat_value", precision = 10, scale = 2, nullable = false)
    @ToString.Include
    private BigDecimal statValue;

    @Column(name = "metadata", columnDefinition = "JSONB")
    private String metadata;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getStatistics().contains(this)) {
            user.addStatistics(this);
        }
    }

    public void setLearningSet(LearningSet learningSet) {
        this.learningSet = learningSet;
        if (learningSet != null && !learningSet.getStatistics().contains(this)) {
            learningSet.addStatistics(this);
        }
    }

    /**
     * Check if this is a user-level statistic (no specific set)
     */
    public boolean isUserLevelStatistic() {
        return learningSet == null;
    }

    /**
     * Check if this is a set-level statistic
     */
    public boolean isSetLevelStatistic() {
        return learningSet != null;
    }

    /**
     * Check if this is a daily review statistic
     */
    public boolean isDailyReviewStatistic() {
        return statType == StatType.DAILY_REVIEWS;
    }

    /**
     * Check if this is a weekly review statistic
     */
    public boolean isWeeklyReviewStatistic() {
        return statType == StatType.WEEKLY_REVIEWS;
    }

    /**
     * Check if this is a monthly review statistic
     */
    public boolean isMonthlyReviewStatistic() {
        return statType == StatType.MONTHLY_REVIEWS;
    }

    /**
     * Check if this is an average score statistic
     */
    public boolean isAverageScoreStatistic() {
        return statType == StatType.AVERAGE_SCORE;
    }

    /**
     * Check if this is a learning streak statistic
     */
    public boolean isLearningStreakStatistic() {
        return statType == StatType.LEARNING_STREAK;
    }

    /**
     * Check if this is a review accuracy statistic
     */
    public boolean isReviewAccuracyStatistic() {
        return statType == StatType.REVIEW_ACCURACY;
    }

    /**
     * Get stat value as integer
     */
    public int getStatValueAsInt() {
        return statValue != null ? statValue.intValue() : 0;
    }

    /**
     * Get stat value as double
     */
    public double getStatValueAsDouble() {
        return statValue != null ? statValue.doubleValue() : 0.0;
    }
}
