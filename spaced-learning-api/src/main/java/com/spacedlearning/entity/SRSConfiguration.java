package com.spacedlearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "srs_configurations", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class SRSConfiguration extends BaseEntity {

    @Min(value = 1)
    @Column(name = "base_delay_days", nullable = false)
    @Builder.Default
    private Integer baseDelayDays = 30;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "penalty_factor", nullable = false)
    @Builder.Default
    private Double penaltyFactor = 0.2;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Column(name = "scaling_factor", nullable = false)
    @Builder.Default
    private Double scalingFactor = 0.02;

    @Min(value = 1)
    @Column(name = "min_delay_days", nullable = false)
    @Builder.Default
    private Integer minDelayDays = 7;

    @Min(value = 1)
    @Column(name = "max_delay_days", nullable = false)
    @Builder.Default
    private Integer maxDelayDays = 90;

    @Min(value = 0)
    @Column(name = "low_score_threshold", nullable = false)
    @Builder.Default
    private Integer lowScoreThreshold = 40;

    @Min(value = 1)
    @Column(name = "max_sets_per_day", nullable = false)
    @Builder.Default
    private Integer maxSetsPerDay = 3;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "description", length = 500)
    private String description;

    // Business methods
    public int calculateNextCycleDelay(double avgScore, int wordCount) {
        if (avgScore < lowScoreThreshold) {
            return minDelayDays;
        }

        double delay = baseDelayDays - penaltyFactor * (100 - avgScore) + scalingFactor * wordCount;
        int roundedDelay = (int) Math.round(delay);
        
        return Math.max(minDelayDays, Math.min(maxDelayDays, roundedDelay));
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
