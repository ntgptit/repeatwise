package com.repeatwise.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    private Set set;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remind_schedule_id")
    private RemindSchedule remindSchedule;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // REVIEW_DUE, CYCLE_COMPLETED, SET_MASTERED, etc.

    @Column(name = "priority", nullable = false, length = 20)
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Override
    protected void onPrePersist() {
        if (isRead == null) {
            isRead = false;
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
    }
} 
