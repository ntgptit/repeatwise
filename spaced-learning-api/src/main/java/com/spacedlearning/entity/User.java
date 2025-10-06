package com.spacedlearning.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.spacedlearning.entity.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
 * User entity representing user accounts and authentication information
 * Maps to the 'users' table in the database
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class User extends BaseEntity {

    @Email
    @NotBlank
    @Size(max = 255)
    @Column(name = "email", unique = true, nullable = false)
    @ToString.Include
    private String email;

    @NotBlank
    @Size(min = 60, max = 255)
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(name = "full_name", nullable = false)
    @ToString.Include
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_language", length = 2, nullable = false)
    @Builder.Default
    private PreferredLanguage preferredLanguage = PreferredLanguage.VI;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^(Asia/Ho_Chi_Minh|UTC|America/New_York|Europe/London)$", 
             message = "Timezone must be one of: Asia/Ho_Chi_Minh, UTC, America/New_York, Europe/London")
    @Column(name = "timezone", nullable = false)
    @Builder.Default
    private String timezone = "Asia/Ho_Chi_Minh";

    @NotNull
    @Column(name = "default_reminder_time", nullable = false)
    @Builder.Default
    private LocalTime defaultReminderTime = LocalTime.of(9, 0);

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    // Relationships
    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserProfile> profiles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSettings> settings = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningSet> learningSets = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReminderSchedule> reminderSchedules = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityLog> activityLogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistics> statistics = new ArrayList<>();

    // Helper methods
    public void addProfile(UserProfile profile) {
        this.profiles.add(profile);
        profile.setUser(this);
    }

    public void removeProfile(UserProfile profile) {
        this.profiles.remove(profile);
        profile.setUser(null);
    }

    public void addSettings(UserSettings userSettings) {
        this.settings.add(userSettings);
        userSettings.setUser(this);
    }

    public void removeSettings(UserSettings userSettings) {
        this.settings.remove(userSettings);
        userSettings.setUser(null);
    }

    public void addLearningSet(LearningSet learningSet) {
        this.learningSets.add(learningSet);
        learningSet.setUser(this);
    }

    public void removeLearningSet(LearningSet learningSet) {
        this.learningSets.remove(learningSet);
        learningSet.setUser(null);
    }

    public void addReminderSchedule(ReminderSchedule reminderSchedule) {
        this.reminderSchedules.add(reminderSchedule);
        reminderSchedule.setUser(this);
    }

    public void removeReminderSchedule(ReminderSchedule reminderSchedule) {
        this.reminderSchedules.remove(reminderSchedule);
        reminderSchedule.setUser(null);
    }

    public void addActivityLog(ActivityLog activityLog) {
        this.activityLogs.add(activityLog);
        activityLog.setUser(this);
    }

    public void addStatistics(Statistics stat) {
        this.statistics.add(stat);
        stat.setUser(this);
    }

    public void removeStatistics(Statistics stat) {
        this.statistics.remove(stat);
        stat.setUser(null);
    }

    /**
     * Check if user is active
     */
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Check if user is suspended
     */
    public boolean isSuspended() {
        return status == UserStatus.SUSPENDED;
    }

    /**
     * Check if user is inactive
     */
    public boolean isInactive() {
        return status == UserStatus.INACTIVE;
    }

    /**
     * Enum for preferred language
     */
    public enum PreferredLanguage {
        VI, EN
    }
}