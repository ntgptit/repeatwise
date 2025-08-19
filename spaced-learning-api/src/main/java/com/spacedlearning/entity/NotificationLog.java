package com.spacedlearning.entity;

import com.spacedlearning.entity.enums.NotificationStatus;
import com.spacedlearning.entity.enums.NotificationType;

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
@Table(name = "notification_logs", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class NotificationLog extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Include
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id")
    private LearningSet set;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remind_schedule_id")
    private RemindSchedule remindSchedule;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 30, nullable = false)
    private NotificationType notificationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "recipient", length = 200)
    private String recipient;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "sent_at")
    private java.time.LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private java.time.LocalDateTime deliveredAt;

    // Business methods
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = java.time.LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = java.time.LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    public boolean isPending() {
        return NotificationStatus.PENDING.equals(this.status);
    }

    public boolean isSent() {
        return NotificationStatus.SENT.equals(this.status);
    }

    public boolean isDelivered() {
        return NotificationStatus.DELIVERED.equals(this.status);
    }

    public boolean isFailed() {
        return NotificationStatus.FAILED.equals(this.status);
    }

    public void setSet(LearningSet set) {
        this.set = set;
    }
}
