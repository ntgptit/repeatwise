package com.spacedlearning.entity;

import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.SetCategory;
import com.spacedlearning.entity.enums.SetStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

@Entity
@Table(name = "sets", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class LearningSet extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    @ToString.Include
    private String name;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 20, nullable = false)
    @Builder.Default
    private SetCategory category = SetCategory.OTHER;

    @NotNull
    @Min(value = 1, message = "Word count must be greater than 0")
    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private SetStatus status = SetStatus.NOT_STARTED;

    @NotNull
    @Min(value = 1)
    @Column(name = "current_cycle", nullable = false)
    @Builder.Default
    private Integer currentCycle = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Include
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "set", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<ReviewHistory> reviewHistories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "set", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL)
    private List<RemindSchedule> remindSchedules = new ArrayList<>();

    // Business methods
    public void startLearning() {
        this.status = SetStatus.LEARNING;
    }

    public void startReviewing() {
        this.status = SetStatus.REVIEWING;
    }

    public void markAsMastered() {
        this.status = SetStatus.MASTERED;
    }

    public void incrementCycle() {
        this.currentCycle++;
    }

    public void addReviewHistory(ReviewHistory reviewHistory) {
        this.reviewHistories.add(reviewHistory);
        reviewHistory.setSet(this);
    }

    public void addRemindSchedule(RemindSchedule remindSchedule) {
        this.remindSchedules.add(remindSchedule);
        remindSchedule.setSet(this);
    }
}
