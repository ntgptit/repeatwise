package com.repeatwise.dto.response.stats;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for user statistics
 *
 * Requirements:
 * - UC-031: View User Statistics
 * - API: GET /api/stats/user
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsResponse {

    /**
     * User unique identifier
     */
    private UUID userId;

    /**
     * Total cards owned
     */
    @Builder.Default
    private Integer totalCards = 0;

    /**
     * Total decks owned
     */
    @Builder.Default
    private Integer totalDecks = 0;

    /**
     * Total folders owned
     */
    @Builder.Default
    private Integer totalFolders = 0;

    /**
     * Cards reviewed today
     */
    @Builder.Default
    private Integer cardsReviewedToday = 0;

    /**
     * Consecutive study days
     */
    @Builder.Default
    private Integer streakDays = 0;

    /**
     * Last study date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastStudyDate;

    /**
     * Total study time in minutes
     */
    @Builder.Default
    private Integer totalStudyTimeMinutes = 0;

    /**
     * Reviews count for past 7 days (array of 7 integers)
     * Index 0 = 7 days ago, Index 6 = today
     */
    private java.util.List<Integer> reviewsPast7Days;
}

