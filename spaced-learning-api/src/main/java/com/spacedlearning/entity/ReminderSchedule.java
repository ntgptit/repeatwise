package com.spacedlearning.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.spacedlearning.entity.enums.ReminderStatus;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ReminderSchedule entity representing scheduled reminders for learning sessions
 * Maps to the 'reminder_schedules' table in the database
 */
@Entity
@Table(name = "reminder_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class ReminderSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;

    @NotNull
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @NotNull
    @Column(name = "reminder_time", nullable = false)
    private LocalTime reminderTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.PENDING;

    @Column(name = "sent_at")
    private java.time.LocalDateTime sentAt;

    @Min(value = 0, message = "Reschedule count must be non-negative")
    @Max(value = 2, message = "Reschedule count must be at most 2")
    @Column(name = "reschedule_count", nullable = false)
    @Builder.Default
    private Integer rescheduleCount = 0;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getReminderSchedules().contains(this)) {
            user.addReminderSchedule(this);
        }
    }

    public void setLearningSet(LearningSet learningSet) {
        this.learningSet = learningSet;
        if (learningSet != null && !learningSet.getReminderSchedules().contains(this)) {
            learningSet.addReminderSchedule(this);
        }
    }

    /**
     * Check if reminder is pending
     */
    public boolean isPending() {
        return status == ReminderStatus.PENDING;
    }

    /**
     * Check if reminder is sent
     */
    public boolean isSent() {
        return status == ReminderStatus.SENT;
    }

    /**
     * Check if reminder is cancelled
     */
    public boolean isCancelled() {
        return status == ReminderStatus.CANCELLED;
    }

    /**
     * Check if reminder is done
     */
    public boolean isDone() {
        return status == ReminderStatus.DONE;
    }

    /**
     * Check if reminder is skipped
     */
    public boolean isSkipped() {
        return status == ReminderStatus.SKIPPED;
    }

    /**
     * Check if reminder is rescheduled
     */
    public boolean isRescheduled() {
        return status == ReminderStatus.RESCHEDULED;
    }

    /**
     * Check if reminder can be rescheduled
     */
    public boolean canBeRescheduled() {
        return rescheduleCount < 2;
    }

    /**
     * Mark reminder as sent
     */
    public void markAsSent() {
        this.status = ReminderStatus.SENT;
        this.sentAt = java.time.LocalDateTime.now();
    }

    /**
     * Mark reminder as done
     */
    public void markAsDone() {
        this.status = ReminderStatus.DONE;
    }

    /**
     * Mark reminder as skipped
     */
    public void markAsSkipped() {
        this.status = ReminderStatus.SKIPPED;
    }

    /**
     * Mark reminder as cancelled
     */
    public void markAsCancelled() {
        this.status = ReminderStatus.CANCELLED;
    }

    /**
     * Reschedule reminder
     */
    public void reschedule(LocalDate newDate, LocalTime newTime) {
        if (canBeRescheduled()) {
            this.scheduledDate = newDate;
            this.reminderTime = newTime;
            this.status = ReminderStatus.RESCHEDULED;
            this.rescheduleCount++;
        } else {
            throw new IllegalStateException("Maximum reschedule count reached");
        }
    }
}
