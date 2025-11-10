package com.repeatwise.entity;

import java.time.LocalDate;

import com.repeatwise.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotNull(message = "{error.userstats.user.required}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.totalcards.non.negative}")
    @Column(name = "total_cards", nullable = false)
    private Integer totalCards = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.totaldecks.non.negative}")
    @Column(name = "total_decks", nullable = false)
    private Integer totalDecks = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.totalfolders.non.negative}")
    @Column(name = "total_folders", nullable = false)
    private Integer totalFolders = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.cardsreviewed.non.negative}")
    @Column(name = "cards_reviewed_today", nullable = false)
    private Integer cardsReviewedToday = 0;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.streakdays.non.negative}")
    @Column(name = "streak_days", nullable = false)
    private Integer streakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @Builder.Default
    @Min(value = 0, message = "{error.userstats.totaltime.non.negative}")
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
        final var today = LocalDate.now();

        if (this.lastStudyDate == null) {
            // First study session
            this.streakDays = 1;
        } else if (this.lastStudyDate.equals(today)) {
            // Already studied today, no change
            return;
        } else if (this.lastStudyDate.equals(today.minusDays(1))) {
            // Studied yesterday, increment streak
            this.streakDays++;
        } else {
            // Streak broken, reset to 1
            this.streakDays = 1;
        }

        this.lastStudyDate = today;
    }

    /**
     * Reset daily counters (should be called at start of new day)
     */
    public void resetDailyCounters() {
        final var today = LocalDate.now();
        if ((this.lastStudyDate != null) && !this.lastStudyDate.equals(today)) {
            this.cardsReviewedToday = 0;
        }
    }

    /**
     * Increment cards reviewed today
     */
    public void incrementCardsReviewedToday() {
        this.cardsReviewedToday++;
    }

    /**
     * Add study time in minutes
     */
    public void addStudyTime(int minutes) {
        this.totalStudyTimeMinutes += minutes;
    }
}
