package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * User Statistics entity - denormalized statistics for performance
 */
@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats extends BaseEntity {

    @NotNull(message = "User is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Min(value = 0, message = "Total cards must be at least 0")
    @Column(name = "total_cards", nullable = false)
    private Integer totalCards = 0;

    @Builder.Default
    @Min(value = 0, message = "Total decks must be at least 0")
    @Column(name = "total_decks", nullable = false)
    private Integer totalDecks = 0;

    @Builder.Default
    @Min(value = 0, message = "Total folders must be at least 0")
    @Column(name = "total_folders", nullable = false)
    private Integer totalFolders = 0;

    @Builder.Default
    @Min(value = 0, message = "Cards reviewed today must be at least 0")
    @Column(name = "cards_reviewed_today", nullable = false)
    private Integer cardsReviewedToday = 0;

    @Builder.Default
    @Min(value = 0, message = "Streak days must be at least 0")
    @Column(name = "streak_days", nullable = false)
    private Integer streakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Builder.Default
    @Min(value = 0, message = "Total study time must be at least 0")
    @Column(name = "total_study_time_minutes", nullable = false)
    private Integer totalStudyTimeMinutes = 0;

    /**
     * Create default user statistics
     */
    public static UserStats createDefault(User user) {
        return UserStats.builder()
                .user(user)
                .totalCards(0)
                .totalDecks(0)
                .totalFolders(0)
                .cardsReviewedToday(0)
                .streakDays(0)
                .totalStudyTimeMinutes(0)
                .build();
    }

    /**
     * Update study streak
     */
    public void updateStreak() {
        LocalDate today = LocalDate.now();

        if (lastStudyDate == null) {
            // First study session
            streakDays = 1;
        } else if (lastStudyDate.equals(today)) {
            // Already studied today, no change
            return;
        } else if (lastStudyDate.equals(today.minusDays(1))) {
            // Studied yesterday, increment streak
            streakDays++;
        } else {
            // Streak broken, reset to 1
            streakDays = 1;
        }

        lastStudyDate = today;
    }

    /**
     * Reset daily counters (should be called at start of new day)
     */
    public void resetDailyCounters() {
        LocalDate today = LocalDate.now();
        if (lastStudyDate != null && !lastStudyDate.equals(today)) {
            cardsReviewedToday = 0;
        }
    }

    /**
     * Increment cards reviewed today
     */
    public void incrementCardsReviewedToday() {
        cardsReviewedToday++;
    }

    /**
     * Add study time in minutes
     */
    public void addStudyTime(int minutes) {
        totalStudyTimeMinutes += minutes;
    }
}
