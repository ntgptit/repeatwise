package com.repeatwise.mapper;

import com.repeatwise.dto.response.stats.BoxDistributionResponse;
import com.repeatwise.dto.response.stats.UserStatsResponse;
import com.repeatwise.entity.CardBoxPosition;
import com.repeatwise.entity.UserStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stats Mapper - MapStruct mapper for Statistics entities and DTOs
 *
 * Requirements:
 * - Coding Convention: Use MapStruct for DTO mapping
 * - Clean separation between entity and DTO
 * - UC-031: View User Statistics
 * - UC-032: View Box Distribution
 *
 * @author RepeatWise Team
 */
@Mapper(componentModel = "spring")
public interface StatsMapper {

    // ==================== UserStats to Response ====================

    /**
     * Convert UserStats entity to UserStatsResponse DTO
     *
     * @param userStats UserStats entity
     * @return UserStatsResponse DTO
     */
    @Mapping(target = "userId", source = "user.id")
    UserStatsResponse toResponse(UserStats userStats);

    // ==================== Box Distribution ====================

    /**
     * Convert list of CardBoxPosition to BoxDistributionResponse
     * Groups cards by box number and counts them
     *
     * @param cardBoxPositions List of CardBoxPosition entities
     * @return BoxDistributionResponse DTO
     */
    default BoxDistributionResponse toBoxDistribution(final List<CardBoxPosition> cardBoxPositions) {
        if (cardBoxPositions == null || cardBoxPositions.isEmpty()) {
            return BoxDistributionResponse.builder()
                    .boxDistribution(java.util.Map.of())
                    .totalCards(0)
                    .build();
        }

        // Group by box and count
        final Map<Integer, Long> distribution = cardBoxPositions.stream()
                .collect(Collectors.groupingBy(
                        CardBoxPosition::getCurrentBox,
                        Collectors.counting()
                ));

        // Fill missing boxes with 0
        for (int box = 1; box <= 7; box++) {
            distribution.putIfAbsent(box, 0L);
        }

        final int totalCards = cardBoxPositions.size();

        return BoxDistributionResponse.builder()
                .boxDistribution(distribution)
                .totalCards(totalCards)
                .build();
    }
}

