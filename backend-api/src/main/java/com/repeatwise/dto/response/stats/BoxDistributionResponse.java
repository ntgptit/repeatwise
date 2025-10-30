package com.repeatwise.dto.response.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Response DTO for box distribution statistics
 *
 * Requirements:
 * - UC-032: View Box Distribution
 * - API: GET /api/stats/box-distribution
 *
 * Response Format:
 * {
 *   "box_distribution": {
 *     "1": 150,
 *     "2": 120,
 *     "3": 100,
 *     "4": 80,
 *     "5": 60,
 *     "6": 40,
 *     "7": 20
 *   },
 *   "total_cards": 570
 * }
 *
 * @author RepeatWise Team
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoxDistributionResponse {

    /**
     * Map of box number to card count
     * Key: Box number (1-7)
     * Value: Number of cards in that box
     */
    private Map<Integer, Long> boxDistribution;

    /**
     * Total cards counted
     */
    private Integer totalCards;
}

