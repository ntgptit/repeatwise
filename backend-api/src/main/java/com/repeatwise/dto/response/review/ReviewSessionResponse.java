package com.repeatwise.dto.response.review;

import java.util.List;

import com.repeatwise.dto.response.card.CardWithProgressResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for review session response (list of cards to review)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSessionResponse {

    private Integer totalDueCards;
    private Integer cardsInSession;
    private List<CardWithProgressResponse> cards;
}
