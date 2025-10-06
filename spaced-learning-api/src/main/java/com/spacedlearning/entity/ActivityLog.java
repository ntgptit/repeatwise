package com.spacedlearning.entity;

import java.util.UUID;

import com.spacedlearning.entity.enums.ActionType;
import com.spacedlearning.entity.enums.EntityType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ActivityLog entity representing audit trail of user activities
 * Maps to the 'activity_logs' table in the database
 */
@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class ActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 50, nullable = false)
    @NotNull
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", length = 50, nullable = false)
    @NotNull
    private EntityType entityType;

    @Column(name = "entity_id", nullable = false)
    @NotNull
    private UUID entityId;

    @Column(name = "old_values", columnDefinition = "JSONB")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "JSONB")
    private String newValues;

    @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$", 
             message = "IP address must be in valid IPv4 format")
    @Size(max = 45)
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // Helper methods
    public void setUser(User user) {
        this.user = user;
        if (user != null && !user.getActivityLogs().contains(this)) {
            user.addActivityLog(this);
        }
    }

    /**
     * Check if this is a create action
     */
    public boolean isCreateAction() {
        return actionType == ActionType.CREATE;
    }

    /**
     * Check if this is an update action
     */
    public boolean isUpdateAction() {
        return actionType == ActionType.UPDATE;
    }

    /**
     * Check if this is a delete action
     */
    public boolean isDeleteAction() {
        return actionType == ActionType.DELETE;
    }

    /**
     * Check if this is a login action
     */
    public boolean isLoginAction() {
        return actionType == ActionType.LOGIN;
    }

    /**
     * Check if this is a logout action
     */
    public boolean isLogoutAction() {
        return actionType == ActionType.LOGOUT;
    }

    /**
     * Check if this is a review completion action
     */
    public boolean isReviewCompletionAction() {
        return actionType == ActionType.COMPLETE_REVIEW;
    }

    /**
     * Check if this is a review skip action
     */
    public boolean isReviewSkipAction() {
        return actionType == ActionType.SKIP_REVIEW;
    }

    /**
     * Check if this is a reschedule action
     */
    public boolean isRescheduleAction() {
        return actionType == ActionType.RESCHEDULE;
    }
}