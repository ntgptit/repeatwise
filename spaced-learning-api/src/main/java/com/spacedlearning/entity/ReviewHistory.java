package com.spacedlearning.entity;

import com.spacedlearning.entity.enums.ReviewStatus;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "review_histories", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReviewHistory extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    @ToString.Include
    private LearningSet set;

    @NotNull
    @Column(name = "cycle_no", nullable = false)
    private Integer cycleNo;

    @NotNull
    @Column(name = "review_no", nullable = false)
    private Integer reviewNo;

    @Min(value = 0)
    @Max(value = 100)
    @Column(name = "score")
    private Integer score;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.COMPLETED;

    @Column(name = "note", length = 500)
    private String note;

    // Business methods
    public boolean isCompleted() {
        return ReviewStatus.COMPLETED.equals(this.status);
    }

    public boolean isSkipped() {
        return ReviewStatus.SKIPPED.equals(this.status);
    }

    public void markAsSkipped(String reason) {
        this.status = ReviewStatus.SKIPPED;
        this.note = reason;
    }

    public void updateScore(Integer newScore) {
        this.score = newScore;
        this.status = ReviewStatus.COMPLETED;
    }

    public void setSet(LearningSet set) {
        this.set = set;
    }
}
