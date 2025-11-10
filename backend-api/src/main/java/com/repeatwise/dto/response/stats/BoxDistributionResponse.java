package com.repeatwise.dto.response.stats;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for box distribution statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxDistributionResponse {

    private Map<Integer, Integer> distribution; // box number -> card count
    private Integer totalCards;
    private Integer matureCards; // cards in box 5+
    private Integer newCards; // cards never reviewed
}
