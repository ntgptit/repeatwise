package com.spacedlearning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "srs_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class SRSConfiguration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "review_intervals", columnDefinition = "TEXT")
    private String reviewIntervals; // JSON array of intervals

    @Column(name = "score_thresholds", columnDefinition = "TEXT")
    private String scoreThresholds; // JSON array of thresholds

    @Column(name = "cycle_delays", columnDefinition = "TEXT")
    private String cycleDelays; // JSON array of delays

    @Min(value = 1)
    @Column(name = "max_cycle_delay", nullable = false)
    @Builder.Default
    private Integer maxCycleDelay = 90;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "description", length = 500)
    private String description;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.isActive);
    }
}
