package com.spacedlearning.entity;

import java.time.LocalDate;

import com.spacedlearning.entity.enums.RemindStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "remind_schedules", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class RemindSchedule extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    @ToString.Include
    private LearningSet set;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Include
    private User user;

    @NotNull
    @Column(name = "remind_date", nullable = false)
    private LocalDate remindDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private RemindStatus status = RemindStatus.PENDING;

    @Column(name = "reschedule_count")
    @Builder.Default
    private Integer rescheduleCount = 0;

    // Business methods
    public boolean isPending() {
        return RemindStatus.PENDING.equals(this.status);
    }

    public boolean isSent() {
        return RemindStatus.SENT.equals(this.status);
    }

    public boolean isDone() {
        return RemindStatus.DONE.equals(this.status);
    }

    public boolean isSkipped() {
        return RemindStatus.SKIPPED.equals(this.status);
    }

    public boolean isRescheduled() {
        return RemindStatus.RESCHEDULED.equals(this.status);
    }

    public boolean isCancelled() {
        return RemindStatus.CANCELLED.equals(this.status);
    }

    public void markAsSent() {
        this.status = RemindStatus.SENT;
    }

    public void markAsDone() {
        this.status = RemindStatus.DONE;
    }

    public void markAsSkipped() {
        this.status = RemindStatus.SKIPPED;
    }

    public void markAsCancelled() {
        this.status = RemindStatus.CANCELLED;
    }

    public boolean canReschedule() {
        return this.rescheduleCount < 2;
    }

    public void reschedule(LocalDate newDate) {
        if (canReschedule()) {
            this.remindDate = newDate;
            this.status = RemindStatus.RESCHEDULED;
            this.rescheduleCount++;
        }
    }

    public void setSet(LearningSet set) {
        this.set = set;
    }
}
