package com.repeatwise.entity;

import com.repeatwise.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * User Stats entity - User's progress and statistics
 *
 * Requirements:
 * - UC-023: View Statistics
 * - Database Schema: user_stats table
 *
 * Business Rules:
 * - One stats record per user (1:1 relationship)
 * - Created automatically on user registration with zeros
 * - Updated after each review (via domain events)
 *
 * @author RepeatWise Team
 */
@Entity
@Table(name = "user_stats", indexes = {
    @Index(name = "idx_user_stats_user", columnList = "user_id")
})
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats extends BaseEntity {

    @NotNull(message = "{error.user.required}")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotNull(message = "{error.userstats.totalcards.required}")
    @Min(value = 0, message = "{error.userstats.totalcards.non.negative}")
    @Column(name = "total_cards", nullable = false)
    @Builder.Default
    private Integer totalCards = 0;

    @NotNull(message = "{error.userstats.totaldecks.required}")
    @Min(value = 0, message = "{error.userstats.totaldecks.non.negative}")
    @Column(name = "total_decks", nullable = false)
    @Builder.Default
    private Integer totalDecks = 0;

    @NotNull(message = "{error.userstats.totalfolders.required}")
    @Min(value = 0, message = "{error.userstats.totalfolders.non.negative}")
    @Column(name = "total_folders", nullable = false)
    @Builder.Default
    private Integer totalFolders = 0;

    @NotNull(message = "{error.userstats.cardsreviewedtoday.required}")
    @Min(value = 0, message = "{error.userstats.cardsreviewedtoday.non.negative}")
    @Column(name = "cards_reviewed_today", nullable = false)
    @Builder.Default
    private Integer cardsReviewedToday = 0;

    @NotNull(message = "{error.userstats.streakdays.required}")
    @Min(value = 0, message = "{error.userstats.streakdays.non.negative}")
    @Column(name = "streak_days", nullable = false)
    @Builder.Default
    private Integer streakDays = 0;

    @Column(name = "last_study_date")
    private LocalDate lastStudyDate;

    @NotNull(message = "{error.userstats.totaltime.required}")
    @Min(value = 0, message = "{error.userstats.totaltime.non.negative}")
    @Column(name = "total_study_time_minutes", nullable = false)
    @Builder.Default
    private Integer totalStudyTimeMinutes = 0;

    /**
     * Business method: Create default stats for a user
     */
    public static UserStats createDefault(final User user) {
        return UserStats.builder()
            .user(user)
            .totalCards(0)
            .totalDecks(0)
            .totalFolders(0)
            .cardsReviewedToday(0)
            .streakDays(0)
            .lastStudyDate(null)
            .totalStudyTimeMinutes(0)
            .build();
    }

    /**
     * Business method: Update streak after review
     */
    public void updateStreak() {
        final LocalDate today = LocalDate.now();

        if (lastStudyDate == null) {
            // First study
            streakDays = 1;
        } else if (lastStudyDate.equals(today)) {
            // Already studied today, no change
            return;
        } else if (lastStudyDate.equals(today.minusDays(1))) {
            // Consecutive day
            streakDays++;
        } else {
            // Streak broken
            streakDays = 1;
        }

        lastStudyDate = today;
    }

    /**
     * Business method: Check if streak is active
     */
    public boolean isStreakActive() {
        if (lastStudyDate == null) {
            return false;
        }
        final LocalDate today = LocalDate.now();
        return lastStudyDate.equals(today) || lastStudyDate.equals(today.minusDays(1));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserStats)) {
            return false;
        }
        final UserStats that = (UserStats) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserStats{" +
                "id=" + getId() +
                ", userId=" + (user != null ? user.getId() : null) +
                ", totalCards=" + totalCards +
                ", totalDecks=" + totalDecks +
                ", totalFolders=" + totalFolders +
                ", cardsReviewedToday=" + cardsReviewedToday +
                ", streakDays=" + streakDays +
                ", lastStudyDate=" + lastStudyDate +
                '}';
    }
}
