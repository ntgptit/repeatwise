package com.repeatwise.dto.response.stats;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user statistics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    private Integer totalCards;
    private Integer totalDecks;
    private Integer totalFolders;
    private Integer cardsReviewedToday;
    private Integer streakDays;
    private LocalDate lastStudyDate;
    private Integer totalStudyTimeMinutes;
}
