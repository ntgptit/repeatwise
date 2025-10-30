package com.repeatwise.dto.request.review;

import com.repeatwise.entity.enums.CardRating;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for rating a card during review
 *
 * Requirements:
 * - UC-024: Rate Card
 *
 * Request Body:
 * {
 *   "cardId": "uuid",
 *   "rating": "GOOD",
 *   "timeTakenMs": 5000
 * }
 *
 * @author RepeatWise Team
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSubmitRequest {

    /**
     * Card ID being rated
     */
    @NotNull(message = "{error.card.required}")
    private UUID cardId;

    /**
     * User rating (AGAIN, HARD, GOOD, EASY)
     */
    @NotNull(message = "{error.reviewlog.rating.required}")
    private CardRating rating;

    /**
     * Time taken in milliseconds (from showing Front to rating)
     */
    @Min(value = 0, message = "{error.reviewlog.timetaken.non.negative}")
    private Long timeTakenMs;
}

