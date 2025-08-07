package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.repeatwise.enums.RemindStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "remind_schedules")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemindSchedule extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RemindStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescheduled_by")
    private User rescheduledBy;

    @Column(name = "rescheduled_at")
    private LocalDateTime rescheduledAt;

    @Column(name = "reschedule_reason", columnDefinition = "TEXT")
    private String rescheduleReason;

    @Column(name = "review_score")
    private Integer reviewScore; // Score from 0-100 when user completes review

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes; // Optional notes from user

    @Override
    protected void onPrePersist() {
        if (status == null) {
            status = RemindStatus.PENDING;
        }
    }
} 
